package com.autocrypt.websocket_redis.session;

import com.autocrypt.websocket_redis.session.repository.SessionRepository;
import com.autocrypt.websocket_redis.ws.WebSocketSessionWrapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.socket.WebSocketSession;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * 김대호
 * 해당 서버에 붙어있는 session들을 중앙관리하기 위한 Manager
 * repository, channel, session사이의 중개 역할
 */
@Slf4j
public class SessionManager {
    public final static String HOST_NAME;

    private final SessionRepository sessionRepository;
    private final Map<String, WebSocketSessionWrapper> sessions;
    private final ExecutorService broadcastWorkers;

    static {
        try {
            HOST_NAME = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public SessionManager(SessionRepository sessionRepository, Map<String, WebSocketSessionWrapper> sessions, ExecutorService broadcastWorkers) {
        this.sessionRepository = sessionRepository;
        this.sessions = sessions;
        this.broadcastWorkers = broadcastWorkers;
    }

    public void addSession(String userId, WebSocketSession session) {
        WebSocketSessionWrapper sessionWrapper = new WebSocketSessionWrapper(userId, session, this);
        sessions.put(userId, sessionWrapper);
        sessionRepository.save(userId);
    }

    public void removeSession(String userId) {
        WebSocketSessionWrapper sessionWrapper = sessions.remove(userId);
        if (sessionWrapper != null) {
            sessionWrapper.stop();
        }
        sessionRepository.remove(userId);
    }

    /**
     * 김대호
     * 특정 유저에게만 전송
     *
     * @param userId  유저 아이디
     * @param message 보낼 내용
     */
    public void sendMessage(String userId, String message) {
        WebSocketSessionWrapper sessionWrapper = sessions.get(userId);
        if (sessionWrapper != null) sessionWrapper.send(message);
    }


    /**
     * 김대호
     * 해당 서버에 붙어잇는 모든 유저에게 전송
     * 커넥션이 많으면 느려질 수 있기 때문에 병렬처리하도록 구성
     * @param message 전송할 내용
     */
    public void sendMessage(String message) {
        //아래 부분에서 하나씩 sync로 send를 호출하면 느릴텐데 어떻게 해결해야할까?
        sessions.forEach((userId, webSocketSessionWrapper) -> {
                    if (webSocketSessionWrapper != null) broadcastWorkers.submit(()->webSocketSessionWrapper.send(message));
                }
        );
    }

    public void updateLastPongReceived(String userId, long timestamp) {
        WebSocketSessionWrapper sessionWrapper = sessions.get(userId);
        if (sessionWrapper != null) {
            sessionWrapper.updateLastPongReceived(timestamp);
        }
    }

    @PostConstruct
    public void init() {
        log.info("SessionManager init...");
        log.info("HOST_NAME: {}", HOST_NAME);
    }

    /**
     * 김대호
     * sigint를 받을 시 redis connection이 먼저 종료되는 일이 있었음
     * 세션정보가 redis에 정상 삭제되지 않는것은 큰 문제점이기에, 별도릐 리스너를 달아서 커넥션이 끊기기 전에 정리하도록 해줌
     *
     * @param event
     */
    @EventListener(ContextClosedEvent.class)
    public void onApplicationEvent(ContextClosedEvent event) {
        cleanUpBeforeShutdown();
    }

    private void cleanUpBeforeShutdown() {
        log.info("🔴 Cleaning up sessions before shutdown...");
        sessions.forEach((userId, sessionWrapper) -> {
            removeSession(userId);
        });
    }
}