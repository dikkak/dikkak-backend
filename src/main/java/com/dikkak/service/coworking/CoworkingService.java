package com.dikkak.service.coworking;

import com.dikkak.common.BaseException;
import com.dikkak.entity.coworking.Coworking;
import com.dikkak.entity.proposal.Proposal;
import com.dikkak.entity.user.User;
import com.dikkak.repository.coworking.CoworkingRepository;
import com.dikkak.repository.proposal.ProposalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.dikkak.common.ResponseMessage.WRONG_COWORKING_ID;
import static com.dikkak.common.ResponseMessage.WRONG_PROPOSAL_ID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CoworkingService {

    private final ProposalRepository proposalRepository;
    private final CoworkingRepository coworkingRepository;

    // 외주작업실 생성, 디자이너 매칭
    @Transactional
    public Coworking create(User designer, Long proposalId) {
        // 제안서 조회
        Proposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new BaseException(WRONG_PROPOSAL_ID));
        // 외주 작업실 생성
        return coworkingRepository.save(new Coworking(proposal, designer));
    }

    public Coworking getCoworking(Long coworkingId) {
        return coworkingRepository.findWithProposalById(coworkingId)
                .orElseThrow(() -> new BaseException(WRONG_COWORKING_ID));
    }
}
