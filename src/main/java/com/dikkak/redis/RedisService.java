package com.dikkak.redis;

import com.dikkak.dto.common.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.dikkak.dto.common.ResponseMessage.REDIS_ERROR;
import static com.dikkak.dto.common.ResponseMessage.WRONG_USER_ID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {
    private final SocialTokenRedisRepository socialTokenRedisRepository;

    // redis에 token 저장
    @Transactional
    public void saveSocialToken(Long userId, String token) throws BaseException {
        try {
            socialTokenRedisRepository.save(SocialToken.builder()
                    .user_id(userId)
                    .token(token)
                    .build());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(REDIS_ERROR);
        }

    }

    // redis에서 token 가져오기
    public String getToken(Long userId) throws BaseException {
        try {
            return socialTokenRedisRepository.findById(userId)
                    .orElseThrow(() -> new BaseException(WRONG_USER_ID))
                    .getToken();
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            throw new BaseException(REDIS_ERROR);
        }

    }
}
