package com.dikkak.service;

import com.dikkak.entity.User;
import dto.auth.PostSignupRes;
import dto.common.BaseException;
import dto.common.ResponseMessage;
import com.dikkak.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // 회원 생성
    public PostSignupRes create(User user) throws BaseException {

        if(checkUserEmailExist(user.getEmail())) { //이메일이 이미 존재하는 경우
            throw new BaseException(ResponseMessage.DUPLICATED_USER_EMAIL);
        }

        // 비밀번호 암호화
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        try{
            User registeredUser = userRepository.save(user);
            return PostSignupRes.builder().userId(registeredUser.getId()).build();

        } catch (Exception e) {
            throw new BaseException(ResponseMessage.DATABASE_ERROR);
        }
    }

    private boolean checkUserEmailExist(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if(user != null) return true;
        return false;
    }

    // 로그인
    public User authenticate(String email, String password) throws BaseException {

        User user;
        try{
            user = userRepository.findByEmail(email).orElse(null);
        } catch (Exception e) {
            throw new BaseException(ResponseMessage.DATABASE_ERROR);
        }

        // 존재하지 않는 이메일
        if(user == null) {
            throw new BaseException(ResponseMessage.NON_EXISTENT_EMAIL);
        }

        // 로그인 성공
        if(passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        else { // 비밀번호가 맞지 않는 경우
            throw new BaseException(ResponseMessage.INCORRECT_PASSWORD);
        }

    }

    @Transactional
    public void setUserRefreshToken(User user, String refreshToken) throws BaseException {
        try {
            user.setRefreshToken(refreshToken);
        } catch(Exception e) {
            throw new BaseException(ResponseMessage.DATABASE_ERROR);
        }
    }






}
