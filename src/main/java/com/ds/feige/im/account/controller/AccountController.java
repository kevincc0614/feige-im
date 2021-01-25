package com.ds.feige.im.account.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.ds.base.nodepencies.api.Response;
import com.ds.base.nodepencies.exception.WarnMessageException;
import com.ds.feige.im.account.dto.GetTokenRequest;
import com.ds.feige.im.account.dto.LogoutRequest;
import com.ds.feige.im.account.dto.UserInfo;
import com.ds.feige.im.account.dto.UserRegisterRequest;
import com.ds.feige.im.account.service.UserService;
import com.ds.feige.im.constants.FeigeWarn;
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
    public Response logout(@RequestHeader(value = "im-auth-token") String token, @RequestParam String deviceId)
        throws Exception {
        Long userId = userService.logout(token);
        if (userId == null) {
            throw new WarnMessageException(FeigeWarn.TOKEN_IS_INVALID);
        }
        LogoutRequest logout = new LogoutRequest();
        logout.setUserId(userId);
        logout.setDeviceId(deviceId);
        logout.addProperty("type", 0);
        sessionUserService.logout(logout);
        return Response.EMPTY_SUCCESS;
    }
}
