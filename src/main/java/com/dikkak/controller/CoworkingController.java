package com.dikkak.controller;

import com.dikkak.config.UserPrincipal;
import com.dikkak.dto.common.BaseException;
import com.dikkak.dto.common.BaseResponse;
import com.dikkak.dto.coworking.GetChattingRes;
import com.dikkak.entity.coworking.Coworking;
import com.dikkak.entity.user.UserTypeEnum;
import com.dikkak.service.CoworkingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.dikkak.dto.common.ResponseMessage.INVALID_ACCESS_TOKEN;
import static com.dikkak.dto.common.ResponseMessage.UNAUTHORIZED_REQUEST;

@RestController
@RequestMapping("/coworking")
@RequiredArgsConstructor
public class CoworkingController {

    private final CoworkingService coworkingService;

    /**
     * 외주작업실의 해당 step의 채팅 목록 조회
     * @param principal 회원 id, 타입
     * @param coworkingId 외주작업실 id
     * @param step step 번호 (0~9)
     */
    @GetMapping("/chat")
    public ResponseEntity<?> getChatList(@AuthenticationPrincipal UserPrincipal principal,
                                         @RequestParam Long coworkingId,
                                         @RequestParam int step) {
        try {
            if(principal == null) throw new BaseException(INVALID_ACCESS_TOKEN);
            if(!checkUser(principal, coworkingId)) throw new BaseException(UNAUTHORIZED_REQUEST);

            List<GetChattingRes> chatList = coworkingService.getMessageList(coworkingId, step);
            return ResponseEntity.ok().body(Map.of("data", chatList));
        } catch (BaseException e) {
            return ResponseEntity.badRequest().body(new BaseResponse(e));
        }

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
