package com.ds.feige.im.gateway.socket.connection;

import java.io.IOException;

import com.ds.feige.im.gateway.socket.protocol.SocketPacket;

public interface UserConnection {

    boolean send(SocketPacket request) throws IOException;


    boolean disconnect(SocketPacket reason) throws IOException;

    boolean disconnect() throws IOException;

    ConnectionMeta getMeta();

    String getId();

}
