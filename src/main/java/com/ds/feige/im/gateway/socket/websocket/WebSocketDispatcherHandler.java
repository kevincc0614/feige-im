package com.ds.feige.im.gateway.socket.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.ds.feige.im.common.util.JsonUtils;
import com.ds.feige.im.common.util.Tracer;
import com.ds.feige.im.constants.SessionAttributeKeys;
import com.ds.feige.im.gateway.service.SessionUserService;
import com.ds.feige.im.gateway.socket.dispatch.SocketControllerDispatcher;
import com.ds.feige.im.gateway.socket.protocol.SocketPacket;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class WebSocketDispatcherHandler extends TextWebSocketHandler {
    private final SocketControllerDispatcher controllerDispatcher;
    private final SessionUserService sessionUserService;
    @Autowired
    public WebSocketDispatcherHandler(SocketControllerDispatcher controllerDispatcher, SessionUserService sessionUserService){
        this.controllerDispatcher=controllerDispatcher;
        this.sessionUserService=sessionUserService;
    }
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        Long userId = (Long) session.getAttributes().get(SessionAttributeKeys.USER_ID);
        SocketPacket socketPacket = JsonUtils.jsonToBean(payload, SocketPacket.class);
        Tracer.getTraceId(socketPacket);
        log.info("Received client text message:sessionId={},userId={},payload={}", session.getId(), userId, payload);
        try {
            controllerDispatcher.doService(session, socketPacket);
        } finally {
            Tracer.removeTraceId();
        }
    }
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Tracer.getOrGenerateTraceId();
        log.info("Websocket connection closed:sessionId={},status={}", session.getId(), status);
        super.afterConnectionClosed(session,status);
        this.sessionUserService.afterConnectionClosed(session,status);
        Tracer.removeTraceId();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Tracer.getOrGenerateTraceId();
        super.afterConnectionEstablished(session);
        this.sessionUserService.afterConnectionEstablished(session);
        Tracer.removeTraceId();
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("Websocket transport error:sessionId={}", session.getId(), exception);
    }
}
