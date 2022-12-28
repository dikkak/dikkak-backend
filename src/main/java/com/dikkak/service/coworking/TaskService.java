package com.dikkak.service.coworking;

import com.dikkak.dto.coworking.GetTaskRes;
import com.dikkak.dto.coworking.message.Message;
import com.dikkak.dto.coworking.message.MessageType;
import com.dikkak.repository.coworking.task.CoworkingTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskService {

    private final CoworkingTaskRepository taskRepository;

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
}
