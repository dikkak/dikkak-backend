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

@Component
@RequiredArgsConstructor
public class StompInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER_TOKEN = "Bearer";

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
        if (!StompCommand.CONNECT.equals(headerAccessor.getCommand())) {
            return message;
        }

        String authorization = headerAccessor.getFirstNativeHeader(AUTHORIZATION);
        if(authorization == null || !authorization.startsWith(BEARER_TOKEN)) {
            throw new BaseException(ResponseMessage.UNAUTHORIZED_REQUEST);
        }
        String token = authorization.substring(7);

        Long userId = jwtService.validateToken(token);
        userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ResponseMessage.UNAUTHORIZED_REQUEST));
        return message;
    }
}
