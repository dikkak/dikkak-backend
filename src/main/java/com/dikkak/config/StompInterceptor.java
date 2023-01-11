package com.dikkak.config;

import com.dikkak.common.BaseException;
import com.dikkak.common.ResponseMessage;
import com.dikkak.repository.UserRepository;
import com.dikkak.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import static com.dikkak.common.Const.BEARER;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@RequiredArgsConstructor
public class StompInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
        if (!StompCommand.CONNECT.equals(headerAccessor.getCommand())) {
            return message;
        }

        String authorization = headerAccessor.getFirstNativeHeader(AUTHORIZATION);
        if(authorization == null || !authorization.startsWith(BEARER + " ")) {
            throw new BaseException(ResponseMessage.UNAUTHORIZED_REQUEST);
        }
        String token = authorization.substring(BEARER.length());

        Long userId = jwtService.validateToken(token);
        if (userRepository.findById(userId).isEmpty()) {
            throw new BaseException(ResponseMessage.UNAUTHORIZED_REQUEST);
        }
        return message;
    }
}
