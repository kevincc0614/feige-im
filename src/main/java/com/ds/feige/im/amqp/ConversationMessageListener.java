package com.ds.feige.im.amqp;

import com.ds.feige.im.constants.ConversationType;
import com.ds.feige.im.constants.SocketPaths;
import com.ds.feige.im.pojo.dto.chat.ChatMessage;
import com.ds.feige.im.pojo.dto.message.UserMsg;
import com.ds.feige.im.pojo.entity.ConversationMessage;
import com.ds.feige.im.service.chat.UserMessageService;
import com.ds.feige.im.service.group.GroupUserService;
import com.ds.feige.im.service.user.SessionUserService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


/**
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
    @RabbitHandler
    @RabbitListener(queues = "conversation.send.message.sync")
    public void syncToUserMessages(@Payload ConversationMessage message) throws Exception{
        int conversationType = message.getConversationType();
        long conversationId = message.getConversationId();
        long msgId = message.getMsgId();
        long targetId = message.getTargetId();
        ChatMessage chatMessage=buildChatMessage(message);
        //消息存入同步库t_user_message
        switch (conversationType) {
            //单聊
            case ConversationType.SINGLE_CONVERSATION_TYPE:
                userMessageService.store(buildUserMsg(targetId, conversationId, msgId));
                sessionUserService.sendToUser(targetId, SocketPaths.SC_PUSH_CHAT_MESSAGE,chatMessage);
                break;
            case ConversationType.GROUP_CONVERSATION_TYPE:
                //给群用户收件箱写消息
                List<Long> userIds = groupUserService.getUserIds(targetId);
                List<UserMsg> list = userIds.stream().map(userId -> buildUserMsg(userId, conversationId, msgId)).collect(Collectors.toList());
                userMessageService.store(list);
                sessionUserService.sendToUsers(userIds,SocketPaths.SC_PUSH_CHAT_MESSAGE,chatMessage);
                break;
            default:
                break;

        }

    }
    public static ChatMessage buildChatMessage(ConversationMessage message){
        ChatMessage chatMessage=new ChatMessage();
        BeanUtils.copyProperties(message,chatMessage);
        return chatMessage;
    }
    public static UserMsg buildUserMsg(long userId, long conversationId, long msgId) {
        UserMsg userMessage = new UserMsg();
        userMessage.setConversationId(conversationId);
        userMessage.setMsgId(msgId);
        userMessage.setUserId(userId);
        return userMessage;
    }
}
