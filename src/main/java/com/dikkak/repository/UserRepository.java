package com.dikkak.repository;

import com.dikkak.entity.user.User;
import com.dikkak.entity.user.UserTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByPhoneNumber(String phoneNumber);

    @Query("select u.email from User u where u.userType = ?1 and u.status = 'ACTIVE'")
    List<String> findByUserType(UserTypeEnum userType);

}
