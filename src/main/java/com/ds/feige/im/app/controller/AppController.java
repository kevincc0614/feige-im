package com.ds.feige.im.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ds.base.nodepencies.api.Response;
import com.ds.feige.im.account.dto.UserInfo;
import com.ds.feige.im.account.service.UserService;
import com.ds.feige.im.app.dto.AppInfo;
import com.ds.feige.im.app.service.AppService;

import cn.hutool.core.codec.Base64;

/**
 * @author DC
 */
@RestController
@RequestMapping("/app")
public class AppController {
    @Autowired
    AppService appService;
    @Autowired
    UserService userService;

    @GetMapping("/user/openid")
    public Response getUserOpenId(@RequestHeader("im-auth-token") String userToken, long appId) {
        AppInfo appInfo = appService.getApp(appId);
        UserInfo userInfo = userService.verifyToken(userToken);
        // TODO 理论上需要验证User是否属于企业员工,或者验证User和App的绑定关系
        String openId = Base64.encode(userInfo.getUserId() + "," + appInfo.getId());
        return new Response(openId);
    }
}
