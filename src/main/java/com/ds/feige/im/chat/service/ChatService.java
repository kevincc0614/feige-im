package com.ds.feige.im.chat.service;

import com.ds.feige.im.chat.dto.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ChatService {

    /**
     * 发送消息
     * ①同步操作存入存储库t_conversation_message
     * ②MQ异步操作存入同步库t_user_message
     * ③实时推送消息到客户端
     * 方法包含会话逻辑,既消息接收对象通过内部逻辑查询得来,而非直接定向推送。所以调用此方法时需要考虑会话关系状态是否正确
     */
    SendMessageResult sendToConversation(MessageToConversation request);

    /**
     * 客户端收到消息存到本地后Ack,但Ack不代表着已读
     *
     * @param msgIds 列表不能太大,否则影响性能
     * @param userId
     */
    ChatMessageAckResult ackMessages(long userId, List<Long> msgIds);

    /**
     * 拉取指定会话的消息列表,筛选userId
     *
     * @param request
     */
    List<MessageToUser> pullMessages(ConversationMessageQueryRequest request);


    MessageToUser getMessage(long msgId);

    /**
     * 获取会话预览列表
     *
     * @param userId
     * @return 会话预览
     */
    Collection<ConversationPreview> getConversationPreviews(long userId);


    void readMessage(ReadMessageRequest request);

    /**
     * 消息已读人列表
     *
     * @param msgId
     * @return <userId,readTime>
     */
    Map<Long, Long> getMessageReadReceipts(long msgId);

    List<MessageToUser> getUserMessages(long userId, List<Long> msgIds);
}
