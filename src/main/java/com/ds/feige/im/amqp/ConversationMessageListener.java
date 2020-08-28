package com.ds.feige.im.amqp;

import com.ds.feige.im.chat.dto.ChatMessage;
import com.ds.feige.im.chat.dto.UserMsg;
import com.ds.feige.im.chat.entity.ConversationMessage;
import com.ds.feige.im.chat.service.GroupUserService;
import com.ds.feige.im.chat.service.UserMessageService;
import com.ds.feige.im.common.util.BeansConverter;
import com.ds.feige.im.constants.ConversationType;
import com.ds.feige.im.constants.DynamicQueues;
import com.ds.feige.im.constants.SocketPaths;
import com.ds.feige.im.gateway.service.SessionUserService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


/**
 * 会话消息相关MQ处理
 *
 * @author caedmon
 */
@Component
public class ConversationMessageListener {
    @Autowired
    UserMessageService userMessageService;
    @Autowired
    GroupUserService groupUserService;
    @Autowired
    SessionUserService sessionUserService;

    @RabbitListener(queues = DynamicQueues.QueueNames.CONVERSATION_SEND_MESSAGE_SYNC)
    public void syncToUserMessages(@Payload ConversationMessage message) throws Exception {
        int conversationType = message.getConversationType();
        long conversationId = message.getConversationId();
        long msgId = message.getMsgId();
        long targetId = message.getTargetId();
        ChatMessage chatMessage = BeansConverter.conversationMsgToChatMsg(message);
        //消息存入同步库t_user_message
        //TODO 离线推送,如果不在线,走离线消息推送
        switch (conversationType) {
            //单聊
            case ConversationType.SINGLE_CONVERSATION_TYPE:
                userMessageService.store(buildUserMsg(targetId, conversationId, msgId));
                sessionUserService.sendToUser(targetId, SocketPaths.SC_PUSH_CHAT_MESSAGE, chatMessage);
                break;
            case ConversationType.GROUP_CONVERSATION_TYPE:
                //给群用户收件箱写消息
                List<Long> userIds = groupUserService.getUserIds(targetId);
                List<UserMsg> list = userIds.stream().map(userId -> buildUserMsg(userId, conversationId, msgId)).collect(Collectors.toList());
                userMessageService.store(list);
                sessionUserService.sendToUsers(userIds, SocketPaths.SC_PUSH_CHAT_MESSAGE, chatMessage);
                break;
            default:
                break;

        }

    }

    public static UserMsg buildUserMsg(long userId, long conversationId, long msgId) {
        UserMsg userMessage = new UserMsg();
        userMessage.setConversationId(conversationId);
        userMessage.setMsgId(msgId);
        userMessage.setUserId(userId);
        return userMessage;
    }
}
