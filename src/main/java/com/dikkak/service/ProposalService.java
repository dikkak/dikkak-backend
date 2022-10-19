package com.dikkak.service;

import com.dikkak.dto.admin.GetProposalListRes;
import com.dikkak.dto.admin.GetUserProposalsRes;
import com.dikkak.common.BaseException;
import com.dikkak.dto.proposal.GetProposalRes;
import com.dikkak.dto.proposal.PostProposalReq;
import com.dikkak.dto.workplace.ClientWorkplaceRes;
import com.dikkak.dto.workplace.DesignerWorkplaceRes;
import com.dikkak.dto.workplace.DesignerWorkplaceRes.WorkInfo;
import com.dikkak.entity.StatusType;
import com.dikkak.entity.coworking.StepType;
import com.dikkak.entity.user.User;
import com.dikkak.entity.proposal.*;
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

import static com.dikkak.common.ResponseMessage.DATABASE_ERROR;
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
    public List<ClientWorkplaceRes> getClientWorkplace(Long userId) throws BaseException {
        try {
            return proposalRepository.getClientWorkplace(userId);
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 디자이너 작업실 목록 조회
    public DesignerWorkplaceRes getDesignerWorkplace(Long designerId) throws BaseException{
        try {
            DesignerWorkplaceRes res = new DesignerWorkplaceRes();
            for (WorkInfo workInfo : proposalRepository.getDesignerWorkplace(designerId)) {
                if(workInfo.getCoworkingStep() == StepType.TERMINATION) // 완료된 작업
                    res.getComplete().add(workInfo);
                else res.getProgress().add(workInfo);
            }
            return res;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional
    public Proposal create(User client, PostProposalReq req) throws BaseException {
        try {
            // 제안서 저장
            return proposalRepository.save(new Proposal(client, req));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional
    public void saveKeyword(Proposal proposal, String keywordName) throws BaseException {
        try {
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

        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }

    }

    // 회원의 제안서 목록 조회
    public List<GetUserProposalsRes> getUserProposalList(Long userId) {
        return proposalRepository.findByClientIdAndStatus(userId, StatusType.ACTIVE ,Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream().map(GetUserProposalsRes::new).collect(Collectors.toList());
    }

    // 제안서 목록 조회
    public GetProposalListRes getProposalList(int page, int size) throws BaseException {
        try {
            Page<Proposal> proposalPage = proposalRepository.findAll(PageRequest.of(page, size, Sort.Direction.DESC, "createdAt"));
            return GetProposalListRes.builder()
                    .totalPages(proposalPage.getTotalPages())
                    .totalCount(proposalPage.getTotalElements())
                    .page(proposalPage.getNumber())
                    .size(proposalPage.getSize())
                    .contents(proposalPage.getContent())
                    .build();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public boolean existUserProposal(User designer, Long proposalId) {
        return coworkingRepository.findByProposalIdAndDesigner(proposalId, designer).isPresent();
    }

    // 제안서 조회
    public GetProposalRes getProposal(Long proposalId) throws BaseException {

        Proposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new BaseException(WRONG_PROPOSAL_ID));

        try {
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
        } catch (Exception e) {
            log.info(e.getMessage());
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 제안서 목록 삭제
    @Transactional
    public long deleteProposalList(List<Long> proposalList, Long clientId) throws BaseException {
        try {
            return proposalRepository.updateProposalsInactive(clientId, proposalList);
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }


}
