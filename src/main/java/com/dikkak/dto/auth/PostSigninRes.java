package com.dikkak.dto.auth;

import lombok.*;

@Builder
@Data
public class PostSigninRes {

    private String accessToken;
    private String refreshToken;
}
