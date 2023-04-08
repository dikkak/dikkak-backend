package com.dikkak.controller;

import com.dikkak.common.BaseException;
import com.dikkak.common.ResponseMessage;
import com.dikkak.config.UserPrincipal;
import com.dikkak.dto.user.PostRegisterReq;
import com.dikkak.dto.user.UserInfoRes;
import com.dikkak.dto.user.UserTypeReq;
import com.dikkak.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.regex.Pattern;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private static final Pattern PHONE_NUMBER = Pattern.compile("^010(?:\\d{4})\\d{4}$");


    /**
     * 최초 로그인 후, 회원 이름과 전화번호를 입력한다.
     * @param principal 회원 id, 타입
     */
    @PostMapping("/register")
    public void register(@LoginUser UserPrincipal principal,
                         @RequestBody PostRegisterReq req) {
        if(req.getUsername() == null || req.getUsername().isEmpty())
            throw new BaseException(ResponseMessage.EMPTY_USER_NAME);

        if(req.getPhoneNumber() == null || req.getPhoneNumber().isEmpty())
            throw new BaseException(ResponseMessage.EMPTY_PHONE_NUMBER);

        // 전화번호 형식 검사
        if(!isRegexPhoneNumber(req.getPhoneNumber()))
            throw new BaseException(ResponseMessage.INVALID_FORMAT_PHONE_NUMBER);

        // 필수 항목 동의 여부 검사
        if(!req.isTermsConditions() || !req.isDataPolicy())
            throw new BaseException(ResponseMessage.REQUIRED_ITEM_DISAGREE);

        userService.registerUser(principal.getUserId(), req);
    }

    /**
     * 회원 타입 설정 API
     * @param req - type: CLIENT, DESIGNER
     */
    @PostMapping("/type")
    public void setUserType(@LoginUser UserPrincipal principal,
                            @RequestBody UserTypeReq req) {
        userService.setUserType(principal.getUserId(), req.getType());
    }

    /**
     * 회원 정보 조회 API
     * @return email, username, type, provider
     */
    @GetMapping("/info")
    public UserInfoRes getUsername(@LoginUser UserPrincipal principal) {
        return UserInfoRes.fromUser(userService.getUser(principal.getUserId()));
    }

    private boolean isRegexPhoneNumber(String phoneNumber) {
        return PHONE_NUMBER.matcher(phoneNumber).find();
    }

}
