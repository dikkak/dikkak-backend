package com.dikkak.controller.coworking;

import com.dikkak.common.BaseException;
import com.dikkak.config.UserPrincipal;
import com.dikkak.dto.coworking.GetChattingRes;
import com.dikkak.dto.message.*;
import com.dikkak.entity.coworking.CoworkingMessage;
import com.dikkak.service.coworking.CoworkingService;
import com.dikkak.service.coworking.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.dikkak.common.ResponseMessage.INVALID_ACCESS_TOKEN;
import static com.dikkak.common.ResponseMessage.UNAUTHORIZED_REQUEST;


@RestController("/message")
@CrossOrigin
@RequiredArgsConstructor
@Slf4j
public class MessageController {

    private final SimpMessageSendingOperations simpMessageSendingOperations;
    private final CoworkingService coworkingService;
    private final MessageService messageService;
    private final CoworkingSupport coworkingSupport;

    /**
     * 외주작업실 채팅 목록 조회
     * @param principal 회원 id, 타입
     * @param coworkingId 외주작업실 id
     */
    @GetMapping("/list")
    public List<com.dikkak.dto.coworking.Message<GetChattingRes>> getChatList(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam Long coworkingId) throws BaseException {

        if(principal == null) throw new BaseException(INVALID_ACCESS_TOKEN);
        if(!coworkingSupport.checkUser(principal, coworkingId)) throw new BaseException(UNAUTHORIZED_REQUEST);

        return coworkingService.getMessageList(coworkingId);
    }

    /**
     * 외주작업실 텍스트 메시지 전송
     * /pub/message 으로 매핑
     * /sub/coworking/{coworkingId} 로 해당 텍스트 메시지 전송
     * @param request email, content, coworkingId
     */
    @MessageMapping("")
    public void saveTextMessage(TextReq request) throws BaseException {
        log.info("request= {}", request);
        CoworkingMessage message = messageService.saveTextMessage(request);
        simpMessageSendingOperations.convertAndSend("/sub/coworking/" + request.getCoworkingId(),
                Message.builder()
                        .coworkingId(request.getCoworkingId())
                        .type(MessageType.TEXT)
                        .data(
                                TextMessage.builder()
                                        .email(request.getEmail())
                                        .content(message.getContent())
                                        .createdAt(message.getCreatedAt())
                                        .build()
                        )
                        .build()
        );
    }

}
