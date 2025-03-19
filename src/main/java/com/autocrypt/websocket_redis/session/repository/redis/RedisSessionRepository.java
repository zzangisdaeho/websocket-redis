package com.autocrypt.websocket_redis.session.repository.redis;

import com.autocrypt.websocket_redis.session.SessionManager;
import com.autocrypt.websocket_redis.session.repository.SessionRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 김대호
 * connection 정보를 redis에 보관하기 위한 구현체
 */
@Slf4j
public class RedisSessionRepository implements SessionRepository {
    private final StringRedisTemplate redisTemplate;
    private final String redisPrefix;

    public RedisSessionRepository(StringRedisTemplate redisTemplate, String servicePrefix) {
        this.redisTemplate = redisTemplate;
        this.redisPrefix = servicePrefix;
    }

    @Override
    public void save(String userId) {
        redisTemplate.opsForValue().set(redisPrefix + ":" + userId, SessionManager.HOST_NAME);
    }

    @Override
    public void remove(String userId) {
        redisTemplate.delete(redisPrefix + ":" + userId);
    }

    @Override
    public String get(String userId) {
        return redisTemplate.opsForValue().get(redisPrefix + ":"+userId);
    }

    @PostConstruct
    public void init() {
        log.info("Initializing Redis SessionRepository. redisPrefix: {}", redisPrefix);
    }
}