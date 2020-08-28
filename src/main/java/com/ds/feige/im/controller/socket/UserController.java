package com.ds.feige.im.controller.socket;

import com.ds.feige.im.service.user.SessionUserService;
import com.ds.feige.im.service.user.UserService;
import com.ds.feige.im.socket.annotation.SocketController;
import com.ds.feige.im.socket.annotation.SocketRequestMapping;
import org.springframework.beans.factory.annotation.Autowired;

@SocketController
@SocketRequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    SessionUserService sessionUserService;

}
