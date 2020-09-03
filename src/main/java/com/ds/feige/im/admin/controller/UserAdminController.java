package com.ds.feige.im.admin.controller;

import com.ds.base.nodepencies.api.Response;
import com.ds.feige.im.account.service.UserService;
import com.ds.feige.im.admin.dto.UnregisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 用户后台管理
 *
 * @author DC
 */
@RequestMapping("/admin/user")
public class UserAdminController {
    @Autowired
    UserService userService;

    @RequestMapping("/unregister")
    public Response unregister(@RequestBody UnregisterRequest request) {
        userService.unregisterUser(request.getUserId(), request.getOperatorId());
        return Response.EMPTY_SUCCESS;
    }
}
