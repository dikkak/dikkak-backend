package com.dikkak.service;

import com.dikkak.common.BaseException;
import com.dikkak.dto.user.PostRegisterReq;
import com.dikkak.entity.user.User;
import com.dikkak.entity.user.UserTypeEnum;
import com.dikkak.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.dikkak.common.ResponseMessage.WRONG_USER_ID;
import static com.dikkak.entity.user.UserTypeEnum.ADMIN;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User create(User user) {
        return userRepository.save(user);
    }

    public User getUser(Long userId) {
        return findUserById(userId);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Transactional
    public void registerUser(Long userId, PostRegisterReq req) {
        findUserById(userId).register(req);
    }

    @Transactional
    public void setUserType(Long userId, UserTypeEnum type) {
        findUserById(userId).setUserType(type);
    }

    private boolean checkUserEmailExist(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(WRONG_USER_ID));
    }

    // 관리자 이메일 목록 조회
    public List<String> getAdminEmail() {
        return userRepository.findByUserType(ADMIN);
    }

    /**
     * 사용하지 않음
     * 로컬 로그인
     */
    // 로그인
//    public User authenticate(String email, String password) {
//
//        User user;
//        try{
//            user = userRepository.findByEmail(email).orElse(null);
//        } catch (Exception e) {
//            throw new BaseException(ResponseMessage.DATABASE_ERROR);
//        }
//
//        // 존재하지 않는 이메일
//        if(user == null) {
//            throw new BaseException(ResponseMessage.NON_EXISTENT_EMAIL);
//        }
//
//        // 로그인 성공
//        if(passwordEncoder.matches(password, user.getPassword())) {
//            return user;
//        }
//        else { // 비밀번호가 맞지 않는 경우
//            throw new BaseException(ResponseMessage.INCORRECT_PASSWORD);
//        }
//    }

    /**
     * 사용하지 않음
     * 로컬 회원가입
     */
    // 회원 생성
//    public PostSignupRes create(User user) {
//
//        if(checkUserEmailExist(user.getEmail())) { //이메일이 이미 존재하는 경우
//            throw new BaseException(ResponseMessage.DUPLICATED_USER_EMAIL);
//        }
//
//        // 비밀번호 암호화
//        // user.setPassword(passwordEncoder.encode(user.getPassword()));
//
//        try{
//            User registeredUser = userRepository.save(user);
//            return PostSignupRes.builder().userId(registeredUser.getId()).build();
//
//        } catch (Exception e) {
//            throw new BaseException(ResponseMessage.DATABASE_ERROR);
//        }
//    }








}
