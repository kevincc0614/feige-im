package com.ds.feige.im.chat.dto.event;

import com.ds.feige.im.gateway.socket.connection.ConnectionMeta;
import com.ds.feige.im.gateway.socket.protocol.SocketRequest;

import lombok.Data;

@Data
public class MessageForwardEvent {
    /** 源服务器ID */
    private String sourceInstanceId;
    private SocketRequest request;
    private ConnectionMeta meta;
}
