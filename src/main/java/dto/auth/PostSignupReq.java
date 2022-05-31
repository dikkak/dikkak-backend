package dto.auth;

import com.dikkak.entity.UserTypeEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostSignupReq {

    private String email;
    private String name;
    private String password;
    private String phoneNumber;
    private boolean termsConditions;
    private boolean dataPolicy;
    private boolean popUpMessage;
    private boolean marketingMessage;

    @Builder
    public PostSignupReq(String email, String name, String password, String phoneNumber, boolean termsConditions, boolean dataPolicy, boolean popUpMessage, boolean marketingMessage) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.termsConditions = termsConditions;
        this.dataPolicy = dataPolicy;
        this.popUpMessage = popUpMessage;
        this.marketingMessage = marketingMessage;
    }
}
