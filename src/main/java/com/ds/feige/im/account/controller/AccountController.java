package com.ds.feige.im.account.controller;

import com.ds.base.nodepencies.api.Response;
import com.ds.feige.im.account.dto.GetTokenRequest;
import com.ds.feige.im.account.dto.UserInfo;
import com.ds.feige.im.account.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author DX
 */
@RestController
@RequestMapping("/user")
public class AccountController {
    @Autowired
    UserService userService;

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
        return new Response<>(userService.verifyToken(token));
    }
}
