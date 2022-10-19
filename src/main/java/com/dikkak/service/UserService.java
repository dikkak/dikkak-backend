package com.dikkak.service;

import com.dikkak.dto.user.PostRegisterReq;
import com.dikkak.entity.user.User;
import com.dikkak.common.BaseException;
import com.dikkak.entity.user.UserTypeEnum;
import com.dikkak.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.dikkak.common.ResponseMessage.DATABASE_ERROR;
import static com.dikkak.common.ResponseMessage.WRONG_USER_ID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

//    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User create(User user) throws BaseException {
        try {
            return userRepository.save(user);

        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public User getUser(Long userId) throws BaseException {
        return findUserById(userId);
    }

    public User getUserByEmail(String email) throws BaseException {
        try{
            return userRepository.findByEmail(email).orElse(null);
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional
    public void registerUser(Long userId, PostRegisterReq req) throws BaseException {
        User user = findUserById(userId);
        try{
            user.register(req);
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional
    public void setUserType(Long userId, UserTypeEnum type) throws BaseException {
        User user = findUserById(userId);
        try {
            user.setUserType(type);
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    private boolean checkUserEmailExist(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        return user != null;
    }

    private User findUserById(Long userId) throws BaseException {
        Optional<User> user;
        try{
            user = userRepository.findById(userId);
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }

        if(user.isEmpty()) throw new BaseException(WRONG_USER_ID);
        return user.get();
    }

    /**
     * 사용하지 않음
     * 로컬 로그인
     */
    // 로그인
//    public User authenticate(String email, String password) throws BaseException {
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
//    public PostSignupRes create(User user) throws BaseException {
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
