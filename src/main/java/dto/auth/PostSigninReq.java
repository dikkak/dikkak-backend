package dto.auth;

import lombok.Getter;

@Getter
public class PostSigninReq {

    private String email;
    private String password;
}
