package com.dikkak.controller;

import com.dikkak.dto.common.BaseException;
import com.dikkak.dto.common.BaseResponse;
import com.dikkak.dto.user.PostRegisterReq;
import com.dikkak.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.regex.Pattern;

import static com.dikkak.dto.common.ResponseMessage.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private static final Pattern PHONE_NUMBER = Pattern.compile("^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$");


    /**
     * 최초 로그인 후, 회원 이름과 전화번호를 입력한다.
     * @param userId - access token으로부터 추출한 회원 아이디
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@AuthenticationPrincipal Long userId,
                                      @RequestBody PostRegisterReq req) {

        if(userId == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new BaseResponse(INVALID_ACCESS_TOKEN));

        if(req.getUsername() == null || req.getUsername().isEmpty())
            return ResponseEntity.badRequest().body(new BaseResponse(EMPTY_USER_NAME));

        if(req.getPhoneNumber() == null || req.getPhoneNumber().isEmpty())
            return ResponseEntity.badRequest().body(new BaseResponse(EMPTY_PHONE_NUMBER));

        // 전화번호 형식 검사
        if(!isRegexPhoneNumber(req.getPhoneNumber()))
            return ResponseEntity.badRequest().body(new BaseResponse(INVALID_FORMAT_PHONE_NUMBER));

        // 필수 항목 동의 여부 검사
        if(!req.isTermsConditions() || !req.isDataPolicy())
            return ResponseEntity.badRequest().body(new BaseResponse(REQUIRED_ITEM_DISAGREE));

        try {
            userService.registerUser(userId, req);
            return ResponseEntity.ok().body(null);
        } catch (BaseException e) {
            return ResponseEntity.badRequest().body(new BaseResponse(e));
        }

    }

    private boolean isRegexPhoneNumber(String phoneNumber) {
        return PHONE_NUMBER.matcher(phoneNumber).find();
    }

}
