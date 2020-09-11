package com.ds.feige.im.chat.service;

import com.ds.feige.im.chat.dto.*;

import java.util.Collection;
import java.util.List;

public interface ChatService {

    /**
     * 发送聊天消息
     */
    SendMessageResult sendMsg(ConversationMessageRequest request);

    /**
     * 客户端收到消息存到本地后Ack
     * 但Ack不代表着已读
     */
    ChatMessageAckResult ackMsg(long userId, List<Long> msgIds);

    /**
     * 拉取指定会话的消息列表,筛选userId
     *
     * @param request
     */
    List<ChatMessage> pullMsg(ConversationMessageQueryRequest request);


    ChatMessage getMessage(long msgId);

    /**
     * 获取会话预览列表
     *
     * @param userId
     * @return 会话预览
     */
    Collection<ConversationPreview> getConversationPreviews(long userId);


    void readMessage(ReadMessageRequest request);
}
