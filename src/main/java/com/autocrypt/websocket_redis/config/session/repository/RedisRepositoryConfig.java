package com.autocrypt.websocket_redis.config.session.repository;

import com.autocrypt.websocket_redis.config.ServiceConfigProperties;
import com.autocrypt.websocket_redis.session.repository.SessionRepository;
import com.autocrypt.websocket_redis.session.repository.redis.RedisSessionRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
@Profile("redis")
public class RedisRepositoryConfig {

    /**
     * 김대호
     * sessionRepository redis구현체 등록
     * @param redisTemplate spring redis 자동 등록
     * @param serviceConfigProperties property 등록코드 참조(ServiceConfigProperties)
     * @return
     */
    @Bean
    public SessionRepository sessionRepository(StringRedisTemplate redisTemplate, ServiceConfigProperties serviceConfigProperties) {
        return new RedisSessionRepository(redisTemplate,serviceConfigProperties.getPrefix());
    }
}
