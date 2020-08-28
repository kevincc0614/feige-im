package com.ds.feige.im.service.chat;

import com.ds.feige.im.pojo.dto.ChatMsgRequest;
import com.ds.feige.im.pojo.dto.chat.ChatMessage;
import com.ds.feige.im.pojo.dto.chat.ChatMsgAckResult;
import com.ds.feige.im.pojo.dto.chat.ConversationPreview;
import com.ds.feige.im.pojo.dto.chat.PullConversationMsgRequest;
import com.ds.feige.im.pojo.dto.message.SendMsgResult;

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
     */
    List<ConversationPreview> getConversationPreviews(long userId);
}
