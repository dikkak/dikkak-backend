package com.dikkak.service;

import com.dikkak.dto.common.BaseException;
import com.dikkak.dto.proposal.PostProposalReq;
import com.dikkak.dto.workplace.WorkplaceRes;
import com.dikkak.entity.User;
import com.dikkak.entity.proposal.Keyword;
import com.dikkak.entity.proposal.Proposal;
import com.dikkak.entity.proposal.ProposalKeyword;
import com.dikkak.entity.proposal.UserProposal;
import com.dikkak.repository.proposal.KeywordRepository;
import com.dikkak.repository.proposal.ProposalKeywordRepository;
import com.dikkak.repository.proposal.ProposalRepository;
import com.dikkak.repository.proposal.UserProposalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.dikkak.dto.common.ResponseMessage.DATABASE_ERROR;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProposalService {

    private final UserProposalRepository userProposalRepository;
    private final ProposalRepository proposalRepository;
    private final ProposalKeywordRepository proposalKeywordRepository;
    private final KeywordRepository keywordRepository;



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
    public void saveKeyword(Proposal proposal, String keywordName) throws BaseException{
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


}
