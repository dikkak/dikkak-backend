package com.dikkak.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@Configuration
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompInterceptor stompInterceptor;

    // 메시지 발행 (publish) 요청   : /pub (Application Destination Prefix)
    // 메시지 구독 (subscribe) 요청 : /sub (enable Simple Broker)
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/pub");
        registry.enableSimpleBroker("/sub");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // websocket handshake를 위한 endpoint 지정
        registry.addEndpoint("/dikkak-chat") // ws://~/dikkak-chat
                .setAllowedOriginPatterns("*");
//                .withSockJS(); // http://~/dikkak-chat
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompInterceptor);
    }
}
