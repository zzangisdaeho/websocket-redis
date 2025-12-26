package com.autocrypt.websocket_redis.channel.redis;

import com.autocrypt.websocket_redis.channel.dto.ChannelRequest;
import com.autocrypt.websocket_redis.session.SessionManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 김대호
 * Redis subscribe 라이브러리가 핸들러를 별도로 구현하여 트리거하도록 작동함
 * 리스너 자체가 멀티쓰레드로 구성되어 부하 처리 하는것으로 보임
 */
@RequiredArgsConstructor
@Slf4j
public class RedisSubscribeListener implements MessageListener {

    private final StringRedisTemplate template;
    private final ObjectMapper objectMapper;
    private final SessionManager sessionManager;


    @Override
    public void onMessage(Message message, byte[] pattern) {
        String publishMessage = template
                .getStringSerializer().deserialize(message.getBody());

        log.debug("Redis SUB from CHANNEL : {}", new String(message.getChannel()));
        log.debug("Redis SUB Message : {}", publishMessage);
        ChannelRequest channelRequest = parsing(publishMessage);
        log.debug("receive channel request : {}", channelRequest);

        String formattedMessage = this.formatMessage(channelRequest);

        if (!channelRequest.isBroadcast()) {
            sessionManager.sendMessage(channelRequest.getTo(), formattedMessage);
        } else {
            sessionManager.sendMessage(formattedMessage);
        }

    }

    /**
     * JSON으로 변환할 데이터 구조를 동적으로 설정
     */
    private String formatMessage(ChannelRequest channelRequest) {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("trxId", channelRequest.getTrxId());
        //메세지가 전송되었어야 할 시간 -> 나중에 전송받지 못한 메세지를 관리하는 값으로 쓰일 수 있다.
        jsonMap.put("msgTime", System.currentTimeMillis());
        jsonMap.put("event", channelRequest.getEvent());
        jsonMap.put("msg", channelRequest.getMsg());

        try {
            return objectMapper.writeValueAsString(jsonMap);
        } catch (JsonProcessingException e) {
            log.error("🚨 Failed to serialize message", e);
            return "{}"; // 변환 실패 시 빈 JSON 반환
        }
    }

    private ChannelRequest parsing(String publishMessage) {
        try {
            return objectMapper.readValue(publishMessage, ChannelRequest.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}