package com.dikkak.service;

import com.dikkak.dto.admin.GetProposalsRes;
import com.dikkak.dto.common.BaseException;
import com.dikkak.dto.proposal.GetProposalRes;
import com.dikkak.dto.proposal.PostProposalReq;
import com.dikkak.dto.workplace.ClientWorkplaceRes;
import com.dikkak.entity.User;
import com.dikkak.entity.proposal.*;
import com.dikkak.entity.work.Coworking;
import com.dikkak.repository.CoworkingRepository;
import com.dikkak.repository.proposal.KeywordRepository;
import com.dikkak.repository.proposal.ProposalKeywordRepository;
import com.dikkak.repository.proposal.ProposalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.dikkak.dto.common.ResponseMessage.DATABASE_ERROR;
import static com.dikkak.dto.common.ResponseMessage.WRONG_PROPOSAL_ID;

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


    public List<ClientWorkplaceRes> getUserWorkplace(Long userId) throws BaseException {
        try {
            return proposalRepository.getClientWorkplace(userId);
        } catch (Exception e) {
            log.info(e.getMessage());
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
    public void create(User designer, Long proposalId) throws BaseException {
        try {
            // 제안서 조회
            Proposal proposal = proposalRepository.findById(proposalId)
                    .orElseThrow(() -> new BaseException(WRONG_PROPOSAL_ID));
            // 외주 작업실 저장
            coworkingRepository.save(new Coworking(proposal, designer));
        } catch (BaseException e) {
            log.error(e.getMessage());
            throw e;
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
    public List<GetProposalsRes> getProposalList(Long userId) {
        return proposalRepository.findByClientId(userId, Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream().map(GetProposalsRes::new).collect(Collectors.toList());
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
}
