package com.dikkak.service;

import com.dikkak.common.BaseException;
import com.dikkak.dto.admin.GetProposalListRes;
import com.dikkak.dto.admin.GetUserProposalsRes;
import com.dikkak.dto.proposal.GetProposalRes;
import com.dikkak.dto.proposal.PostProposalReq;
import com.dikkak.dto.workspace.ClientWorkspaceRes;
import com.dikkak.dto.workspace.DesignerWorkspaceRes;
import com.dikkak.dto.workspace.DesignerWorkspaceRes.WorkInfo;
import com.dikkak.entity.StatusType;
import com.dikkak.entity.proposal.Keyword;
import com.dikkak.entity.proposal.Proposal;
import com.dikkak.entity.proposal.ProposalKeyword;
import com.dikkak.entity.user.User;
import com.dikkak.repository.coworking.CoworkingRepository;
import com.dikkak.repository.proposal.KeywordRepository;
import com.dikkak.repository.proposal.ProposalKeywordRepository;
import com.dikkak.repository.proposal.ProposalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.dikkak.common.ResponseMessage.WRONG_PROPOSAL_ID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProposalService {
    private final ProposalRepository proposalRepository;
    private final ProposalKeywordRepository proposalKeywordRepository;
    private final KeywordRepository keywordRepository;
    private final ReferenceService referenceService;
    private final OtherFileService otherFileService;
    private final CoworkingRepository coworkingRepository;


    // 클라이언트 작업실 목록 조회
    public List<ClientWorkspaceRes> getClientWorkspace(Long userId) {
        return proposalRepository.getClientWorkspace(userId);
    }

    // 디자이너 작업실 목록 조회
    public DesignerWorkspaceRes getDesignerWorkspace(Long designerId){
        DesignerWorkspaceRes res = new DesignerWorkspaceRes();
        for (WorkInfo workInfo : proposalRepository.getDesignerWorkspace(designerId)) {
            if (workInfo.isComplete()){ // 완료된 작업
                res.getComplete().add(workInfo);
            } else {
                res.getProgress().add(workInfo);
            }
        }
        return res;
    }

    @Transactional
    public Proposal create(User client, PostProposalReq req) {
        // 제안서 저장
        return proposalRepository.save(new Proposal(client, req));
    }

    @Transactional
    public void saveKeyword(Proposal proposal, String keywordName) {
        // 키워드 찾기 or 저장
        Keyword keyword = keywordRepository.findByName(keywordName).orElseGet(() -> keywordRepository.save(new Keyword(keywordName)));

        // 제안서와 키워드 매핑 정보 저장
        proposalKeywordRepository.save(
                ProposalKeyword
                        .builder()
                        .keyword(keyword)
                        .proposal(proposal)
                        .build()
        );
    }

    // 회원의 제안서 목록 조회
    public List<GetUserProposalsRes> getUserProposalList(Long userId) {
        return proposalRepository.findByClientIdAndStatus(userId, StatusType.ACTIVE ,Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream().map(GetUserProposalsRes::new).collect(Collectors.toList());
    }

    // 제안서 목록 조회
    public GetProposalListRes getProposalList(int page, int size) {
        Page<Proposal> proposalPage = proposalRepository.findAll(PageRequest.of(page, size, Sort.Direction.DESC, "createdAt"));
        return GetProposalListRes.builder()
                .totalPages(proposalPage.getTotalPages())
                .totalCount(proposalPage.getTotalElements())
                .page(proposalPage.getNumber())
                .size(proposalPage.getSize())
                .contents(proposalPage.getContent())
                .build();
    }

    public boolean existUserProposal(User designer, Long proposalId) {
        return coworkingRepository.findByProposalIdAndDesigner(proposalId, designer).isPresent();
    }

    // 제안서 조회
    public GetProposalRes getProposal(Long proposalId) {

        Proposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new BaseException(WRONG_PROPOSAL_ID));

        GetProposalRes res = new GetProposalRes(proposal);

        // reference file 조회
        referenceService.getRefList(proposalId).forEach(res::addReferenceFile);

        // etc file 조회
        otherFileService.getOtherFileList(proposalId).forEach(res::addEtcFile);

        // keyword 조회
        proposalKeywordRepository.findByProposalId(proposalId).forEach(proposalKeyword -> {
            res.getKeywords().add(proposalKeyword.getKeyword().getName());
        });
        return res;
    }

    // 제안서 목록 삭제
    @Transactional
    public long deleteProposalList(List<Long> proposalList, Long clientId) {
        return proposalRepository.updateProposalsInactive(clientId, proposalList);
    }

}
