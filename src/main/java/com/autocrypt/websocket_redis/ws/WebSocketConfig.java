package com.autocrypt.websocket_redis.ws;


import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Slf4j
public class WebSocketConfig implements WebSocketConfigurer {

    private final WebsocketHandler websocketHandler;

    public WebSocketConfig(WebsocketHandler websocketHandler) {
        this.websocketHandler = websocketHandler;
    }

    /**
     * 김대호
     * websocket endpoint 등록용
     * @param registry
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(websocketHandler, "/ws/{username}")
                .setAllowedOrigins("*"); // 모든 도메인 허용 (보안 설정 필요)
    }

    @PostConstruct
    public void init() {
        log.info("WebSocketConfig init...");
    }
}
