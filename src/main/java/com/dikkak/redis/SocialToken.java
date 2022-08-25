package com.dikkak.redis;

import com.dikkak.entity.user.ProviderTypeEnum;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@RedisHash("token")
public class SocialToken {
    @Id
    Long user_id;
    String token;

    ProviderTypeEnum provider;

    @TimeToLive
    Integer expiration;

    @Builder
    public SocialToken(Long user_id, ProviderTypeEnum provider, String token, Integer expiration) {
        this.user_id = user_id;
        this.provider = provider;
        this.token = token;
        this.expiration = expiration;
    }
}
