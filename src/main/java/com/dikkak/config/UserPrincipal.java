package com.dikkak.config;

import com.dikkak.entity.user.User;
import com.dikkak.entity.user.UserTypeEnum;
import lombok.Data;

@Data
public class UserPrincipal {
    private Long userId;
    private UserTypeEnum type;

    public UserPrincipal(User user) {
        this.userId = user.getId();
        this.type = user.getUserType();
    }
}
