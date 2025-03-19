package com.autocrypt.websocket_redis.config.ws;

import com.autocrypt.websocket_redis.session.SessionManager;
import com.autocrypt.websocket_redis.ws.WebsocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringWebSocketConfig {

    @Bean
    public WebsocketHandler websocketHandler(SessionManager sessionManager) {
        return new WebsocketHandler(sessionManager);
    }

    @Bean
    public com.autocrypt.websocket_redis.ws.WebSocketConfig webSocketConfig(WebsocketHandler websocketHandler) {
        return new com.autocrypt.websocket_redis.ws.WebSocketConfig(websocketHandler);
    }
}
