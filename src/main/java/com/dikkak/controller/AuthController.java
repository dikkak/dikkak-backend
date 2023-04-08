package com.dikkak.controller;

import com.dikkak.common.BaseException;
import com.dikkak.config.UserPrincipal;
import com.dikkak.dto.auth.GetLoginRes;
import com.dikkak.dto.auth.ReissueRes;
import com.dikkak.service.JwtProvider;
import com.dikkak.service.OauthService;
import com.dikkak.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.dikkak.common.ResponseMessage.INVALID_PROVIDER;
import static com.dikkak.common.ResponseMessage.INVALID_REFRESH_TOKEN;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final OauthService oauthService;
    private final JwtProvider jwtProvider;
    private final List<String> providerList = new ArrayList<>(Arrays.asList("kakao", "google", "facebook"));

    /**
     * oauth 로그인 - 최초 로그인 시 회원가입
     * @param provider kakao, google, facebook
     * @param code 인가 코드
     */
    @GetMapping("/login/{provider}")
    public GetLoginRes login(@PathVariable String provider, @RequestParam String code, HttpServletResponse res) {
        if (!providerList.contains(provider)) {
            throw new BaseException(INVALID_PROVIDER);
        }
        GetLoginRes loginRes = oauthService.login(provider, code);

        // refresh token을 cookie에 저장
        String refreshToken = loginRes.getRefreshToken();
        ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken)
                .maxAge(60L * 60 * 24 * 14)
                .path("/")
                .sameSite("none")
                .secure(true)
                .httpOnly(true)
                .build();

        res.setHeader("Set-Cookie", cookie.toString());

        // response body에서 refresh token 제거하기
        loginRes.setRefreshToken(null);
        return loginRes;
    }

    /**
     * @param refreshToken cookie에 저장되어 있는 refresh token
     * @return 새로 발행한 access token
     */
    @GetMapping("/refresh")
    public ReissueRes reIssue(@CookieValue(name = "refresh_token", required = false) String refreshToken) {
        // refresh token 없는 경우
        if(refreshToken == null) {
            throw new BaseException(INVALID_REFRESH_TOKEN);
        }

        // refresh 토큰 유효성 검사 및 userId 추출
        Long userId = jwtProvider.validateToken(refreshToken);

        // 존재하는 회원인지 검사
        userService.getUser(userId);

        // access token 재발급
        String newAccessToken = jwtProvider.createAccessToken(userId);
        return new ReissueRes(newAccessToken);
    }

    /**
     * 로그아웃 API
     * 쿠키에 저장된 refresh_token을 null로 설정
     * 소셜 로그아웃 요청
     */
    @GetMapping("/logout")
    public void logout(@LoginUser UserPrincipal principal, HttpServletResponse res) {
        // 쿠키 지우기
        ResponseCookie cookie = ResponseCookie.from("refresh_token", null)
                .maxAge(0)
                .path("/")
                .secure(true)
                .httpOnly(true)
                .sameSite("none")
                .build();
        res.setHeader("Set-Cookie", cookie.toString());

        // 소셜 로그아웃 - 구글, 페이스북만
        oauthService.logout(principal.getUserId());
    }
}
