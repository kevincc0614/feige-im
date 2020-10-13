package com.ds.feige.im.account.service;

import java.util.Collection;
import java.util.List;

import com.ds.feige.im.account.dto.GetTokenRequest;
import com.ds.feige.im.account.dto.UserInfo;
import com.ds.feige.im.account.dto.UserRegisterRequest;

public interface UserService {
    /**
     * @param mobile
     */
    UserInfo getUserByMobile(String mobile);

    /**
     * 用户名密码获取token
     *
     * @param request
     */
    String getToken(GetTokenRequest request);

    /**
     * 验证token合法性
     *
     * @param token
     * @return 用户基本信息
     */
    UserInfo verifyToken(String token);

    /**
     * 注册用户
     * 
     * @param registerRequest
     * @return 用户ID
     */
    long register(UserRegisterRequest registerRequest);

    /**
     * 删除用户
     * 
     * @param userId
     */
    boolean deleteUser(long userId);

    /**
     * 获取用户信息
     * 
     * @param userId
     * @return 用户信息
     */
    UserInfo getUserById(long userId);

    /**
     * 判断用户是否都存在
     *
     * @param userIdList
     * @return 返回的是存在的用户ID
     */
    List<UserInfo> getUserByIds(Collection<Long> userIdList);

    /**
     * 注销用户
     *
     * @param userId
     * @param operatorId
     */
    void unregisterUser(long userId, long operatorId);

}
