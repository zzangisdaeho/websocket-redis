package com.autocrypt.websocket_redis;

import com.autocrypt.websocket_redis.config.ServiceConfigProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@SpringBootApplication
@EnableWebSocket
@EnableConfigurationProperties(ServiceConfigProperties.class)
public class WebsocketRedisApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebsocketRedisApplication.class, args);
    }

}
