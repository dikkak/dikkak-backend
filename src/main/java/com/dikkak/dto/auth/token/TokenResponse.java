package com.dikkak.dto.auth.token;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Getter
@ToString
public class TokenResponse {

    @JsonProperty("token_type")
    String tokenType;

    @JsonProperty("access_token")
    String accessToken;

    @JsonProperty("refresh_token")
    String refreshToken;

    @JsonProperty("expires_in")
    Integer expiresIn;

    @JsonProperty("refresh_token_expires_in")
    Integer refreshTokenExpiresIn;

    String scope;
}
