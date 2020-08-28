package com.ds.feige.im.chat.service;

import com.ds.feige.im.chat.dto.ChatMsgAckResult;
import com.ds.feige.im.chat.dto.ConversationPreview;
import com.ds.feige.im.chat.dto.UserMsg;

import java.util.List;

public interface UserMessageService {
    /**
     * 存储消息到用户收件箱
     *
     * @param message 会话消息
     * @return msgId
     */
    long store(UserMsg message);

    /**
     * 批量存储消息到用户收件箱s
     *
     * @param messages 会话消息
     */
    void store(List<UserMsg> messages);

    /**
     * 客户端确认消息到达
     *
     * @param userId
     * @param msgIds
     * @return ack 结果
     */
    ChatMsgAckResult ackMsg(long userId, List<Long> msgIds);

    /**
     * 用户聊天概览,
     *
     * @param userId
     * @return 主要包括会话未读消息数, 最新一条消息msgId
     */
    List<ConversationPreview> getConversationPreview(long userId);


}
