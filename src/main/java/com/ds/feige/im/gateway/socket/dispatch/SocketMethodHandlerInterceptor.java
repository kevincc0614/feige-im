package com.ds.feige.im.gateway.socket.dispatch;

import org.springframework.web.socket.WebSocketSession;

import com.ds.feige.im.gateway.socket.protocol.SocketPacket;

public interface SocketMethodHandlerInterceptor {

    boolean preHandle(WebSocketSession session, SocketPacket request, Object handler);

    boolean postHandle(WebSocketSession session, SocketPacket socketPacket, Object result, Object handler);

    void afterCompletion(WebSocketSession session, SocketPacket socketPacket, Object result, Object handler,
        Throwable throwable);
}
