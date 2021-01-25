package com.ds.feige.im.gateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.WebSocketSession;

import com.ds.feige.im.account.service.UserService;
import com.ds.feige.im.constants.SocketPaths;
import com.ds.feige.im.gateway.service.SessionUserService;
import com.ds.feige.im.gateway.socket.annotation.SocketController;
import com.ds.feige.im.gateway.socket.annotation.SocketRequestMapping;

@SocketController
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    SessionUserService sessionUserService;

    @SocketRequestMapping(SocketPaths.CS_PING_PONG)
    public long pingpong(WebSocketSession session) {
        sessionUserService.pingPong(session);
        return System.currentTimeMillis();
    }

}
