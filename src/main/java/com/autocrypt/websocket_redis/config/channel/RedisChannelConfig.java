package com.autocrypt.websocket_redis.config.channel;

import com.autocrypt.websocket_redis.channel.ListeningChannel;
import com.autocrypt.websocket_redis.channel.redis.RedisListeningChannel;
import com.autocrypt.websocket_redis.channel.redis.RedisSubscribeListener;
import com.autocrypt.websocket_redis.session.SessionManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
@Profile("redis")
public class RedisChannelConfig {

    /**
     * 김대호
     * pubsub을 지원하는 리스너 집합체
     * @param redisConnectionFactory spring redis 자동등록
     * @return
     */
    @Bean
    public RedisMessageListenerContainer redisMessageListener(RedisConnectionFactory redisConnectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        return container;
    }

    /**
     * 김대호
     * redis subscribe message handler
     * @param template spring redis 자동등록
     * @param objectMapper spring websocket -> spring web -> jackson 자동등록
     * @param sessionManager -> Orchestration 설정클래스에서 등록
     * @return
     */
    @Bean
    public MessageListener redisSubscribeListener(StringRedisTemplate template,
                                                  ObjectMapper objectMapper, SessionManager sessionManager) {
        return new RedisSubscribeListener(template, objectMapper, sessionManager);
    }

    /**
     * 김대호
     * redis용 ListeningChannel 구현체 등록
     * @param redisMessageListenerContainer -> RedisConfig에서 등록
     * @param redisMessageListener -> RedisConfig에서 등록
     * @return
     */
    @Bean
    public ListeningChannel redisListeningChannel(RedisMessageListenerContainer redisMessageListenerContainer,
                                                  MessageListener redisMessageListener) {
        ListeningChannel redisListeningChannel = new RedisListeningChannel(redisMessageListenerContainer, redisMessageListener);
        //채널 등록
        redisListeningChannel.register();
        return redisListeningChannel;
    }


}
