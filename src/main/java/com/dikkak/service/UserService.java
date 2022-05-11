package com.dikkak.service;

import com.dikkak.entity.User;
import dto.BaseException;
import dto.ResponseCode;
import com.dikkak.repository.UserRepository;
import dto.UserRegisterDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User create(User user) throws BaseException {

        if(checkUserEmailExist(user.getEmail())) { //이메일이 이미 존재하는 경우
            throw new BaseException(ResponseCode.DUPLICATED_USER_EMAIL);
        }

        try{
            User registeredUser = userRepository.save(user);
            return registeredUser;
        } catch (Exception e) {
            throw new BaseException(ResponseCode.DATABASE_ERROR);
        }
    }

    boolean checkUserEmailExist(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if(user != null) return true;
        return false;
    }

}
