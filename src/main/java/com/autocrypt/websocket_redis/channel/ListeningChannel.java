package com.autocrypt.websocket_redis.channel;

/**
 * connection이 맺어진 클라이언트로 전송하기 위한 외부 통신포인트 규격
 */
public interface ListeningChannel {

    String WEBSOCKET_CHANNEL = "websocket_channel";

    //내부 서버들에서 실시간 통신으로 특정 유저에게 데이터를 보내달라는 요청을 받기위한 채널을 등록함 (유니캐스트)
    void registerUnicastListener();

    //내부 서버들에서 실시간 통신으로 특정 유저에게 데이터를 보내달라는 요청을 받기위한 채널을 등록함 (브로드캐스트)
    void registerBroadCastListener();

    default void register(){
        registerUnicastListener();
        registerBroadCastListener();
    }
}
