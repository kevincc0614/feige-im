package com.ds.feige.im.chat.service;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.ds.feige.im.chat.dto.CreateGroupConversations;
import com.ds.feige.im.chat.dto.MessageOfUser;
import com.ds.feige.im.chat.dto.UserConversationInfo;
import com.ds.feige.im.chat.dto.event.ConversationMessageEvent;

public interface ConversationService {
    /**
     * 获取聊天会话 如果是单聊,第一次调用时,会为双方创建会话 如果是群聊,在群创建时,已经为所有群成员创建聊天会话
     *
     * @param userId
     *            会话所属人ID
     * @param targetId
     *            目标ID
     * @param conversationType
     *            会话类型 单聊或群聊
     * @return 会话信息
     */
    UserConversationInfo getUserConversation(long userId, long targetId, int conversationType);

    /**
     * 获取用户会话列表
     * 
     */
    List<UserConversationInfo> getUserConversations(long userId, long lastUpdateTime);

    List<MessageOfUser> handleNewMessage(ConversationMessageEvent message);
    /**
     * @param userId
     * @param conversationId
     */
    UserConversationInfo getUserConversation(long userId, long conversationId);

    List<UserConversationInfo> getUserConversations(long userId, Collection<Long> conversationIds);

    /**
     * @param conversationId
     */
    Set<Long> getUserIdsByConversation(long conversationId);

    /**
     * 创建单聊会话
     *
     * @param userId
     * @param targetId
     * @return 会话信息
     */
    UserConversationInfo createSingleConversation(long userId, long targetId);

    /**
     * 删除指定会话
     *
     * @param userId
     * @param targetId
     * @param conversationType
     * @return 删除条数
     */
    int deleteConversation(long userId, long targetId, int conversationType);

    /**
     * 删除指定会话
     *
     * @param userIds
     * @param targetId
     * @param conversationType
     * @return 删除条数
     */
    int deleteConversations(Collection<Long> userIds, long targetId, int conversationType);

    /**
     * 创建群聊天会话
     *
     * @param groupConversations
     * @return conversationId
     */
    long createGroupConversations(CreateGroupConversations groupConversations);

    /**
     * 删除群会话
     *
     * @param groupId
     * @return 用户ID列表
     */
    long deleteConversations(long groupId);
}
