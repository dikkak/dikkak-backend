package com.dikkak.redis;

import org.springframework.data.repository.CrudRepository;

public interface SocialTokenRedisRepository extends CrudRepository<SocialToken, Long> {
}
