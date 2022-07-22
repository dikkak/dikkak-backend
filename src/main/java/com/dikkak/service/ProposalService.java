package com.dikkak.service;

import com.dikkak.dto.admin.GetProposalsRes;
import com.dikkak.dto.common.BaseException;
import com.dikkak.dto.proposal.GetProposalRes;
import com.dikkak.dto.proposal.PostProposalReq;
import com.dikkak.dto.workplace.WorkplaceRes;
import com.dikkak.entity.User;
import com.dikkak.entity.proposal.*;
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

    // 제안서 조회
    public GetProposalRes getProposal(Long proposalId) throws BaseException {

        Proposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new BaseException(WRONG_PROPOSAL_ID));

        try {
            GetProposalRes res = new GetProposalRes(proposal);
            System.out.println("res = " + res);

            referenceService.getRefList(proposalId).forEach(reference -> {
                res.getReferenceFile().add(reference.getFileUrl());
                res.getReferenceDesc().add(reference.getDescription());
            });
            System.out.println("res = " + res);
            otherFileService.getOtherFileList(proposalId).forEach(otherfile -> {
                res.getEtcFile().add(otherfile.getFileUrl());
            });
            System.out.println("res = " + res);

            proposalKeywordRepository.findByProposalId(proposalId).forEach(proposalKeyword -> {
                res.getKeywords().add(proposalKeyword.getKeyword().getName());
            });
            System.out.println("res = " + res);
            return res;
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
