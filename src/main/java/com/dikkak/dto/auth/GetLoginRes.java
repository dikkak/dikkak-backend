package com.dikkak.dto.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Builder
@Data
public class GetLoginRes {

    private String username;

    private String accessToken;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String refreshToken;

}
