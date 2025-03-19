package com.autocrypt.websocket_redis.channel.redis;

import com.autocrypt.websocket_redis.channel.ListeningChannel;
import com.autocrypt.websocket_redis.session.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * 김대호
 * ws쪽 트리거 endpoint
 * redis 구현체
 */
@Slf4j
@RequiredArgsConstructor
public class RedisListeningChannel implements ListeningChannel {
    private final RedisMessageListenerContainer container;
    private final MessageListener messageListener;

    //해당 서버 전용 리스너 등록
    @Override
    public void registerUnicastListener() {
        log.info("Registering unicast listener for channel : {}", SessionManager.HOST_NAME);
        container.addMessageListener(messageListener, ChannelTopic.of(SessionManager.HOST_NAME));
    }

    //user websocket 서버용 broadcast channel 등록
    @Override
    public void registerBroadCastListener() {
        log.info("Registering broadcast listener for channel : {}", ListeningChannel.WEBSOCKET_CHANNEL);
        container.addMessageListener(messageListener, ChannelTopic.of(ListeningChannel.WEBSOCKET_CHANNEL));
    }
}