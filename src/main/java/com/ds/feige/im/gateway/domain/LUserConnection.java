package com.ds.feige.im.gateway.domain;

import com.ds.feige.im.common.util.JsonUtils;
import com.ds.feige.im.gateway.socket.connection.ConnectionMeta;
import com.ds.feige.im.gateway.socket.connection.UserConnection;
import com.ds.feige.im.gateway.socket.protocol.SocketRequest;
import org.springframework.util.Assert;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

/**
 * 本地用户链接对象
 * @author DC
 */
public class LUserConnection implements UserConnection {

    private WebSocketSession webSocketSession;
    private ConnectionMeta meta;

    public LUserConnection(ConnectionMeta meta, WebSocketSession webSocketSession) {
        Assert.notNull(webSocketSession, "websocketSession can not be null");
        this.webSocketSession = webSocketSession;
        this.meta = meta;
    }

    @Override
    public boolean send(SocketRequest request) throws IOException {
        if (request == null) {
            throw new NullPointerException("SocketRequest is null");
        }
        String jsonText = JsonUtils.toJson(request);
        TextMessage textMessage = new TextMessage(jsonText);
        this.webSocketSession.sendMessage(textMessage);
        return true;
    }

    @Override
    public boolean disconnect(SocketRequest reason) throws IOException {
        this.send(reason);
        this.webSocketSession.close();
        return true;
    }

    @Override
    public boolean disconnect() throws IOException {
        this.webSocketSession.close();
        return true;
    }

    @Override
    public ConnectionMeta getMeta() {
        return this.meta;
    }
}