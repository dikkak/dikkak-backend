package dto.auth;

import lombok.*;

@Builder
@Data
public class PostSigninRes {

    private Long userId;
    private String accessToken;
    private String refreshToken;
}
