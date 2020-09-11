package com.ds.feige.im.chat.consumer;

import com.ds.base.nodepencies.strategy.id.IdKeyGenerator;
import com.ds.feige.im.chat.dto.ConversationMessageRequest;
import com.ds.feige.im.chat.dto.CreateGroupConversation;
import com.ds.feige.im.chat.dto.CreateGroupConversations;
import com.ds.feige.im.chat.dto.UserConversationInfo;
import com.ds.feige.im.chat.dto.event.*;
import com.ds.feige.im.chat.dto.group.GroupInfo;
import com.ds.feige.im.chat.service.ChatService;
import com.ds.feige.im.chat.service.ConversationService;
import com.ds.feige.im.chat.service.GroupUserService;
import com.ds.feige.im.constants.ConversationType;
import com.ds.feige.im.constants.DynamicQueues;
import com.ds.feige.im.constants.MsgType;
import com.ds.feige.im.constants.SocketPaths;
import com.ds.feige.im.gateway.service.SessionUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 群聊MQ消费者
 *
 * @author DC
 */
@Component
public class GroupUserListener {
    @Autowired
    GroupUserService groupUserService;
    @Autowired
    SessionUserService sessionUserService;
    @Autowired
    ConversationService conversationService;
    @Autowired
    ChatService chatService;
    @Autowired
    @Qualifier("longIdKeyGenerator")
    IdKeyGenerator<Long> longIdKeyGenerator;
    static final Logger LOGGER = LoggerFactory.getLogger(GroupUserListener.class);

    @RabbitListener(queues = DynamicQueues.QueueNames.GROUP_CREATED_BROADCAST)
    public void broadcastGroupCreated(GroupCreatedEvent event) throws Exception {
        LOGGER.info("Group created event broadcast start{}", event);
        long groupId = event.getGroupId();
        GroupInfo groupInfo = groupUserService.getGroupInfo(groupId);
        //判断会话是否已创建
        if (groupInfo.getConversationId() != null) {
            LOGGER.error("Group conversations has already created:groupId={}", groupId);
            return;
        }
        List<Long> userIds = groupUserService.getUserIds(groupId);
        CreateGroupConversations createGroupConversations = new CreateGroupConversations();
        createGroupConversations.setGroupId(groupId);
        createGroupConversations.setName(groupInfo.getName());
        createGroupConversations.setAvatar(groupInfo.getAvatar());
        createGroupConversations.setMembers(userIds);
        long conversationId = conversationService.createGroupConversations(createGroupConversations);
        groupUserService.groupConversationsCreated(groupId, conversationId);
        sendGroupNotice(event.getGroupId(), event.getOperatorName() + " 创建了群聊");
        LOGGER.info("Group created event broadcast success:{}", event);

    }

    @RabbitListener(queues = DynamicQueues.QueueNames.GROUP_DISBANDED_BROADCAST)
    public void broadcastGroupDisbanded(GroupDisbandEvent event) throws Exception {
        sendGroupNotice(event.getGroupId(), event.getOperatorName() + " 解散了群聊");
        //删除会话
        long conversationId = conversationService.deleteGroupConversations(event.getGroupId());
        LOGGER.info("Group disbanded event broadcast success:conversationId={}", conversationId);
    }

    @RabbitListener(queues = DynamicQueues.QueueNames.GROUP_USER_JOINED_BROADCAST)
    public void broadcastUserJoinGroup(GroupUserJoinEvent event) throws Exception {
        //创建会话
        GroupInfo groupInfo = groupUserService.getGroupInfo(event.getGroupId());
        if (groupInfo == null) {
            LOGGER.error("Group not exists:groupId={}", event.getGroupId());
            return;
        }
        CreateGroupConversation createGroupConversation = new CreateGroupConversation();
        createGroupConversation.setUserId(event.getUserId());
        createGroupConversation.setAvatar(groupInfo.getAvatar());
        createGroupConversation.setName(groupInfo.getName());
        createGroupConversation.setGroupId(event.getGroupId());
        UserConversationInfo conversationInfo = conversationService.createGroupConversation(createGroupConversation);
        sendGroupNotice(event.getGroupId(), event.getInviteUserName() + " 邀请 " + event.getUserName() + " 加入了群聊");
        LOGGER.info("Group disbanded event broadcast success:{}", event);
    }

    @RabbitListener(queues = DynamicQueues.QueueNames.GROUP_USER_EXITED_BROADCAST)
    public void broadcastUserExitGroup(GroupUserExitEvent event) {
        //删除会话
        conversationService.deleteConversation(event.getUserId(), event.getGroupId(), ConversationType.GROUP_CONVERSATION_TYPE);
        sendGroupNotice(event.getGroupId(), event.getUserName() + " 退出了群聊");
        LOGGER.info("Group user exit event broadcast success:{}", event);
    }

    @RabbitListener(queues = DynamicQueues.QueueNames.GROUP_USER_KICKED_BROADCAST)
    public void broadcastUserKickedGroup(GroupUserKickEvent event) throws Exception {
        //删除会话
        conversationService.deleteConversation(event.getKickUserId(), event.getGroupId(), ConversationType.GROUP_CONVERSATION_TYPE);
        sendGroupNotice(event.getGroupId(), event.getKickUserName() + " 已被 " + event.getOperatorName() + " 踢出群聊");
        //被踢用户需要单独通知,因为此时已经不在群内
        sessionUserService.sendToUser(event.getKickUserId(), SocketPaths.SC_GROUP_USER_KICKED, event);
        LOGGER.info("Group user kicked event broadcast success:{}", event);
    }

    public void sendGroupNotice(long groupId, String msgContent) {
        ConversationMessageRequest req = new ConversationMessageRequest();
        req.setTargetId(groupId);
        req.setMsgContent(msgContent);
        req.setMsgType(MsgType.NOTICE);
        req.setConversationType(ConversationType.GROUP_CONVERSATION_TYPE);
        chatService.sendMsg(req);
    }
}
