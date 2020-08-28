package com.ds.feige.im.gateway.controller;

import com.ds.feige.im.account.service.UserService;
import com.ds.feige.im.gateway.service.SessionUserService;
import com.ds.feige.im.gateway.socket.annotation.SocketController;
import com.ds.feige.im.gateway.socket.annotation.SocketRequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.WebSocketSession;

@SocketController
@SocketRequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    SessionUserService sessionUserService;

    @SocketRequestMapping("/ping-pong")
    public void pingpong(WebSocketSession session) {

    }
}
