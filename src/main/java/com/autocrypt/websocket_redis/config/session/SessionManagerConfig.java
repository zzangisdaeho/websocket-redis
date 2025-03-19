package com.autocrypt.websocket_redis.config.session;

import com.autocrypt.websocket_redis.session.SessionManager;
import com.autocrypt.websocket_redis.session.repository.SessionRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class SessionManagerConfig {

    /**
     * 김대호
     * 요청량과 접속량을 예측할 수 없기 때문에 서버 자원만큼 broadcast 처리하도록 cachedPool을 선택함.
     * @return
     */
    @Bean
    public ExecutorService broadcastWorkerThreadPool() {
        return Executors.newCachedThreadPool();
    }

    @Bean
    public SessionManager sessionManager(SessionRepository sessionRepository, ExecutorService broadcastWorkerThreadPool) {
        return new SessionManager(sessionRepository, new ConcurrentHashMap<>(), broadcastWorkerThreadPool);
    }



}
