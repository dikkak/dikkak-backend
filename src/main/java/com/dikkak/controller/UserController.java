package com.dikkak.controller;

import com.dikkak.entity.User;
import dto.*;
import com.dikkak.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")

    public BaseResponse<UserResponseDto> registerUser(@RequestBody UserRegisterDto userRegisterDto) {

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        try{
            User user = User.builder().email(userRegisterDto.getEmail())
                    .name(userRegisterDto.getName())
                    .password(passwordEncoder.encode(userRegisterDto.getPassword())) //암호화된 비밀번호로 저장
                    .phoneNumber(userRegisterDto.getPhoneNumber())
                    .build();

            User registeredUser = userService.create(user);

            return new BaseResponse<>(
                    UserResponseDto.builder()
                            .email(registeredUser.getEmail())
                            .name(registeredUser.getName())
                            .build()
            );

        } catch(BaseException e) {
            ResponseCode responseCode = e.getResponseCode();
            return new BaseResponse<>(responseCode);
        }
    }
}
