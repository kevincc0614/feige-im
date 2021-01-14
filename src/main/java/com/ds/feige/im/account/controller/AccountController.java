package com.ds.feige.im.account.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.ds.base.nodepencies.api.Response;
import com.ds.feige.im.account.dto.GetTokenRequest;
import com.ds.feige.im.account.dto.LogoutRequest;
import com.ds.feige.im.account.dto.UserInfo;
import com.ds.feige.im.account.dto.UserRegisterRequest;
import com.ds.feige.im.account.service.UserService;
import com.ds.feige.im.common.web.WebUtils;
import com.ds.feige.im.gateway.service.SessionUserService;

/**
 * @author DX
 */
@RestController
@RequestMapping("/user")
public class AccountController {
    @Autowired
    UserService userService;
    @Autowired
    SessionUserService sessionUserService;
    @PostMapping("/get-token")
    @ResponseBody
    @NodAuthorizedRequest
    public Response<String> getToken(@RequestBody @Valid GetTokenRequest request) {
        String token = userService.getToken(request);
        return new Response<>(token);
    }

    @RequestMapping("/profile")
    @NodAuthorizedRequest
    public Response<UserInfo> getUserProfile(@RequestHeader(value = "im-auth-token", required = true) String token) {
        UserInfo userInfo = userService.verifyToken(token);
        userInfo.setPassword(null);
        return new Response<>(userInfo);
    }

    @PostMapping("/register")
    @NodAuthorizedRequest
    public Response register(@RequestBody @Valid UserRegisterRequest request) {
        long userId = userService.register(request);
        return new Response(userId);
    }

    @PostMapping("/logout")
    public Response logout(HttpServletRequest request, @RequestHeader(value = "deviceId") String deviceId)
        throws Exception {
        LogoutRequest logout = new LogoutRequest();
        Long userId = WebUtils.getUserId(request);
        logout.setUserId(userId);
        logout.setDeviceId(deviceId);
        sessionUserService.logout(logout);
        return Response.EMPTY_SUCCESS;
    }
}
