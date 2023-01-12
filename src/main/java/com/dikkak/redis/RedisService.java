package com.dikkak.redis;

import com.dikkak.common.BaseException;
import com.dikkak.dto.auth.token.TokenResponse;
import com.dikkak.entity.user.ProviderTypeEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.dikkak.common.ResponseMessage.EXPIRED_TOKEN;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RedisService {
    private final SocialTokenRedisRepository socialTokenRedisRepository;

    // redis에 token 저장
    @Transactional
    public void saveSocialToken(Long userId, ProviderTypeEnum provider, TokenResponse token,
                                @Nullable String providerUserId) {
        socialTokenRedisRepository.save(SocialToken.builder()
                .userId(userId)
                .provider(provider)
                .token(token.getAccessToken())
                .expiration(token.getExpiresIn())
                .providerUserId(providerUserId)
                .build());
    }

    // redis에서 token 가져오기
    public SocialToken getToken(Long userId) {
        return socialTokenRedisRepository.findById(userId)
                .orElseThrow(() -> new BaseException(EXPIRED_TOKEN));
    }

    // redis에서 token 삭제
    @Transactional
    public void deleteToken(SocialToken token) {
        socialTokenRedisRepository.delete(token);
    }
}
