package com.ds.feige.im.socket.dispatch;

import com.ds.feige.im.socket.protocol.SocketRequest;
import com.ds.feige.im.socket.protocol.SocketResponse;
import org.springframework.web.socket.WebSocketSession;

public interface SocketMethodHandlerInterceptor {

    boolean preHandle(WebSocketSession session, SocketRequest request, Object handler);

    boolean postHandle(WebSocketSession session, SocketRequest socketRequest, Object result, Object handler);

    void afterCompletion(WebSocketSession session, SocketRequest socketRequest,Object result, Object handler, Throwable throwable);
}
