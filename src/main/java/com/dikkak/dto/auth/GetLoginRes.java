package com.dikkak.dto.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Builder
@Data
public class GetLoginRes {

    private boolean newUser; // 새로운 회원 여부

    private Long userId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String accessToken;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String refreshToken;

}
