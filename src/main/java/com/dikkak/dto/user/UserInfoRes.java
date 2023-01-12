package com.dikkak.dto.user;

import com.dikkak.entity.user.ProviderTypeEnum;
import com.dikkak.entity.user.User;
import com.dikkak.entity.user.UserTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserInfoRes {

    private String email;
    private String username;
    private UserTypeEnum type;
    private ProviderTypeEnum provider;

    private String phoneNumber;
    private boolean marketingMessage;
    private boolean popUpMessage;

    public static UserInfoRes fromUser(User user) {
        return new UserInfoRes(
                user.getEmail(),
                user.getName(),
                user.getUserType(),
                user.getProviderType(),
                user.getPhoneNumber(),
                user.isMarketingMessage(),
                user.isPopUpMessage()
        );
    }
}
