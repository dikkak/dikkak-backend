package com.dikkak.controller.coworking;

import com.dikkak.common.BaseException;
import com.dikkak.config.UserPrincipal;
import com.dikkak.dto.coworking.GetTaskRes;
import com.dikkak.dto.coworking.message.Message;
import com.dikkak.entity.coworking.Coworking;
import com.dikkak.service.coworking.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.dikkak.common.ResponseMessage.INVALID_ACCESS_TOKEN;
import static com.dikkak.common.ResponseMessage.UNAUTHORIZED_REQUEST;

@RequestMapping("/coworking/task")
@RestController
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final CoworkingSupport coworkingSupport;

    /**
     * 외주작업실 task 조회
     * @param principal 회원 id, 타입
     * @param coworkingId 외주작업실 id
     */
    @GetMapping
    public List<Message<GetTaskRes>> getTask(@AuthenticationPrincipal UserPrincipal principal,
                                             @RequestParam Long coworkingId) {
        if(principal == null) throw new BaseException(INVALID_ACCESS_TOKEN);

        Coworking coworking = coworkingSupport.checkUserAndGetCoworking(principal, coworkingId);
        if(coworking == null) {
            throw new BaseException(UNAUTHORIZED_REQUEST);
        }

        return taskService.getTaskList(coworkingId);
    }
}
