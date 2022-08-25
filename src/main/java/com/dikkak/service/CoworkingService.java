package com.dikkak.service;

import com.dikkak.dto.common.BaseException;
import com.dikkak.entity.user.User;
import com.dikkak.entity.proposal.Proposal;
import com.dikkak.entity.work.Coworking;
import com.dikkak.repository.CoworkingRepository;
import com.dikkak.repository.proposal.ProposalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.dikkak.dto.common.ResponseMessage.DATABASE_ERROR;
import static com.dikkak.dto.common.ResponseMessage.WRONG_PROPOSAL_ID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoworkingService {

    private final ProposalRepository proposalRepository;
    private final CoworkingRepository coworkingRepository;

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

}
