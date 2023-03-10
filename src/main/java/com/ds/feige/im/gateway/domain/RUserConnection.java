package com.ds.feige.im.gateway.domain;

import java.io.IOException;

import com.ds.feige.im.gateway.socket.protocol.SocketPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.ds.feige.im.chat.dto.event.MessageForwardEvent;
import com.ds.feige.im.constants.AMQPConstants;
import com.ds.feige.im.gateway.socket.connection.ConnectionMeta;
import com.ds.feige.im.gateway.socket.connection.UserConnection;

/**
 * 远程用户链接对象
 * */
public class RUserConnection implements UserConnection {
    private ConnectionMeta meta;
    private RabbitTemplate rabbitTemplate;
    private static final Logger LOGGER= LoggerFactory.getLogger(RUserConnection.class);

    public RUserConnection(ConnectionMeta meta, RabbitTemplate rabbitTemplate){
        this.meta=meta;
        this.rabbitTemplate=rabbitTemplate;
    }
    @Override
    public boolean send(SocketPacket request) throws IOException {
        //非本机服务器通过MQ推送
        String routeKey = AMQPConstants.SERVER_FORWARD_MESSAGE_QUEUE + meta.getInstanceId();
        MessageForwardEvent message = new MessageForwardEvent();
        message.setMeta(this.meta);
//        message.setSourceInstanceId(discoveryMQService.getInstanceId());
        message.setRequest(request);
        this.rabbitTemplate.convertAndSend(routeKey,message);
        LOGGER.info("Forward send socket message:targetInstanceId={},message={}",meta.getInstanceId(),message);
        return true;
    }

    @Override
    public boolean disconnect(SocketPacket reason) throws IOException {
        String routeKey = AMQPConstants.SERVER_FORWARD_DISCONNECT_MESSAGE_QUEUE + meta.getInstanceId();
        MessageForwardEvent message = new MessageForwardEvent();
        message.setMeta(this.meta);
        message.setRequest(reason);
        this.rabbitTemplate.convertAndSend(routeKey,message);
        return true;
    }

    @Override
    public boolean disconnect() throws IOException {
       return disconnect(null);
    }

    @Override
    public ConnectionMeta getMeta() {
        return this.meta;
    }

    @Override
    public String getId() {
        return this.meta.getSessionId();
    }
}
