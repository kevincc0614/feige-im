package com.ds.feige.im.gateway.service;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import com.ds.feige.im.account.dto.LoginRequest;
import com.ds.feige.im.common.domain.UserIdHolder;
import com.ds.feige.im.gateway.domain.SessionUser;
import com.ds.feige.im.gateway.domain.UserState;

public interface SessionUserService {
    /**
     * 登录
     *
     * @param request
     *            登录请求参数
     * @param session
     *            WebSocketSession
     * @return 在线用户对象
     */
    SessionUser login(LoginRequest request, WebSocketSession session);

    SessionUser getSessionUser(long userId);

    Map<Long, UserState> getUserStates(Collection<? extends UserIdHolder> users);

    /**
     * 登出
     */
    void logout(WebSocketSession session);

    /**
     * 网络连接断开时要主动调用此接口
     *
     * @param session
     */
    void disconnect(WebSocketSession session);

    /**
     * 服务器主动发消息给客户端 如果客户端连接的是非本机服务器,则会通过其他服务器进行转发推送
     *
     * @param userId
     *            用户ID
     * @param path
     * @param payload
     */
    void sendToUser(Long userId, String path, Object payload, Set<String> excludeConnectionIds);

    /**
     * 服务器主动发消息给客户端 如果客户端连接的是非本机服务器,则会通过其他服务器进行转发推送
     *
     * @param userIds
     *            用户ID
     * @param path
     * @param payload
     */
    void sendToUsers(Collection<Long> userIds, String path, Object payload, Set<String> excludeConnectionIds);

    void afterConnectionEstablished(WebSocketSession session) throws Exception;

    void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception;

    /**
     * 心跳
     *
     * @param session
     */
    void pingPong(WebSocketSession session);
}
