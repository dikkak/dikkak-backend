package com.dikkak.service;

import com.dikkak.dto.admin.GetProposalsRes;
import com.dikkak.dto.common.BaseException;
import com.dikkak.dto.proposal.GetProposalRes;
import com.dikkak.dto.proposal.PostProposalReq;
import com.dikkak.dto.workplace.WorkplaceRes;
import com.dikkak.entity.User;
import com.dikkak.entity.proposal.*;
import com.dikkak.entity.work.Coworking;
import com.dikkak.repository.CoworkingRepository;
import com.dikkak.repository.proposal.KeywordRepository;
import com.dikkak.repository.proposal.ProposalKeywordRepository;
import com.dikkak.repository.proposal.ProposalRepository;
import com.dikkak.repository.proposal.UserProposalRepository;
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

    private final UserProposalRepository userProposalRepository;
    private final ProposalRepository proposalRepository;
    private final ProposalKeywordRepository proposalKeywordRepository;
    private final KeywordRepository keywordRepository;
    private final ReferenceService referenceService;
    private final OtherFileService otherFileService;
    private final CoworkingRepository coworkingRepository;


    public WorkplaceRes getUserWorkplace(Long userId) throws BaseException {
        try {
            WorkplaceRes workplaceRes = new WorkplaceRes();
            workplaceRes.setProposals(userProposalRepository.getByUserId(userId));
            return workplaceRes;
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional
    public Proposal create(User user, PostProposalReq req) throws BaseException {
        try {
            // 제안서 저장
            Proposal savedProposal = proposalRepository.save(new Proposal(req));

            // 회원과 매핑
            userProposalRepository.save(UserProposal.builder()
                    .user(user)
                    .proposal(savedProposal)
                    .build());
            return savedProposal;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional
    public void create(User user, Long proposalId) throws BaseException {
        try {
            // 제안서 조회
            Proposal proposal = proposalRepository.findById(proposalId).orElse(null);
            if(proposal == null)
                throw new BaseException(WRONG_PROPOSAL_ID);

            // 회원과 매핑
            UserProposal userProposal = userProposalRepository.save(UserProposal.builder()
                    .user(user)
                    .proposal(proposal)
                    .build());

            // 외주 작업실 저장
            coworkingRepository.save(new Coworking(userProposal));

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
        return userProposalRepository.findByUserId(userId, Sort.by(Sort.Direction.DESC, "createdAt")).stream()
                .map(userProposal -> new GetProposalsRes(userProposal.getProposal()))
                .collect(Collectors.toList());
    }

    public boolean existUserProposal(User user, Long proposalId) {
        return userProposalRepository.findByUserAndProposalId(user, proposalId).isPresent();
    }

    // 제안서 조회
    public GetProposalRes getProposal(Long proposalId) throws BaseException {

        Proposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new BaseException(WRONG_PROPOSAL_ID));

        try {
            String clientName = userProposalRepository.findClientByProposalId(proposalId);
            GetProposalRes res = new GetProposalRes(proposal, clientName);

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
