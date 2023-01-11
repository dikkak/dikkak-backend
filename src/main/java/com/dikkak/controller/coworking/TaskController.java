package com.dikkak.controller.coworking;

import com.dikkak.config.UserPrincipal;
import com.dikkak.controller.LoginUser;
import com.dikkak.dto.coworking.GetTaskRes;
import com.dikkak.dto.coworking.message.Message;
import com.dikkak.entity.coworking.Coworking;
import com.dikkak.service.coworking.CoworkingService;
import com.dikkak.service.coworking.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/coworking/task")
@RestController
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final CoworkingSupport coworkingSupport;
    private final CoworkingService coworkingService;

    /**
     * 외주작업실 task 조회
     * @param principal 회원 id, 타입
     * @param coworkingId 외주작업실 id
     */
    @GetMapping
    public List<Message<GetTaskRes>> getTask(@LoginUser UserPrincipal principal,
                                             @RequestParam Long coworkingId) {
        Coworking coworking = coworkingService.getCoworking(coworkingId);
        coworkingSupport.checkCoworkingUser(principal, coworking);
        return taskService.getTaskList(coworkingId);
    }
}
