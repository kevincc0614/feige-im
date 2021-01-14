package com.ds.feige.im.chat.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ds.feige.im.chat.dto.MessageOfUser;

public interface UserMessageService {
    /**
     * 存储消息到用户收件箱
     *
     * @param message
     *            会话消息
     * @return msgId
     */
    void store(MessageOfUser message);

    /**
     * 批量存储消息到用户收件箱s
     *
     * @param messages
     *            会话消息
     */
    void store(List<MessageOfUser> messages);

    /**
     * 用户读取消息
     *
     * @param userId
     *            用户ID
     * @param conversationId
     *            会话ID
     * @param msgIds
     *            消息ID集合
     * @return 返回的是已读消息的聚合结果, key是senderId, value是对应userId发送的消息ID集合
     */
    Map<Long, List<Long>> readMessages(long userId, long conversationId, Set<Long> msgIds);

    Set<Long> readAndGetSenders(long userId, long conversationId, Set<Long> msgIds);

    /**
     * 获取用户会话未读数
     * 
     * @param userId
     * @param conversationId
     */
    int getUserConversationUnread(long userId, long conversationId);

    Map<Long, Integer> getUserConversationsUnread(long userId, Set<Long> conversationIds);

    /**
     * 获取用户总未读数
     * 
     * @param userId
     */
    int getUserTotalUnread(long userId);
}
