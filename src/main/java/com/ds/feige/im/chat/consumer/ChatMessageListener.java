package com.ds.feige.im.chat.consumer;

import com.ds.feige.im.chat.dto.ChatMessage;
import com.ds.feige.im.chat.dto.MessageToUser;
import com.ds.feige.im.chat.dto.ReadReceiptNotice;
import com.ds.feige.im.chat.dto.event.ReadMessageEvent;
import com.ds.feige.im.chat.entity.ConversationMessage;
import com.ds.feige.im.chat.service.GroupUserService;
import com.ds.feige.im.chat.service.UserMessageService;
import com.ds.feige.im.common.util.BeansConverter;
import com.ds.feige.im.constants.ConversationType;
import com.ds.feige.im.constants.DynamicQueues;
import com.ds.feige.im.constants.SocketPaths;
import com.ds.feige.im.gateway.service.SessionUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 会话消息相关MQ处理
 *
 * @author caedmon
 */
@Component
public class ChatMessageListener {
    @Autowired
    UserMessageService userMessageService;
    @Autowired
    GroupUserService groupUserService;
    @Autowired
    SessionUserService sessionUserService;
    static final Logger LOGGER = LoggerFactory.getLogger(ChatMessageListener.class);

    @RabbitListener(queues = DynamicQueues.QueueNames.CONVERSATION_SEND_MESSAGE_SYNC)
    public void syncToUserMessages(@Payload ConversationMessage message) throws Exception {
        int conversationType = message.getConversationType();
        long conversationId = message.getConversationId();
        long msgId = message.getMsgId();
        long targetId = message.getTargetId();
        long senderId = message.getSenderId();
        int msgType = message.getMsgType();
        ChatMessage chatMessage = BeansConverter.conversationMsgToChatMsg(message);
        switch (conversationType) {
            //单聊
            case ConversationType.SINGLE_CONVERSATION_TYPE:
                //存入用户收件箱
                userMessageService.store(buildMessageOfUser(targetId, senderId, conversationId, msgId, msgType));
                sessionUserService.sendToUser(targetId, SocketPaths.SC_PUSH_CHAT_MESSAGE, chatMessage);
                break;
            case ConversationType.GROUP_CONVERSATION_TYPE:
                //给群用户收件箱写消息
                List<Long> userIds = groupUserService.getUserIds(targetId);
                List<MessageToUser> list = userIds.stream().map(userId -> buildMessageOfUser(userId, senderId, conversationId, msgId, msgType)).collect(Collectors.toList());
                userMessageService.store(list);
                sessionUserService.sendToUsers(userIds, SocketPaths.SC_PUSH_CHAT_MESSAGE, chatMessage);
                break;
            default:
                break;

        }

    }

    @RabbitListener(queues = DynamicQueues.QueueNames.CONVERSATION_READ_MESSAGE_RECEIPT)
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
                sessionUserService.sendToUser(senderId, SocketPaths.SC_CHAT_MESSAGE_READ_RECEIPT, notice);
            } catch (Exception e) {
                LOGGER.error("Send read receipt notice to user error:value={}", entry, e);
            }
        });
    }

    public static MessageToUser buildMessageOfUser(long userId, long senderId, long conversationId, long msgId, int msgType) {
        MessageToUser userMessage = new MessageToUser();
        userMessage.setConversationId(conversationId);
        userMessage.setMsgId(msgId);
        userMessage.setUserId(userId);
        userMessage.setSenderId(senderId);
        userMessage.setMsgType(msgType);
        return userMessage;

    }
}
