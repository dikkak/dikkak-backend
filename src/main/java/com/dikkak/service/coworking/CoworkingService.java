package com.dikkak.service.coworking;

import com.dikkak.common.BaseException;
import com.dikkak.dto.PageCustom;
import com.dikkak.dto.coworking.*;
import com.dikkak.entity.coworking.Coworking;
import com.dikkak.entity.proposal.Proposal;
import com.dikkak.entity.user.User;
import com.dikkak.repository.coworking.CoworkingRepository;
import com.dikkak.repository.coworking.file.CoworkingFileRepository;
import com.dikkak.repository.coworking.message.CoworkingMessageRepository;
import com.dikkak.repository.coworking.task.CoworkingTaskRepository;
import com.dikkak.repository.proposal.ProposalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.dikkak.common.ResponseMessage.WRONG_COWORKING_ID;
import static com.dikkak.common.ResponseMessage.WRONG_PROPOSAL_ID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoworkingService {

    private final ProposalRepository proposalRepository;
    private final CoworkingRepository coworkingRepository;
    private final CoworkingMessageRepository messageRepository;
    private final CoworkingTaskRepository taskRepository;
    private final CoworkingFileRepository fileRepository;

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

    // 채팅 목록 조회
    public PageCustom<Message<GetChattingRes>> getMessageList(Coworking coworking, Pageable pageable) {
        Page<GetChattingRes> messages = messageRepository.getCoworkingMessage(coworking, pageable);
        System.out.println(pageable);
        System.out.println(messages);
        return PageCustom.<Message<GetChattingRes>>builder()
                .content(mapToMessage(coworking, messages.getContent()))
                .hasNext(messages.hasNext())
                .hasPrev(messages.hasPrevious())
                .next(pageable.getPageNumber()+1)
                .prev(pageable.getPageNumber()-1)
                .build();
    }

    private static List<Message<GetChattingRes>> mapToMessage(Coworking coworking, List<GetChattingRes> messages) {
        return messages
                .stream()
                .map(res -> Message.<GetChattingRes>builder()
                        .type((res.getFileName() == null) ? MessageType.TEXT : MessageType.FILE)
                        .coworkingId(coworking.getId())
                        .data(res)
                        .build())
                .collect(Collectors.toList());
    }

    // task 목록 조회
    public List<Message<GetTaskRes>> getTaskList(Long coworkingId) {
        return taskRepository.getCoworkingTask(coworkingId)
                .stream()
                .map(res ->
                        Message.<GetTaskRes>builder()
                                .type(MessageType.TASK)
                                .coworkingId(coworkingId)
                                .data(res)
                                .build())
                .collect(Collectors.toList());
    }

    // file 목록 조회
    public List<GetFileRes> getFileList(Long coworkingId, Pageable pageable) {
        return fileRepository.getFileList(coworkingId, pageable);
    }
}
