package com.dikkak.controller;

import com.dikkak.dto.auth.GetLoginRes;
import com.dikkak.service.JwtService;
import com.dikkak.service.OauthService;
import com.dikkak.service.UserService;
import com.dikkak.dto.common.BaseException;
import com.dikkak.dto.common.BaseResponse;
import com.dikkak.dto.common.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static com.dikkak.dto.common.ResponseMessage.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final OauthService oauthService;
    private final JwtService jwtService;
    private final List<String> providerList = new ArrayList<>(Arrays.asList("kakao", "google", "facebook"));
    private static final Pattern EMAIL = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",Pattern.CASE_INSENSITIVE);
    private static final Pattern PHONE_NUMBER = Pattern.compile("\\d{3}-\\d{4}-\\d{4}");


    /**
     * oauth 로그인 - 최초 로그인 시 회원가입
     * @param provider kakao, google, facebook
     * @param code 인가 코드
     */
    @GetMapping("/login/{provider}")
    @ResponseBody
    public ResponseEntity<?> login(@PathVariable String provider, @RequestParam String code) {
        try {
            if(providerList.contains(provider)) {
                GetLoginRes res = oauthService.login(provider, code);
                System.out.println("res = " + res);
                return getOkResponse(res);
            } else {
                return getBadRequestResponse(INVALID_PROVIDER);
            }

        } catch (BaseException e){
            return getBadRequestResponse(e.getResponseMessage());
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
