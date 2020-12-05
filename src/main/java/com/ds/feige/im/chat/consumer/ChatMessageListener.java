package com.ds.feige.im.chat.consumer;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.ds.feige.im.chat.dto.MessageOfUser;
import com.ds.feige.im.chat.dto.ReadReceiptNotice;
import com.ds.feige.im.chat.dto.event.ConversationMessageEvent;
import com.ds.feige.im.chat.dto.event.ReadMessageEvent;
import com.ds.feige.im.chat.service.ConversationService;
import com.ds.feige.im.chat.service.GroupUserService;
import com.ds.feige.im.chat.service.UserMessageService;
import com.ds.feige.im.constants.AMQPConstants;
import com.ds.feige.im.constants.SocketPaths;
import com.ds.feige.im.event.dto.UserEventData;
import com.ds.feige.im.event.service.UserEventService;
import com.ds.feige.im.gateway.service.SessionUserService;

import lombok.extern.slf4j.Slf4j;

/**
 * 会话消息相关MQ处理
 *
 * @author caedmon
 */
@Component
@Slf4j
public class ChatMessageListener {
    @Autowired
    UserMessageService userMessageService;
    @Autowired
    GroupUserService groupUserService;
    @Autowired
    SessionUserService sessionUserService;
    @Autowired
    RedissonClient redissonClient;
    @Autowired
    ConversationService conversationService;
    @Autowired
    UserEventService userEventService;
    @RabbitListener(queues = AMQPConstants.QueueNames.CONVERSATION_SEND_MESSAGE_SYNC)
    public void syncToUserMessages(@Payload ConversationMessageEvent event) {
        log.info("Sync conversation message to users:event={}", event);
        Set<String> excludeConnectionIds = event.getExcludeConnectionIds();
        // 会话服务逻辑处理
        List<MessageOfUser> userMessages = conversationService.handleNewMessage(event);
        // 用户收件箱处理
        userMessageService.store(userMessages);
        // 事件处理
        List<UserEventData> dataList = userMessages.stream().map(m -> {
            UserEventData data = new UserEventData();
            data.setUserId(m.getUserId());
            data.setTopic("chat.conversation.message");
            data.setSourceId(String.valueOf(event.getMsgId()));
            data.setContent(m);
            return data;
        }).collect(Collectors.toList());
        userEventService.publishEvents(dataList, excludeConnectionIds);

    }

    @RabbitListener(queues = AMQPConstants.QueueNames.CONVERSATION_READ_MESSAGE_RECEIPT)
    public void readMessageFeedback(@Payload ReadMessageEvent event) {
        Map<Long, List<Long>> senderAndMsgIds = event.getSenderAndMsgIds();
        senderAndMsgIds.entrySet().forEach(entry -> {
            try {
                long senderId = entry.getKey();
                List<Long> msgIds = entry.getValue();
                long readerId = event.getReaderId();
                ReadReceiptNotice notice = new ReadReceiptNotice();
                notice.setMsgIds(msgIds);
                notice.setReaderId(readerId);
                notice.setReadTime(event.getReadTime());
                notice.setConversationId(event.getConversationId());
                // TODO 考虑做成event事件
                sessionUserService.sendToUser(senderId, SocketPaths.SC_CHAT_MESSAGE_READ_RECEIPT, notice,
                    event.getExcludeConnectionIds());
            } catch (Exception e) {
                log.error("Send read receipt notice to user error:value={}", entry, e);
            }
        });
    }

}
