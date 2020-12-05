package com.ds.feige.im.common.rabbitmq;

import java.util.List;
import java.util.Map;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ds.feige.im.common.entity.MQFailMessage;
import com.ds.feige.im.common.mapper.MQFailMessageMapper;
import com.ds.feige.im.constants.AMQPConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * @author DC
 */
@Component
@Slf4j
public class RabbitDLXHandler {
    @Autowired
    private MQFailMessageMapper mapper;

    @RabbitListener(queues = AMQPConstants.QueueNames.PUBLIC_DLX_DEFAULT)
    public void handleFailMessage(Message message) {
        try {
            MessageProperties properties = message.getMessageProperties();
            Map<String, Object> headers = properties.getHeaders();
            MQFailMessage failMessage = new MQFailMessage();
            List list = (List)headers.get("x-death");
            if (list != null && !list.isEmpty()) {
                Map<String, Object> xDeath = (Map<String, Object>)list.get(0);
                failMessage.setRoutingKeys(String.valueOf(xDeath.get("routing-keys")));
            }
            failMessage.setMessageId(properties.getMessageId());
            failMessage.setProduceTime(properties.getTimestamp());
            failMessage.setExchange((String)headers.get(" x-first-death-exchange"));
            failMessage.setQueue((String)headers.get(" x-first-death-queue"));
            failMessage.setReason((String)headers.get(" x-first-death-reason"));
            failMessage.setBody(new String(message.getBody()));
            failMessage.setStatus(0);
            mapper.insert(failMessage);
        } catch (Throwable e) {
            log.error("Handle fail message error:message={}", message, e);
        }

    }
}
