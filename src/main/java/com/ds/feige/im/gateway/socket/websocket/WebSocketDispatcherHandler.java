package com.ds.feige.im.gateway.socket.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.ds.feige.im.common.util.JsonUtils;
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
        log.info("Received client text message:userId={},payload={}", userId, payload);
        SocketPacket socketPacket = JsonUtils.jsonToBean(payload, SocketPacket.class);
        controllerDispatcher.doService(session, socketPacket);
    }
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session,status);
        this.sessionUserService.afterConnectionClosed(session,status);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        this.sessionUserService.afterConnectionEstablished(session);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        super.handleTransportError(session, exception);
    }
}
