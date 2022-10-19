package com.dikkak.service;

import com.dikkak.common.BaseException;
import com.dikkak.dto.coworking.GetChattingRes;
import com.dikkak.entity.coworking.Coworking;
import com.dikkak.entity.coworking.StepType;
import com.dikkak.entity.user.User;
import com.dikkak.entity.proposal.Proposal;
import com.dikkak.repository.coworking.CoworkingMessageRepository;
import com.dikkak.repository.coworking.CoworkingRepository;
import com.dikkak.repository.proposal.ProposalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.dikkak.common.ResponseMessage.*;
import static com.dikkak.common.ResponseMessage.DATABASE_ERROR;
import static com.dikkak.common.ResponseMessage.WRONG_PROPOSAL_ID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoworkingService {

    private final ProposalRepository proposalRepository;
    private final CoworkingRepository coworkingRepository;
    private final CoworkingMessageRepository messageRepository;

    // 외주작업실 생성, 디자이너 매칭
    @Transactional
    public Coworking create(User designer, Long proposalId) throws BaseException {
        try {
            // 제안서 조회
            Proposal proposal = proposalRepository.findById(proposalId)
                    .orElseThrow(() -> new BaseException(WRONG_PROPOSAL_ID));
            // 외주 작업실 생성
            return coworkingRepository.save(new Coworking(proposal, designer));
        } catch (BaseException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public Coworking getCoworking(Long coworkingId) throws BaseException{
        return coworkingRepository.findById(coworkingId).orElseThrow(() -> new BaseException(WRONG_COWORKING_ID));
    }

    // 채팅 목록 조회
    public List<GetChattingRes> getMessageList(Long coworkingId, StepType step) throws BaseException {
        try {
            return messageRepository.getCoworkingStepMessage(coworkingId, step);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
