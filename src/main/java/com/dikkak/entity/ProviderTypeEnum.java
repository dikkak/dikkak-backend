package com.dikkak.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProviderTypeEnum {
    LOCAL("로컬"),
    GOOGLE("구글"),
    FACEBOOK("페이스북"),
    NAVER("네이버"),
    KAKAO("카카오");

    private String name;
}
