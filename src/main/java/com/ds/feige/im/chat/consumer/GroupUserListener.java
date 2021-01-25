package com.ds.feige.im.chat.consumer;

import java.util.Set;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ds.base.nodepencies.strategy.id.IdKeyGenerator;
import com.ds.feige.im.account.dto.UserInfo;
import com.ds.feige.im.account.service.UserService;
import com.ds.feige.im.chat.dto.CreateGroupConversations;
import com.ds.feige.im.chat.dto.MessageContent;
import com.ds.feige.im.chat.dto.MessageToConversation;
import com.ds.feige.im.chat.dto.event.*;
import com.ds.feige.im.chat.dto.group.GroupInfo;
import com.ds.feige.im.chat.service.ChatService;
import com.ds.feige.im.chat.service.ConversationService;
import com.ds.feige.im.chat.service.GroupUserService;
import com.ds.feige.im.constants.AMQPConstants;
import com.ds.feige.im.constants.ConversationType;
import com.ds.feige.im.constants.MsgType;
import com.ds.feige.im.gateway.service.SessionUserService;

import lombok.extern.slf4j.Slf4j;

/**
 * 群聊MQ消费者
 *
 * @author DC
 */
@Component
@Slf4j
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
    @Autowired
    UserService userService;

    @RabbitListener(queues = AMQPConstants.QueueNames.GROUP_CREATED_BROADCAST)
    public void broadcastGroupCreated(GroupCreatedEvent event) throws Exception {
        log.info("Group created event broadcast start:{}", event);
        long groupId = event.getGroupId();
        GroupInfo groupInfo = groupUserService.getGroupInfo(groupId);
        // 判断会话是否已创建
        if (groupInfo.getConversationId() != null) {
            log.error("Group conversations has already created:groupId={}", groupId);
            return;
        }
        Set<Long> members = groupUserService.getUserIds(groupId);
        CreateGroupConversations createGroupConversations = new CreateGroupConversations();
        createGroupConversations.setGroupId(groupId);
        createGroupConversations.setName(groupInfo.getName());
        createGroupConversations.setAvatar(groupInfo.getAvatar());
        createGroupConversations.setMembers(members);
        long conversationId = conversationService.createGroupConversations(createGroupConversations);
        groupUserService.groupConversationsCreated(groupId, conversationId);
        UserInfo userInfo = userService.getUserById(event.getOperatorId());
        MessageContent.GroupCreatedMessage message =
            new MessageContent.GroupCreatedMessage(event.getGroupId(), event.getOperatorId(), userInfo.getNickName(),
                conversationId);
        sendGroupNotice(event.getOperatorId(), event.getGroupId(), message);
        log.info("Group created event broadcast success:{}", event);

    }

    @RabbitListener(queues = AMQPConstants.QueueNames.GROUP_DISBANDED_BROADCAST)
    public void broadcastGroupDisbanded(GroupDisbandEvent event) throws Exception {
        // 群关系已经不存在,但是会话关系还在,先发完消息,再删除会话关系
        MessageContent.GroupDisbandMessage message =
            new MessageContent.GroupDisbandMessage(event.getGroupId(), event.getOperatorId());
        sendGroupNotice(event.getOperatorId(), event.getGroupId(), message);
        long conversationId = conversationService.deleteConversations(event.getGroupId());
        log.info("Group disbanded event broadcast success:conversationId={}", conversationId);
    }

    @RabbitListener(queues = AMQPConstants.QueueNames.GROUP_USER_JOINED_BROADCAST)
    public void broadcastUserJoinGroup(GroupInviteUsersJoinEvent event) throws Exception {
        // 创建会话
        GroupInfo groupInfo = groupUserService.getGroupInfo(event.getGroupId());
        if (groupInfo == null) {
            log.error("Group not exists:groupId={}", event.getGroupId());
            return;
        }
        if (event.getInviteUsers() == null || event.getInviteUsers().isEmpty()) {
            log.error("InviteUsers is empty:groupId={}", event.getGroupId());
            return;
        }
        CreateGroupConversations createGroupConversations = new CreateGroupConversations();
        createGroupConversations.setMembers(event.getInviteUsers().keySet());
        createGroupConversations.setAvatar(groupInfo.getAvatar());
        createGroupConversations.setName(groupInfo.getName());
        createGroupConversations.setGroupId(event.getGroupId());
        conversationService.createGroupConversations(createGroupConversations);
        // String memberNames = StringUtils.collectionToDelimitedString(event.getInviteUsers().values(), ",");
        MessageContent.GroupUserJoinMessage joinMessage = new MessageContent.GroupUserJoinMessage(event.getGroupId(),
            event.getInviteUsers().keySet(), event.getOperatorId(), 0);
        sendGroupNotice(event.getOperatorId(), event.getGroupId(), joinMessage);
        log.info("Group user join event broadcast success:{}", event);
    }

    @RabbitListener(queues = AMQPConstants.QueueNames.GROUP_USER_EXITED_BROADCAST)
    public void broadcastUserExitGroup(GroupUserExitEvent event) throws Exception {
        // 删除会话
        conversationService.deleteConversation(event.getUserId(), event.getGroupId(),
            ConversationType.GROUP_CONVERSATION_TYPE);
        MessageContent.GroupUserExitedMessage message =
            new MessageContent.GroupUserExitedMessage(event.getGroupId(), event.getUserId());
        sendGroupNotice(event.getUserId(), event.getGroupId(), message);
        log.info("Group user exit event broadcast success:{}", event);
    }

    @RabbitListener(queues = AMQPConstants.QueueNames.GROUP_USER_KICKED_BROADCAST)
    public void broadcastUserKickedGroup(GroupUserKickEvent event) throws Exception {
        // 构建通知消息内容
        MessageContent.GroupUserKickedMessage kickedMessage = new MessageContent.GroupUserKickedMessage(
            event.getGroupId(), event.getKickedUsers(), event.getOperatorId());
        sendGroupNotice(event.getOperatorId(), event.getGroupId(), kickedMessage);
        // 删除会话
        conversationService.deleteConversations(event.getKickedUsers(), event.getGroupId(),
            ConversationType.GROUP_CONVERSATION_TYPE);
        log.info("Group user kicked event broadcast success:{}", event);
    }
    public void sendGroupNotice(long senderId, long groupId, MessageContent msgContent) throws Exception {
        MessageToConversation req = new MessageToConversation();
        req.setTargetId(groupId);
        req.setMsgContent(msgContent.toJson());
        req.setMsgType(MsgType.NOTICE);
        req.setConversationType(ConversationType.GROUP_CONVERSATION_TYPE);
        req.setUserId(senderId);
        chatService.sendToConversation(req);
    }
}
