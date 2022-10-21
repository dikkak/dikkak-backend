package com.dikkak.controller;

import com.dikkak.common.BaseException;
import com.dikkak.config.UserPrincipal;
import com.dikkak.dto.coworking.GetChattingRes;
import com.dikkak.dto.coworking.GetTaskRes;
import com.dikkak.dto.coworking.Message;
import com.dikkak.entity.coworking.Coworking;
import com.dikkak.entity.coworking.StepType;
import com.dikkak.entity.user.UserTypeEnum;
import com.dikkak.service.CoworkingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/coworking")
@RequiredArgsConstructor
public class CoworkingController {

    private final CoworkingService coworkingService;

    /**
     * 외주작업실의 해당 step의 채팅 목록 조회
     * @param principal 회원 id, 타입
     * @param coworkingId 외주작업실 id
     * @param step 외주 작업 step
     */
    @GetMapping("/chat")
    public List<Message<GetChattingRes>> getChatList(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam Long coworkingId,
            @RequestParam StepType step) throws BaseException {

//        if(principal == null) throw new BaseException(INVALID_ACCESS_TOKEN);
//        if(!checkUser(principal, coworkingId)) throw new BaseException(UNAUTHORIZED_REQUEST);

        return coworkingService.getMessageList(coworkingId, step);
    }

    /**
     * 외주작업실 task 조회
     * @param principal 회원 id, 타입
     * @param coworkingId 외주작업실 id
     */
    @GetMapping("/task")
    public List<Message<GetTaskRes>> getTask( @AuthenticationPrincipal UserPrincipal principal,
                                              @RequestParam Long coworkingId) throws BaseException {
//        if(principal == null) throw new BaseException(INVALID_ACCESS_TOKEN);
//        if(!checkUser(principal, coworkingId)) throw new BaseException(UNAUTHORIZED_REQUEST);

        return coworkingService.getTaskList(coworkingId);
    }

    // 작업실 접근권한이 있는 회원인지 검사
    private boolean checkUser(UserPrincipal principal, Long coworkingId) throws BaseException {
        UserTypeEnum type = principal.getType();
        if (type.equals(UserTypeEnum.ADMIN)) return true;
        if (type.equals(UserTypeEnum.UNDEFINED)) return false;
        else {
            Coworking coworking = coworkingService.getCoworking(coworkingId);
            if (type.equals(UserTypeEnum.CLIENT))
                return principal.getUserId().equals(coworking.getProposal().getClient().getId());
            else if (type.equals(UserTypeEnum.DESIGNER))
                return principal.getUserId().equals(coworking.getDesigner().getId());
        }
        return false;
    }

}
