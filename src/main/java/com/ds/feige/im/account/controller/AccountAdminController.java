package com.ds.feige.im.account.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ds.base.nodepencies.api.Response;
import com.ds.feige.im.account.dto.UnregisterRequest;
import com.ds.feige.im.account.service.UserService;

/**
 * @author DC
 */
@RequestMapping("/admin/user")
@RestController
public class AccountAdminController {
    @Autowired
    UserService userService;

    @RequestMapping("/unregister")
    public Response unregister(@RequestBody UnregisterRequest request) {
        userService.unregisterUser(request.getUserId(), request.getOperatorId());
        return Response.EMPTY_SUCCESS;
    }
}
