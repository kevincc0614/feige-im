package com.ds.feige.im.gateway.socket.dispatch;

import com.ds.feige.im.gateway.socket.protocol.SocketRequest;
import org.springframework.web.socket.WebSocketSession;

public interface SocketMethodHandlerInterceptor {

    boolean preHandle(WebSocketSession session, SocketRequest request, Object handler);

    boolean postHandle(WebSocketSession session, SocketRequest socketRequest, Object result, Object handler);

    void afterCompletion(WebSocketSession session, SocketRequest socketRequest,Object result, Object handler, Throwable throwable);
}
