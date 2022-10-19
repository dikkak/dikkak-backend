package com.dikkak.config;

import com.dikkak.repository.UserRepository;
import com.dikkak.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable().authorizeRequests()
                .antMatchers("/").permitAll();

        http
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin().disable()  // 폼 로그인 비활성화
                .httpBasic().disable(); // Http Basic Auth 인증 비활성화

        // jwt 인가 - 토큰 검증 및 Authentication 객체를 세션에 저장
        http.addFilter(new JwtAuthorizationFilter(authenticationManagerBean(), userRepository, jwtService));
    }
}


