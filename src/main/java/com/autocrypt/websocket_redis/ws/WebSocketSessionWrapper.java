package com.autocrypt.websocket_redis.ws;

import com.autocrypt.websocket_redis.session.SessionManager;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.PingMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.*;

/**
 * 김대호
 * session이 살아있는지 healthcheck하기 위한 기능을 session에 덮어씌운 wrapper
 */
@Slf4j
@Getter
public class WebSocketSessionWrapper {
    private final WebSocketSession session;
    private final SessionManager sessionManager;
    private final String userId;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private long lastPongReceived = System.currentTimeMillis();

    public WebSocketSessionWrapper(String userId, WebSocketSession session, SessionManager sessionManager) {
        this.userId = userId;
        this.session = session;
        this.sessionManager = sessionManager;
        startPingTask();
    }

    private void startPingTask() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                if (!session.isOpen()) {
                    stop();
                    return;
                }
                session.sendMessage(new PingMessage(ByteBuffer.allocate(0)));
                log.debug("📤 Sent Ping to : {}", userId);

                // 마지막 Pong 응답이 10초 이상 없으면 세션 종료
                if (System.currentTimeMillis() - lastPongReceived > 10000) {
                    log.warn("❌ User {} is inactive. Closing session.", userId);
                    sessionManager.removeSession(userId);
                    this.stop();
                }
            } catch (Exception e) {
                log.error("Error while sending ping to {}: {}", userId, e.getMessage());
                sessionManager.removeSession(userId);
            }
        }, 3, 3, TimeUnit.SECONDS);
    }

    /**
     * 김대호
     * session 소켓자원임으로 필히 close해야함
     * scheduler 별도 쓰레드풀로 필이 close해야함
     */
    public void stop() {
        try (session){
            scheduler.shutdown();
        } catch (IOException e) {
            log.error("Error while closing session: {}", e.getMessage());
        }
    }

    public void send(String message){
        if (session.isOpen()) {
            try {
               session.sendMessage(new TextMessage(message));
               log.debug("send success to : {}, message : {}", userId, message);
            } catch (Exception e) {
                log.warn("Error sending message start retry", e);
                this.retrySend(message, 3);
            }
        }
    }

    /**
     * 김대호
     * 일시적 통신 장애인 경우를 고려하여 n번까지 재전송 시도.
     * @param message
     * @param retryCount
     */
    private void retrySend(String message, int retryCount) {
        for (int attempt = 1; attempt <= retryCount; attempt++) {
            try {
                session.sendMessage(new TextMessage(message));
                log.warn("📤 Message sent retry count {} to {}: {}", attempt, userId, message);
                return;
            } catch (Exception e) {
                log.warn("⚠️ Attempt {}/{} failed to send message to {}. Retrying...", attempt, retryCount, userId);
                try {
                    Thread.sleep(1000 * attempt); // 1초, 2초, 3초 대기 후 재시도 (지수적 backoff)
                } catch (InterruptedException ignored) {
                    break;
                }
            }
        }
        // 3번 실패 시 세션 종료
        log.error("❌ Message to {} failed after {} attempts. Closing session.", userId, retryCount);
        this.sessionManager.removeSession(userId);
    }

    public void updateLastPongReceived(long timestamp) {
        this.lastPongReceived = timestamp;
    }
}