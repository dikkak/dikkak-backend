package com.dikkak.service;

import com.dikkak.common.BaseException;
import com.dikkak.dto.coworking.*;
import com.dikkak.entity.coworking.Coworking;
import com.dikkak.entity.coworking.CoworkingStep;
import com.dikkak.entity.coworking.StepType;
import com.dikkak.entity.proposal.Proposal;
import com.dikkak.entity.user.User;
import com.dikkak.repository.coworking.CoworkingRepository;
import com.dikkak.repository.coworking.CoworkingStepRepository;
import com.dikkak.repository.coworking.file.CoworkingFileRepository;
import com.dikkak.repository.coworking.message.CoworkingMessageRepository;
import com.dikkak.repository.coworking.task.CoworkingTaskRepository;
import com.dikkak.repository.proposal.ProposalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.dikkak.common.ResponseMessage.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoworkingService {

    private final ProposalRepository proposalRepository;
    private final CoworkingRepository coworkingRepository;
    private final CoworkingStepRepository stepRepository;
    private final CoworkingMessageRepository messageRepository;
    private final CoworkingTaskRepository taskRepository;
    private final CoworkingFileRepository fileRepository;

    // 외주작업실 생성, 디자이너 매칭
    @Transactional
    public Coworking create(User designer, Long proposalId) throws BaseException {
        try {
            // 제안서 조회
            Proposal proposal = proposalRepository.findById(proposalId)
                    .orElseThrow(() -> new BaseException(WRONG_PROPOSAL_ID));
            // 외주 작업실 생성
            Coworking coworking = coworkingRepository.save(new Coworking(proposal, designer));
            // step 생성
            stepRepository.save(
                    CoworkingStep
                            .builder()
                            .coworking(coworking)
                            .type(coworking.getProgress())
                            .build()
            );
            return coworking;

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
    public List<Message<GetChattingRes>> getMessageList(Long coworkingId, StepType step) throws BaseException {
        try {
            return messageRepository.getCoworkingStepMessage(coworkingId, step)
                    .stream().map(res ->
                            Message.<GetChattingRes>builder()
                                .type((res.getFileName() == null) ? MessageType.TEXT : MessageType.FILE)
                                .coworkingId(coworkingId)
                                .data(res)
                                .build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // task 목록 조회
    public List<Message<GetTaskRes>> getTaskList(Long coworkingId) throws BaseException {
        try {
            return taskRepository.getCoworkingTask(coworkingId)
                    .stream().map(res ->
                            Message.<GetTaskRes>builder()
                                    .type(MessageType.TASK)
                                    .coworkingId(coworkingId)
                                    .data(res)
                                    .build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // file 목록 조회
    public List<GetFileRes> getFileList(Long coworkingId, Pageable pageable) throws BaseException {
        try {
            return fileRepository.getFileList(coworkingId, pageable);
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


}
