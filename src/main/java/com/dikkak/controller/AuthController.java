package com.dikkak.controller;

import com.dikkak.entity.User;
import com.dikkak.service.JwtService;
import com.dikkak.service.UserService;
import dto.auth.PostSigninReq;
import dto.auth.PostSigninRes;
import dto.auth.PostSignupReq;
import dto.auth.PostSignupRes;
import dto.common.BaseException;
import dto.common.BaseResponse;
import dto.common.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.regex.Pattern;

import static dto.common.ResponseMessage.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private static final Pattern EMAIL = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",Pattern.CASE_INSENSITIVE);
    private static final Pattern PHONE_NUMBER = Pattern.compile("\\d{3}-\\d{4}-\\d{4}");

    /***
     * 회원가입
     */
    @PostMapping("/signup")
    @ResponseBody
    public ResponseEntity<?> registerUser(@RequestBody PostSignupReq postSignupReq) {

        try {
            // 필수 사항 미동의
            if(!postSignupReq.isTermsConditions() || !postSignupReq.isDataPolicy()) {
                return getBadRequestResponse(REQUIRED_ITEM_DISAGREE);
            }

            // 이메일 형식 검사
            if(postSignupReq.getEmail() == null || !isRegexEmail(postSignupReq.getEmail())) {
                return getBadRequestResponse(INVALID_FORMAT_EMAIL);
            }

            // 비밀번호 형식 검사
            if(postSignupReq.getPassword() == null) {
                return getBadRequestResponse(INVALID_FORMAT_PASSWORD);
            }

            // 전화번호 형식 검사
            if(postSignupReq.getPhoneNumber() == null || !isRegexPhoneNumber(postSignupReq.getPhoneNumber())){
                return getBadRequestResponse(INVALID_FORMAT_PHONENUMBER);
            }

            PostSignupRes postSignupRes = userService.create(new User(postSignupReq));

            return getOkResponse(postSignupRes);

        } catch(BaseException e) {
            return getBadRequestResponse(e.getResponseMessage());
        }
    }

    private ResponseEntity<BaseResponse<Object>> getOkResponse(Object data) {
        return ResponseEntity.ok().body(new BaseResponse<>(data));
    }

    private ResponseEntity<BaseResponse<Object>> getBadRequestResponse(ResponseMessage message) {
        return ResponseEntity.badRequest().body(new BaseResponse<>(message));
    }

    private boolean isRegexEmail(String email) {
        return EMAIL.matcher(email).find();
    }
    private boolean isRegexPhoneNumber(String phoneNumber) {
        return PHONE_NUMBER.matcher(phoneNumber).find();
    }


    /***
     * 로그인
     */
    @PostMapping("/signin")
    @ResponseBody
    public ResponseEntity<?> login(@RequestBody PostSigninReq postSigninReq) {

        try {
            // 이메일 형식 검사
            if(postSigninReq.getEmail() == null || !isRegexEmail(postSigninReq.getEmail())) {
                return getBadRequestResponse(INVALID_FORMAT_EMAIL);
            }

            // 비밀번호 형식 검사
            if(postSigninReq.getPassword() == null) {
                return getBadRequestResponse(INVALID_FORMAT_PASSWORD);
            }

            User user = userService.authenticate(postSigninReq.getEmail(), postSigninReq.getPassword());

            // 토큰 발급
            String accessToken = jwtService.createAccessToken(user.getId());
            String refreshToken = jwtService.createRefreshToken();

            // refreshToken db에 저장
            userService.setUserRefreshToken(user, refreshToken);

            return getOkResponse(PostSigninRes.builder()
                    .userId(user.getId())
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build());

        } catch (BaseException e) {
            return getBadRequestResponse(e.getResponseMessage());
        }
    }


}
