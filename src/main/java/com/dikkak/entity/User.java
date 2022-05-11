package com.dikkak.entity;

import lombok.*;

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
//    @Builder.Default
    private UserTypeEnum userType = UserTypeEnum.UNDEFINED;

    // 로그인 provider 타입 - 로컬, 구글, 페이스북, 네이버, 카카오
    @Enumerated(EnumType.STRING)
    @Column(name = "provider_type", nullable = false)
    private ProviderTypeEnum providerType;

    // 소셜 로그인 아이디
    @Column(name = "provider_id", unique = true)
    private String providerId;

    // 전화번호
    @Column(name = "phone_number", length = 11)
    private String phoneNumber;


    @Builder
    public User(String name, String email, String password, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.providerType = ProviderTypeEnum.LOCAL;
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
}
