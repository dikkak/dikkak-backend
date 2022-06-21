package com.dikkak.entity;

import com.dikkak.dto.auth.PostSignupReq;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import javax.persistence.*;
import javax.validation.constraints.Email;

@Entity
@NoArgsConstructor
@Getter
@ToString
public class User extends BaseTime {

    @Id @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    // 이름
    @Column(nullable = false, length = 100)
    private String name;

    // 이메일
    @Email
    @Column(unique = true, length = 100)
    private String email;

    // 비밀번호
    private String password;

    // 사용자 타입 - 미정의(기본값), 디자이너, 의뢰인
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private UserTypeEnum userType = UserTypeEnum.UNDEFINED;

    // 로그인 provider 타입 - 로컬, 구글, 페이스북, 네이버, 카카오
    @Enumerated(EnumType.STRING)
    @Column(name = "provider_type")
    @ColumnDefault("'LOCAL'")
    private ProviderTypeEnum providerType;

    // 소셜 로그인 아이디
    @Column(name = "provider_id", unique = true)
    private String providerId;

    // 전화번호
    @Column(name = "phone_number", length = 13)
    private String phoneNumber;

    // 이용약관 동의 여부
    @Column(name = "terms_conditions")
    @ColumnDefault("true")
    private boolean termsConditions;

    // 개인정보 처리 방침 동의 여부
    @Column(name = "personal_data_processing_policy")
    @ColumnDefault("true")
    private boolean dataPolicy;

    // 팝업 메시지 제공 동의 여부
    @Column(name = "popup_message")
    @ColumnDefault("true")
    private boolean popUpMessage;

    // 마케팅 메시지 수신 동의 여부
    @Column(name = "marketing_message")
    @ColumnDefault("true")
    private boolean marketingMessage;

    private String refreshToken;

    @Builder
    public User(String name, String email, String password, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }

    public User(PostSignupReq req) {
        this.name = req.getName();
        this.email = req.getEmail();
        this.password = req.getPassword();
        this.phoneNumber = req.getPhoneNumber();
        this.termsConditions = req.isTermsConditions();
        this.dataPolicy = req.isDataPolicy();
        this.popUpMessage = req.isPopUpMessage();
        this.marketingMessage = req.isMarketingMessage();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserType(UserTypeEnum userType) {
        this.userType = userType;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
