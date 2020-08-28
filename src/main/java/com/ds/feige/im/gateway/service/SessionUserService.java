package com.ds.feige.im.gateway.service;

import com.ds.feige.im.account.dto.LoginRequest;
import com.ds.feige.im.gateway.domain.SessionUser;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;

public interface SessionUserService {
    /**
     * 登录
     *
     * @param request 登录请求参数
     * @param session WebSocketSession
     * @return 在线用户对象
     */
    SessionUser login(LoginRequest request, WebSocketSession session);

    /**
     * 登出
     *
     * @param userId
     */
    void logout(String userId, WebSocketSession session);

    /**
     * 网络连接断开时要主动调用此接口
     *
     * @param session
     */
    void disconnect(WebSocketSession session);

    /**
     * 服务器主动发消息给客户端
     * 如果客户端连接的是非本机服务器,则会通过其他服务器进行转发推送
     *
     * @param userId  用户ID
     * @param path
     * @param payload
     */
    void sendToUser(Long userId, String path, Object payload) throws IOException;

    /**
     * 服务器主动发消息给客户端
     * 如果客户端连接的是非本机服务器,则会通过其他服务器进行转发推送
     *
     * @param userIds  用户ID
     * @param path
     * @param payload
     */
    void sendToUsers(List<Long> userIds, String path, Object payload) throws IOException;

    void afterConnectionEstablished(WebSocketSession session) throws Exception;

    void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception;

    /**
     * 心跳
     *
     * @param session
     */
    void pingPong(WebSocketSession session);
}
