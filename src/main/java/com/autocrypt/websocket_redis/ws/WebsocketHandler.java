package com.autocrypt.websocket_redis.ws;

import com.autocrypt.websocket_redis.session.SessionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

/**
 * 김대호
 * websocket 관련 통신이 발생하는 경우 트리거 되는 1차 handler
 */
@Slf4j
public class WebsocketHandler extends AbstractWebSocketHandler {
    private final SessionManager sessionManager;

    public WebsocketHandler(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userId = getUserIdFromSession(session);
        sessionManager.addSession(userId, session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String userId = getUserIdFromSession(session);
        log.info("Received text from user : {}, message: {}", userId, message);
    }

    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
        String userId = getUserIdFromSession(session);
        sessionManager.updateLastPongReceived(userId, System.currentTimeMillis());
        log.debug("🔄 Received Pong from : {}", userId);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String userId = getUserIdFromSession(session);
        log.debug("bye bye : {}", userId);
        sessionManager.removeSession(userId);
    }

    /**
     * 김대호
     * 클라이언트 특정 로직
     * 인증은 앞단에서 핸들링하도록 합의함
     * @param session
     * @return
     */
    //todo : user정보를 식별하는 방법에 대한 정의 필요
    private String getUserIdFromSession(WebSocketSession session) {
        String path = session.getUri().getPath();
        return path.substring(path.lastIndexOf('/') + 1);
    }


}