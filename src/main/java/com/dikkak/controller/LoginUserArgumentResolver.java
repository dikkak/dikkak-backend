package com.dikkak.controller;

import com.dikkak.common.BaseException;
import com.dikkak.config.UserPrincipal;
import com.dikkak.entity.user.User;
import com.dikkak.repository.UserRepository;
import com.dikkak.service.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import static com.dikkak.common.Const.BEARER;
import static com.dikkak.common.ResponseMessage.INVALID_ACCESS_TOKEN;

@Component
@RequiredArgsConstructor
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(LoginUser.class) != null
                && parameter.getParameterType().equals(UserPrincipal.class);
    }

    @Override
    public UserPrincipal resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        String authorization = webRequest.getHeader(HttpHeaders.AUTHORIZATION);
        String token = extractToken(authorization);

        // 토큰 검사 및 회원 아이디 추출
        Long userId = jwtProvider.validateToken(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(INVALID_ACCESS_TOKEN));

        return new UserPrincipal(user);
    }

    private static String extractToken(String authorization) {
        if (authorization == null || !authorization.startsWith(BEARER + " ")) {
            throw new BaseException(INVALID_ACCESS_TOKEN);
        }
        return authorization.substring(BEARER.length());
    }
}
