package com.autocrypt.websocket_redis.channel;

import lombok.Data;

import java.util.UUID;

/**
 * 김대호
 * websocket <-> redis pub/sub 용 msg형식
 */
@Data
public class ChannelRequest {

    //메세지 포함 트랜젝션 아이디
    private String trxId = UUID.randomUUID().toString();

    //수신자
    private String to;

    //그룹 메세지 여부 (기능 미구현)
    private String group;

    //내부 정의 이벤트 값
    private String event;

    //어떤 채널에 게시될건지 고유값
    private String channel;

    //메세지 본문
    private String msg;

    //broadcast 채널에 개시 여부
    private boolean broadcast;

}
