package com.ds.feige.im.push.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ds.base.nodepencies.api.Response;
import com.ds.feige.im.account.controller.NodAuthorizedRequest;
import com.ds.feige.im.common.web.WebUtils;
import com.ds.feige.im.gateway.service.UserDeviceService;
import com.ds.feige.im.push.dto.RegisterTokenRequest;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

/**
 * @author DC
 */
@RestController
@RequestMapping("/push")
public class PushController {
    @Autowired
    FirebaseMessaging firebaseMessaging;
    @Autowired
    UserDeviceService userDeviceService;

    @RequestMapping("/test")
    @NodAuthorizedRequest
    public Response testPush(String token) throws Exception {
        Message message = Message.builder().setToken(token)
            .setNotification(Notification.builder().setTitle("test title").setBody("test body").build()).build();
        firebaseMessaging.send(message);
        return Response.EMPTY_SUCCESS;
    }
    @RequestMapping("/device-token/register")
    public Response registerToken(HttpServletRequest request, @RequestBody RegisterTokenRequest registerToken) {
        Long userId = WebUtils.getUserId(request);
        registerToken.setUserId(userId);
        userDeviceService.registerToken(registerToken);
        return Response.EMPTY_SUCCESS;
    }
}
