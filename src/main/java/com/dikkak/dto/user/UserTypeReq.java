package com.dikkak.dto.user;

import com.dikkak.entity.UserTypeEnum;
import lombok.Data;

@Data
public class UserTypeReq {

    private UserTypeEnum type;
}
