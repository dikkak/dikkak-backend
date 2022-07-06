package com.dikkak.controller;

import com.dikkak.dto.common.BaseException;
import com.dikkak.dto.common.BaseResponse;
import com.dikkak.dto.user.PostRegisterReq;
import com.dikkak.dto.user.UserInfoRes;
import com.dikkak.dto.user.UserTypeReq;
import com.dikkak.entity.User;
import com.dikkak.entity.UserTypeEnum;
import com.dikkak.service.UserService;
import lombok.RequiredArgsConstructor;
import org.hibernate.usertype.UserType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 회원 타입 설정 API
     * @param req - type: CLIENT, DESIGNER
     */
    @PostMapping("/type")
    @ResponseBody
    public ResponseEntity<?> setUserType(@AuthenticationPrincipal Long userId,
                                         @RequestBody UserTypeReq req) {

        if(userId == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new BaseResponse(INVALID_ACCESS_TOKEN));

        try {
            userService.setUserType(userId, req.getType());
            return ResponseEntity.ok().body(null);
        } catch (BaseException e) {
            return ResponseEntity.badRequest().body(new BaseResponse(e));
        }
    }

    /**
     * 회원 정보 조회 API
     * @return username, type
     */
    @GetMapping("/info")
    public ResponseEntity<?> getUsername(@AuthenticationPrincipal Long userId) {

        if(userId == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new BaseResponse(INVALID_ACCESS_TOKEN));

        try {
            User user = userService.getUser(userId);
            return ResponseEntity.ok().body(new UserInfoRes(user.getName(), user.getUserType()));

        } catch (BaseException e) {
            return ResponseEntity.badRequest().body(new BaseResponse(e));
        }
    }

    private boolean isRegexPhoneNumber(String phoneNumber) {
        return PHONE_NUMBER.matcher(phoneNumber).find();
    }

}
