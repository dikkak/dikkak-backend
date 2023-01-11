package com.dikkak.controller.coworking;

import com.dikkak.config.UserPrincipal;
import com.dikkak.controller.LoginUser;
import com.dikkak.dto.PageCustom;
import com.dikkak.dto.coworking.GetChattingRes;
import com.dikkak.dto.coworking.message.FileMessage;
import com.dikkak.dto.coworking.FileReq;
import com.dikkak.dto.coworking.message.Message;
import com.dikkak.dto.coworking.message.MessageType;
import com.dikkak.dto.coworking.message.TextMessage;
import com.dikkak.dto.coworking.TextReq;
import com.dikkak.entity.coworking.Coworking;
import com.dikkak.entity.coworking.CoworkingMessage;
import com.dikkak.service.coworking.CoworkingService;
import com.dikkak.service.coworking.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@CrossOrigin
@RequiredArgsConstructor
@Slf4j
public class MessageController {

    private final SimpMessageSendingOperations simpMessageSendingOperations;
    private final MessageService messageService;
    private final CoworkingService coworkingService;
    private final CoworkingSupport coworkingSupport;

    /**
     * 외주작업실의 채팅 목록 조회
     * @param principal 회원 id, 타입
     * @param coworkingId 외주작업실 id
     */
    @GetMapping("/chat")
    public PageCustom<Message<GetChattingRes>> getChatList(@LoginUser UserPrincipal principal,
                                                           @RequestParam Long coworkingId,
                                                           @PageableDefault(size = 20, page = 0) Pageable pageable) {
        Coworking coworking = coworkingService.getCoworking(coworkingId);
        coworkingSupport.checkCoworkingUser(principal, coworking);
        return messageService.getMessageList(coworking, pageable);
    }

    /**
     * 외주작업실 텍스트 메시지 전송
     * /pub/text 으로 매핑
     * /sub/coworking/{coworkingId} 로 해당 텍스트 메시지 전송
     * @param request email, content, coworkingId
     */
    @MessageMapping("/text")
    public void saveTextMessage(TextReq request) {
        Coworking coworking = coworkingService.getCoworking(request.getCoworkingId());

        log.info("request= {}", request);
        CoworkingMessage message = messageService.saveTextMessage(request, coworking);
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

    /**
     * 파일 메시지 전송
     * /sub/coworking/{coworkingId} 로 해당 파일 메시지 전송
     * @param request email, coworkingId
     */
    @PostMapping("/pub/file")
    public void saveFileMessage(@LoginUser UserPrincipal principal,
                                @RequestPart FileReq request,
                                @RequestPart MultipartFile file) {
        Coworking coworking = coworkingService.getCoworking(request.getCoworkingId());
        coworkingSupport.checkCoworkingUser(principal, coworking);

        // 파일 저장 & 메시지 저장
        FileMessage message = messageService.saveFileMessage(request, coworking, file);

        simpMessageSendingOperations.convertAndSend("/sub/coworking/" + request.getCoworkingId(),
                Message.builder()
                        .coworkingId(request.getCoworkingId())
                        .type(MessageType.FILE)
                        .data(message)
                        .build()
        );
    }
}
