package com.ds.feige.im.service.user;

import com.ds.feige.im.pojo.dto.user.GetTokenRequest;
import com.ds.feige.im.pojo.dto.user.UserInfo;
import com.ds.feige.im.pojo.dto.user.UserRegisterRequest;

import java.util.List;

public interface UserService {



    /**
     * 用户名密码获取token
     * @param request
     * */
    String getToken(GetTokenRequest request);

    /**
     * 验证token合法性
     * @param token
     * @return 用户基本信息
     * */
    UserInfo verifyToken(String token);

    /**
     * 判断用户是否存在
     * */
    boolean userExists(long userId);

    long register(UserRegisterRequest registerRequest);

    boolean deleteUser(long userId);
    /**
     * 获取用户信息
     * @param userId
     * @return 用户信息
     * */
    UserInfo getUserById(long userId);

    /**
     * 判断用户是否都存在
     * @param userIdList
     * @return 返回的是存在的用户ID
     * */
    List<UserInfo> getUserByIds(List<Long> userIdList);

}