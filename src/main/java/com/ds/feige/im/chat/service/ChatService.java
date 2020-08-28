package com.ds.feige.im.chat.service;

import com.ds.feige.im.chat.dto.*;

import java.util.List;

public interface ChatService {
    /**
     * 发送聊天消息
     */
    SendMsgResult sendMsg(ChatMsgRequest request);

    /**
     * 客户端收到消息存到本地后Ack
     * 但Ack不代表着已读
     */
    ChatMsgAckResult ackMsg(long userId, List<Long> msgIds);

    /**
     * 拉取指定会话的消息列表,筛选userId
     *
     * @param request
     */
    List<ChatMessage> pullMsg(PullConversationMsgRequest request);

    /**
     * 获取会话预览列表
     *
     * @param userId
     * @return 会话预览
     */
    List<ConversationPreview> getConversationPreviews(long userId);
}
