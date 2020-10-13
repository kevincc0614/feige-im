package com.ds.feige.im.chat.consumer;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.ds.feige.im.chat.dto.MessageOfUser;
import com.ds.feige.im.chat.dto.MessageToUser;
import com.ds.feige.im.chat.dto.ReadReceiptNotice;
import com.ds.feige.im.chat.dto.event.ConversationMessageEvent;
import com.ds.feige.im.chat.dto.event.ReadMessageEvent;
import com.ds.feige.im.chat.service.GroupUserService;
import com.ds.feige.im.chat.service.UserMessageService;
import com.ds.feige.im.common.util.BeansConverter;
import com.ds.feige.im.constants.AMQPConstants;
import com.ds.feige.im.constants.SocketPaths;
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

    @RabbitListener(queues = AMQPConstants.QueueNames.CONVERSATION_SEND_MESSAGE_SYNC)
    public void syncToUserMessages(@Payload ConversationMessageEvent event) {
        log.info("Sync conversation message to users:event={}", event);
        long conversationId = event.getConversationId();
        long msgId = event.getMsgId();
        long senderId = event.getSenderId();
        int msgType = event.getMsgType();
        Set<String> excludeConnectionIds = event.getExcludeConnectionIds();
        MessageToUser chatMessage = BeansConverter.conversationMsgToMessageToUser(event);
        // 给群用户收件箱写消息
        Set<Long> receiverIds = event.getReceiverIds();
        List<MessageOfUser> list =
            receiverIds.stream().map(userId -> buildMessageOfUser(userId, senderId, conversationId, msgId, msgType))
                .collect(Collectors.toList());
        userMessageService.store(list);
        sessionUserService.sendToUsers(receiverIds, SocketPaths.SC_PUSH_CHAT_MESSAGE, chatMessage,
            excludeConnectionIds);
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
                sessionUserService.sendToUser(senderId, SocketPaths.SC_CHAT_MESSAGE_READ_RECEIPT, notice, null);
            } catch (Exception e) {
                log.error("Send read receipt notice to user error:value={}", entry, e);
            }
        });
    }

    public static MessageOfUser buildMessageOfUser(long userId, long senderId, long conversationId, long msgId,
        int msgType) {
        MessageOfUser userMessage = new MessageOfUser();
        userMessage.setConversationId(conversationId);
        userMessage.setMsgId(msgId);
        userMessage.setUserId(userId);
        userMessage.setSenderId(senderId);
        userMessage.setMsgType(msgType);
        return userMessage;

    }
}
