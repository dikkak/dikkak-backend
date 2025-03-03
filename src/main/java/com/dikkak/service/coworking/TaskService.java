package com.dikkak.service.coworking;

import com.dikkak.common.BaseException;
import com.dikkak.config.UserPrincipal;
import com.dikkak.controller.coworking.CoworkingSupport;
import com.dikkak.dto.PageCustom;
import com.dikkak.dto.coworking.AddTaskReq;
import com.dikkak.dto.coworking.TaskRes;
import com.dikkak.dto.coworking.UpdateTaskReq;
import com.dikkak.entity.coworking.Coworking;
import com.dikkak.entity.coworking.CoworkingFile;
import com.dikkak.entity.coworking.CoworkingTask;
import com.dikkak.repository.coworking.task.CoworkingTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.dikkak.common.ResponseMessage.WRONG_TASK_ID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskService {

    private final CoworkingTaskRepository taskRepository;
    private final CoworkingSupport coworkingSupport;

    // task 목록 조회
    public PageCustom<TaskRes> getTaskList(Long coworkingId, @Nullable Boolean complete, Pageable pageable) {
        Page<TaskRes> page = taskRepository.getCoworkingTask(coworkingId, complete, pageable);
        return PageCustom.<TaskRes>builder()
                .content(page.getContent())
                .hasNext(page.hasNext())
                .hasPrev(page.hasPrevious())
                .next(pageable.getPageNumber()+1)
                .prev(pageable.getPageNumber()-1)
                .build();
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

    public CoworkingTask getTask(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new BaseException(WRONG_TASK_ID));
    }

    @Transactional
    public void updateTask(UpdateTaskReq req, UserPrincipal principal) {
        CoworkingTask task = getTask(req.getTaskId());
        coworkingSupport.checkCoworkingUser(principal, task.getCoworking());
        task.updateComplete(req.getChecked());
    }
}
