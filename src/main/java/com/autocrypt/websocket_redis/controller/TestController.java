package com.autocrypt.websocket_redis.controller;

import com.autocrypt.websocket_redis.session.SessionManager;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class TestController {

    private final SessionManager sessionManager;

    private final StringRedisTemplate stringRedisTemplate;

    //session에 직접 push
    @PostMapping("ws/{id}.send")
    public String send(@PathVariable("id") String id, @RequestBody String message) {
        sessionManager.sendMessage(id, message);
        return message;
    }

    //redis pub/sub으로 publish
    @PostMapping("redis/{id}.send")
    public String sendRedis(@PathVariable("id") String id, @RequestBody List<String> message) {
        stringRedisTemplate.convertAndSend(message.get(0), message.get(1));
        return message.toString();
    }
}
