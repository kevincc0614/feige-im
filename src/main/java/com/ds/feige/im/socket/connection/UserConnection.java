package com.ds.feige.im.socket.connection;

import com.ds.feige.im.socket.protocol.SocketRequest;

import java.io.IOException;

public interface UserConnection {

    boolean send(SocketRequest request) throws IOException;


    boolean disconnect(SocketRequest reason) throws IOException;

    boolean disconnect() throws IOException;

    ConnectionMeta getMeta();

}
