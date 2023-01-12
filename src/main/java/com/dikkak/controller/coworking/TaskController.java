package com.dikkak.controller.coworking;

import com.dikkak.config.UserPrincipal;
import com.dikkak.controller.LoginUser;
import com.dikkak.dto.PageCustom;
import com.dikkak.dto.coworking.AddTaskReq;
import com.dikkak.dto.coworking.TaskRes;
import com.dikkak.dto.coworking.UpdateTaskReq;
import com.dikkak.entity.coworking.Coworking;
import com.dikkak.entity.coworking.CoworkingFile;
import com.dikkak.service.coworking.CoworkingService;
import com.dikkak.service.coworking.FileService;
import com.dikkak.service.coworking.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RequestMapping("/coworking/task")
@RestController
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final CoworkingSupport coworkingSupport;
    private final CoworkingService coworkingService;
    private final FileService fileService;

    /**
     * 외주작업실 task 조회
     * @param principal 회원 id, 타입
     * @param coworkingId 외주작업실 id
     */
    @GetMapping
    public PageCustom<TaskRes> getTask(@LoginUser UserPrincipal principal,
                                       @RequestParam Long coworkingId,
                                       @RequestParam(required = false) Boolean complete,
                                       @PageableDefault(size = 10, page = 0, sort = "createdAt", direction = DESC) Pageable pageable) {
        Coworking coworking = coworkingService.getCoworking(coworkingId);
        coworkingSupport.checkCoworkingUser(principal, coworking);
        return taskService.getTaskList(coworkingId, complete, pageable);
    }

    /**
     * 외주작업실 task 추가
     * @param req coworking id, content, (optional) file id
     */
    @PostMapping
    public TaskRes addTask(@LoginUser UserPrincipal principal,
                           @RequestBody AddTaskReq req) {
        Coworking coworking = coworkingService.getCoworking(req.getCoworkingId());
        coworkingSupport.checkCoworkingUser(principal, coworking);
        CoworkingFile file = (req.getFileId() != null) ? fileService.getFile(req.getFileId()) : null;
        return taskService.createTask(req, coworking, file);
    }

    @PatchMapping("/checked")
    public void updateTask(@LoginUser UserPrincipal principal,
                           @Validated @RequestBody UpdateTaskReq req) {
        taskService.updateTask(req, principal);
    }
}
