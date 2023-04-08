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
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    @Transactional
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

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(WRONG_USER_ID));
    }

    // 관리자 이메일 목록 조회
    public List<String> getAdminEmail() {
        return userRepository.findByUserType(ADMIN);
    }
}
