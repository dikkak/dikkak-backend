package com.dikkak.dto.user;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class PostRegisterReq {

    private String username;
    private String phoneNumber;
    private boolean termsConditions;
    private boolean dataPolicy;
    private boolean popUpMessage;
    private boolean marketingMessage;
}
