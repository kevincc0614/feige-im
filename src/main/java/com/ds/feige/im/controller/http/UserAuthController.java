package com.ds.feige.im.controller.http;

import com.ds.base.nodepencies.api.Response;
import com.ds.feige.im.pojo.dto.user.GetTokenRequest;
import com.ds.feige.im.pojo.dto.user.UserInfo;
import com.ds.feige.im.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author DX
 */
@RestController
@RequestMapping("/user/")
public class UserAuthController {
    @Autowired
    UserService userService;
    @PostMapping("/get-token")
    @ResponseBody
    public Response<String> getToken(@RequestBody @Valid GetTokenRequest request){
        String token=userService.getToken(request);
        return new Response<>(token);
    }
    @RequestMapping("/profile")
    public Response<UserInfo> getUserProfile(String token){
        return new Response<>(userService.verifyToken(token));
    }
}
