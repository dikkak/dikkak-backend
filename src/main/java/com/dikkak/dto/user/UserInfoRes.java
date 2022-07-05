package com.dikkak.dto.user;

import com.dikkak.entity.UserTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserInfoRes {

    private String username;
    private UserTypeEnum type;
}
