package com.ds.feige.im.chat.service;

import com.ds.feige.im.chat.dto.CreateGroupConversations;
import com.ds.feige.im.chat.dto.UserConversationInfo;

import java.util.Set;

public interface ConversationService {
    /**
     * 获取聊天会话
     * 如果是单聊,第一次调用时,会为双方创建会话
     * 如果是群聊,在群创建时,已经为所有群成员创建聊天会话
     *
     * @param userId           会话所属人ID
     * @param targetId         目标ID
     * @param conversationType 会话类型 单聊或群聊
     * @return 会话信息
     */
    UserConversationInfo getUserConversation(long userId, long targetId, int conversationType);

    UserConversationInfo getUserConversation(long userId, long conversationId);

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
    long deleteGroupConversations(long groupId);

    void updateGroupConversationName(long conversationId, String name);

    void updateGroupConversationAvatar(long conversationId, String avatar);

    void updateConversationName(long userId, long conversationId, String name);

    void updateConversationAvatar(long userId, long conversationId, String avatar);

    void updateConversationOption(long userId, long conversationId, String option);
}
