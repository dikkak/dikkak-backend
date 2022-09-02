package com.dikkak.controller;

import com.dikkak.dto.auth.GetLoginRes;
import com.dikkak.dto.auth.ReissueRes;
import com.dikkak.dto.common.BaseResponse;
import com.dikkak.service.JwtService;
import com.dikkak.service.OauthService;
import com.dikkak.service.UserService;
import com.dikkak.dto.common.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static com.dikkak.dto.common.ResponseMessage.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final OauthService oauthService;
    private final JwtService jwtService;
    private final List<String> providerList = new ArrayList<>(Arrays.asList("kakao", "google", "facebook"));
    private static final Pattern EMAIL = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",Pattern.CASE_INSENSITIVE);


    /**
     * oauth 로그인 - 최초 로그인 시 회원가입
     * @param provider kakao, google, facebook
     * @param code 인가 코드
     */
    @GetMapping("/login/{provider}")
    public ResponseEntity<?> login(@PathVariable String provider, @RequestParam String code,
                                   HttpServletResponse res) {
        try {
            if(providerList.contains(provider)) {

                GetLoginRes loginRes = oauthService.login(provider, code);

                // refresh token을 cookie에 저장
                String refreshToken = loginRes.getRefreshToken();
                ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken)
                        .maxAge(60 * 60 * 24 * 14)
                        .path("/")
                        .sameSite("none")
                        .secure(true)
                        .httpOnly(true)
                        .build();

                res.setHeader("Set-Cookie", cookie.toString());

                // response body에서 refresh token 제거하기
                loginRes.setRefreshToken(null);
                return ResponseEntity.ok().body(loginRes);
            } else {
                return ResponseEntity.badRequest().body(new BaseResponse(INVALID_PROVIDER));
            }

        } catch (BaseException e){
            if(e.getResponseMessage().equals(ALREADY_REGISTERED_SOCIAL_LOGIN))
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new BaseResponse(e));
            else return ResponseEntity.badRequest().body(new BaseResponse(e));
        }
    }


    /**
     * @param refreshToken cookie에 저장되어 있는 refresh token
     * @return 새로 발행한 access token
     */
    @GetMapping("/refresh")
    public ResponseEntity<?> reIssue(
            @CookieValue(name = "refresh_token", required = false) String refreshToken) {

        // refresh token 없는 경우
        if(refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new BaseResponse(INVALID_REFRESH_TOKEN));
        }

        // access token 재발급
        try {
            // refresh 토큰 유효성 검사 및 userId 추출
            Long userId = jwtService.validateToken(refreshToken);

            // 존재하는 회원인지 검사
            userService.getUser(userId);

            // access token 생성
            String newAccessToken = jwtService.createAccessToken(userId);
            return ResponseEntity.ok().body(new ReissueRes(newAccessToken));
        } catch (BaseException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new BaseResponse(e));
        }

    }

    /**
     * 로그아웃 API
     * 쿠키에 저장된 refresh_token을 null로 설정
     * 소셜 로그아웃 요청
     * @return provider 타입
     */
    @GetMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal Long userId, HttpServletResponse res) {

        if(userId == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_ACCESS_TOKEN);

        try {
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
            oauthService.logout(userId);

            return ResponseEntity.ok().build();
        } catch (BaseException e) {
            return ResponseEntity.badRequest().body(new BaseResponse(e.getResponseMessage()));
        }
    }


    /**
     * 사용하지 않음
     * 회원가입
     */
//    @PostMapping("/signup")
//    @ResponseBody
//    public ResponseEntity<?> registerUser(@RequestBody PostSignupReq postSignupReq) {
//
//        try {
//            // 필수 사항 미동의
//            if(!postSignupReq.isTermsConditions() || !postSignupReq.isDataPolicy()) {
//                return getBadRequestResponse(REQUIRED_ITEM_DISAGREE);
//            }
//
//            // 이메일 형식 검사
//            if(postSignupReq.getEmail() == null || !isRegexEmail(postSignupReq.getEmail())) {
//                return getBadRequestResponse(INVALID_FORMAT_EMAIL);
//            }
//
//            // 비밀번호 형식 검사
//            if(postSignupReq.getPassword() == null) {
//                return getBadRequestResponse(INVALID_FORMAT_PASSWORD);
//            }
//
//            // 전화번호 형식 검사
//            if(postSignupReq.getPhoneNumber() == null || !isRegexPhoneNumber(postSignupReq.getPhoneNumber())){
//                return getBadRequestResponse(INVALID_FORMAT_PHONENUMBER);
//            }
//
//            PostSignupRes postSignupRes = userService.create(new User(postSignupReq));
//
//            return getOkResponse(postSignupRes);
//
//        } catch(BaseException e) {
//            return getBadRequestResponse(e.getResponseMessage());
//        }
//    }

    private boolean isRegexEmail(String email) {
        return EMAIL.matcher(email).find();
    }


    /**
     * 사용하지 않음
     * 로그인
     */
//    @PostMapping("/signin")
//    @ResponseBody
//    public ResponseEntity<?> login(@RequestBody PostSigninReq postSigninReq) {
//
//        try {
//            // 이메일 형식 검사
//            if(postSigninReq.getEmail() == null || !isRegexEmail(postSigninReq.getEmail())) {
//                return getBadRequestResponse(INVALID_FORMAT_EMAIL);
//            }
//
//            // 비밀번호 형식 검사
//            if(postSigninReq.getPassword() == null) {
//                return getBadRequestResponse(INVALID_FORMAT_PASSWORD);
//            }
//
//            User user = userService.authenticate(postSigninReq.getEmail(), postSigninReq.getPassword());
//
//            // 토큰 발급
//            String accessToken = jwtService.createAccessToken(user.getId());
//            String refreshToken = jwtService.createRefreshToken();
//
//            // refreshToken db에 저장
//            userService.setUserRefreshToken(user, refreshToken);
//
//            return getOkResponse(PostSigninRes.builder()
//                    .accessToken(accessToken)
//                    .refreshToken(refreshToken)
//                    .build());
//
//        } catch (BaseException e) {
//            return getBadRequestResponse(e.getResponseMessage());
//        }
//    }


}
