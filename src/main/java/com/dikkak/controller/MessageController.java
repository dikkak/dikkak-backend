package com.dikkak.controller;

import com.dikkak.common.BaseException;
import com.dikkak.dto.message.Message;
import com.dikkak.dto.message.MessageType;
import com.dikkak.dto.message.TextMessage;
import com.dikkak.dto.message.TextReq;
import com.dikkak.entity.coworking.CoworkingMessage;
import com.dikkak.service.coworking.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;


@RestController
@CrossOrigin
@RequiredArgsConstructor
@Slf4j
public class MessageController {

    private final SimpMessageSendingOperations simpMessageSendingOperations;
    private final MessageService messageService;

    /**
     * /pub/text 으로 매핑
     * /sub/coworking/{coworkingId} 로 해당 텍스트 메시지 전송
     * @param request email, content, coworkingId
     */
    @MessageMapping("/text")
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
