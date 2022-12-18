package com.dikkak.controller;

import com.dikkak.config.UserPrincipal;
import com.dikkak.common.BaseException;
import com.dikkak.dto.user.PostRegisterReq;
import com.dikkak.dto.user.UserInfoRes;
import com.dikkak.dto.user.UserTypeReq;
import com.dikkak.entity.user.User;
import com.dikkak.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.regex.Pattern;

import static com.dikkak.common.ResponseMessage.*;

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
    public void register(@AuthenticationPrincipal UserPrincipal principal,
                         @RequestBody PostRegisterReq req) {
        if(principal == null) {
            throw new BaseException(INVALID_ACCESS_TOKEN);
        }

        if(req.getUsername() == null || req.getUsername().isEmpty())
            throw new BaseException(EMPTY_USER_NAME);

        if(req.getPhoneNumber() == null || req.getPhoneNumber().isEmpty())
            throw new BaseException(EMPTY_PHONE_NUMBER);

        // 전화번호 형식 검사
        if(!isRegexPhoneNumber(req.getPhoneNumber()))
            throw new BaseException(INVALID_FORMAT_PHONE_NUMBER);

        // 필수 항목 동의 여부 검사
        if(!req.isTermsConditions() || !req.isDataPolicy())
            throw new BaseException(REQUIRED_ITEM_DISAGREE);

        userService.registerUser(principal.getUserId(), req);
    }

    /**
     * 회원 타입 설정 API
     * @param req - type: CLIENT, DESIGNER
     */
    @PostMapping("/type")
    public void setUserType(@AuthenticationPrincipal UserPrincipal principal,
                            @RequestBody UserTypeReq req) {
        if(principal == null) {
            throw new BaseException(INVALID_ACCESS_TOKEN);
        }

        userService.setUserType(principal.getUserId(), req.getType());
    }

    /**
     * 회원 정보 조회 API
     * @return email, username, type, provider
     */
    @GetMapping("/info")
    public UserInfoRes getUsername(@AuthenticationPrincipal UserPrincipal principal) {
        if(principal == null) {
            throw new BaseException(INVALID_ACCESS_TOKEN);
        }

        User user = userService.getUser(principal.getUserId());
        return new UserInfoRes(user.getEmail(), user.getName(), user.getUserType(), user.getProviderType(),
                user.getPhoneNumber(), user.isMarketingMessage(), user.isPopUpMessage());
    }

    private boolean isRegexPhoneNumber(String phoneNumber) {
        return PHONE_NUMBER.matcher(phoneNumber).find();
    }

}
