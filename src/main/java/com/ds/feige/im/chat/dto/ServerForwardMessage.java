package com.ds.feige.im.chat.dto;

import com.ds.feige.im.gateway.socket.connection.ConnectionMeta;
import com.ds.feige.im.gateway.socket.protocol.SocketRequest;

public class ServerForwardMessage {
    /**源服务器ID*/
    private String sourceInstanceId;
    private SocketRequest request;
    private ConnectionMeta meta;

    public String getSourceInstanceId() {
        return sourceInstanceId;
    }

    public void setSourceInstanceId(String sourceInstanceId) {
        this.sourceInstanceId = sourceInstanceId;
    }

    public SocketRequest getRequest() {
        return request;
    }

    public void setRequest(SocketRequest request) {
        this.request = request;
    }

    public ConnectionMeta getMeta() {
        return meta;
    }

    public void setMeta(ConnectionMeta meta) {
        this.meta = meta;
    }
}
