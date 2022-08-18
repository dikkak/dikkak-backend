package com.dikkak.redis;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash("token")
public class SocialToken {
    @Id
    Long user_id;
    String token;

    @Builder
    public SocialToken(Long user_id, String token) {
        this.user_id = user_id;
        this.token = token;
    }
}
