package com.dikkak.entity.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProviderTypeEnum {
    GOOGLE("구글"),
    FACEBOOK("페이스북"),
    KAKAO("카카오");

    private String name;
}
