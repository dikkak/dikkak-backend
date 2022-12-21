package com.dikkak.controller.coworking;

import com.dikkak.common.BaseException;
import com.dikkak.config.UserPrincipal;
import com.dikkak.dto.PageCustom;
import com.dikkak.dto.coworking.GetChattingRes;
import com.dikkak.dto.coworking.GetTaskRes;
import com.dikkak.dto.coworking.Message;
import com.dikkak.entity.coworking.Coworking;
import com.dikkak.service.coworking.CoworkingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.dikkak.common.ResponseMessage.INVALID_ACCESS_TOKEN;
import static com.dikkak.common.ResponseMessage.UNAUTHORIZED_REQUEST;

@RestController
@RequestMapping("/coworking")
@RequiredArgsConstructor
public class CoworkingController {

    private final CoworkingService coworkingService;
    private final CoworkingSupport coworkingSupport;

    /**
     * 외주작업실의 채팅 목록 조회
     * @param principal 회원 id, 타입
     * @param coworkingId 외주작업실 id
     */
    @GetMapping("/chat")
    public PageCustom<Message<GetChattingRes>> getChatList(@AuthenticationPrincipal UserPrincipal principal,
                                                           @RequestParam Long coworkingId,
                                                           @PageableDefault(size = 20, page = 0) Pageable pageable) {
        if(principal == null) {
            throw new BaseException(INVALID_ACCESS_TOKEN);
        }

        Coworking coworking = coworkingSupport.checkUserAndGetCoworking(principal, coworkingId);
        if(coworking == null) {
            throw new BaseException(UNAUTHORIZED_REQUEST);
        }

        return coworkingService.getMessageList(coworking, pageable);
    }

    /**
     * 외주작업실 task 조회
     * @param principal 회원 id, 타입
     * @param coworkingId 외주작업실 id
     */
    @GetMapping("/task")
    public List<Message<GetTaskRes>> getTask(@AuthenticationPrincipal UserPrincipal principal,
                                             @RequestParam Long coworkingId) {
        if(principal == null) throw new BaseException(INVALID_ACCESS_TOKEN);

        Coworking coworking = coworkingSupport.checkUserAndGetCoworking(principal, coworkingId);
        if(coworking == null) {
            throw new BaseException(UNAUTHORIZED_REQUEST);
        }

        return coworkingService.getTaskList(coworkingId);
    }
}
