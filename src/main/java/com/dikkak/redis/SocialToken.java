package com.dikkak.redis;

import com.dikkak.entity.user.ProviderTypeEnum;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.lang.Nullable;

@Getter
@RedisHash("token")
public class SocialToken {
    @Id
    Long userId;
    String token;

    String providerUserId; // 페이스북 로그아웃을 위한 userId

    ProviderTypeEnum provider;

    @TimeToLive
    Integer expiration;

    @Builder
    public SocialToken(Long userId, ProviderTypeEnum provider, String token, Integer expiration,
                       @Nullable String providerUserId) {
        this.userId = userId;
        this.provider = provider;
        this.token = token;
        this.expiration = expiration;
        this.providerUserId = providerUserId;

    }
}
