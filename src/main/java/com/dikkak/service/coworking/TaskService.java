package com.dikkak.service.coworking;

import com.dikkak.dto.coworking.AddTaskReq;
import com.dikkak.dto.coworking.TaskRes;
import com.dikkak.dto.coworking.message.Message;
import com.dikkak.dto.coworking.message.MessageType;
import com.dikkak.entity.coworking.Coworking;
import com.dikkak.entity.coworking.CoworkingFile;
import com.dikkak.entity.coworking.CoworkingTask;
import com.dikkak.repository.coworking.task.CoworkingTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
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
    public List<Message<TaskRes>> getTaskList(Long coworkingId) {
        return taskRepository.getCoworkingTask(coworkingId)
                .stream()
                .map(res ->
                        Message.<TaskRes>builder()
                                .type(MessageType.TASK)
                                .coworkingId(coworkingId)
                                .data(res)
                                .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public TaskRes createTask(AddTaskReq req, Coworking coworking, @Nullable CoworkingFile file) {
        CoworkingTask task = taskRepository.save(CoworkingTask.builder()
                .coworking(coworking)
                .file(file)
                .content(req.getContent())
                .build());
        return TaskRes.fromEntity(task);
    }
}
