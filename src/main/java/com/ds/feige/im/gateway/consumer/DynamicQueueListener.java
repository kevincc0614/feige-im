package com.ds.feige.im.gateway.consumer;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ds.feige.im.chat.dto.event.MessageForwardEvent;
import com.ds.feige.im.common.util.JsonUtils;
import com.ds.feige.im.constants.AMQPConstants;
import com.ds.feige.im.gateway.DiscoveryService;
import com.ds.feige.im.gateway.domain.SessionUserFactory;
import com.ds.feige.im.gateway.socket.connection.UserConnection;

/**
 * @author caedmon
 */
@Component
public class DynamicQueueListener implements MessageListener {
    private DiscoveryService discoveryService;
    private SessionUserFactory sessionUserFactory;
    static final Logger LOGGER= LoggerFactory.getLogger(DynamicQueueListener.class);
    private Set<String> dynamicQueueSet=new HashSet<>();
    @Autowired
    public DynamicQueueListener(DiscoveryService discoveryService, SessionUserFactory sessionUserFactory){
        this.discoveryService=discoveryService;
        this.sessionUserFactory=sessionUserFactory;
        final String instanceId=discoveryService.getInstanceId();
        dynamicQueueSet.add(AMQPConstants.SERVER_FORWARD_MESSAGE_QUEUE + instanceId);
        dynamicQueueSet.add(AMQPConstants.SERVER_FORWARD_DISCONNECT_MESSAGE_QUEUE + instanceId);
    }
    @Override
    public void onMessage(Message message) {
        String queueName=message.getMessageProperties().getConsumerQueue();
        if(dynamicQueueSet.contains(queueName)){
            String body=new String(message.getBody());
            try {
                if (queueName.equals(AMQPConstants.SERVER_FORWARD_MESSAGE_QUEUE + discoveryService.getInstanceId())) {
                    MessageForwardEvent forwardMessage = JsonUtils.jsonToBean(body, MessageForwardEvent.class);
                    UserConnection connection = this.sessionUserFactory.getConnection(forwardMessage.getMeta());
                    connection.send(forwardMessage.getRequest());
                } else if (queueName
                    .equals(AMQPConstants.SERVER_FORWARD_DISCONNECT_MESSAGE_QUEUE + discoveryService.getInstanceId())) {
                    MessageForwardEvent forwardMessage = JsonUtils.jsonToBean(body, MessageForwardEvent.class);
                    UserConnection connection = this.sessionUserFactory.getConnection(forwardMessage.getMeta());
                    connection.disconnect(forwardMessage.getRequest());
                }
            } catch (IOException e) {
                LOGGER.error("Dynamic queue consumer error:queue={},body={}",queueName,body,e);
            }
        }

    }

}
