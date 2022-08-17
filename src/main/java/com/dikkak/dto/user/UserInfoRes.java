package com.dikkak.dto.user;

import com.dikkak.entity.ProviderTypeEnum;
import com.dikkak.entity.UserTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserInfoRes {

    private String email;
    private String username;
    private UserTypeEnum type;
    private ProviderTypeEnum provider;
}
