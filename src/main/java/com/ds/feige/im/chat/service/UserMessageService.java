package com.ds.feige.im.chat.service;

import com.ds.feige.im.chat.dto.ChatMessageAckResult;
import com.ds.feige.im.chat.dto.MessageOfUser;
import com.ds.feige.im.chat.po.UnreadMessagePreview;

import java.util.List;
import java.util.Map;

public interface UserMessageService {
    /**
     * 存储消息到用户收件箱
     *
     * @param message 会话消息
     * @return msgId
     */
    long store(MessageOfUser message);

    /**
     * 批量存储消息到用户收件箱s
     *
     * @param messages 会话消息
     */
    void store(List<MessageOfUser> messages);

    /**
     * 客户端确认消息到达
     *
     * @param userId
     * @param msgIds
     * @return ack 结果
     */
    ChatMessageAckResult ackMsg(long userId, List<Long> msgIds);

    /**
     * 用户读取消息
     *
     * @param userId
     * @param msgIds
     * @return 返回的是已读消息的聚合结果, key是userId, value是对应userId发送的消息ID集合
     */
    Map<Long, List<Long>> readMsg(long userId, List<Long> msgIds);

    /**
     * 用户聊天概览,
     *
     * @param userId
     * @return 主要包括会话未读消息数, 最新一条消息msgId
     */
    List<UnreadMessagePreview> getConversationUnreadPreview(long userId);
}
