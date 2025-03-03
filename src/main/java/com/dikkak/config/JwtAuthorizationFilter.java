package com.dikkak.config;

import com.dikkak.common.BaseException;
import com.dikkak.common.ErrorResponse;
import com.dikkak.entity.user.User;
import com.dikkak.repository.UserRepository;
import com.dikkak.service.JwtProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private UserRepository userRepository;
    private JwtProvider jwtProvider;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository, JwtProvider jwtProvider) {
        super(authenticationManager);
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String authorization = request.getHeader("authorization");
        if(authorization == null || !authorization.startsWith("Bearer")) {
            chain.doFilter(request, response);
            return;
        }
        String token = authorization.substring(7); // "Bearer " 이후부터

        // 토큰 검사 및 회원 아이디 추출
        Long userId = null;
        try {
            userId = jwtProvider.validateToken(token);
        } catch (BaseException e) {
            response.setContentType("application/json");
            response.setCharacterEncoding("utf8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().print(new ObjectMapper().writeValueAsString(new ErrorResponse(e)));
            return;
        }
        Optional<User> user = userRepository.findById(userId);
        if(user.isPresent()) {
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    new UserPrincipal(user.get()), null, AuthorityUtils.NO_AUTHORITIES
            );
            // 시큐리티 세션에 회원 정보를 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        chain.doFilter(request, response);
    }
}
