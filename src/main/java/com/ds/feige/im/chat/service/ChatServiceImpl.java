package com.ds.feige.im.chat.service;

import com.ds.base.nodepencies.exception.WarnMessageException;
import com.ds.base.nodepencies.strategy.id.IdKeyGenerator;
import com.ds.feige.im.account.service.UserService;
import com.ds.feige.im.chat.dto.*;
import com.ds.feige.im.chat.dto.event.ReadMessageEvent;
import com.ds.feige.im.chat.entity.ConversationMessage;
import com.ds.feige.im.chat.entity.UserConversation;
import com.ds.feige.im.chat.mapper.ConversationMessageMapper;
import com.ds.feige.im.chat.mapper.UserConversationMapper;
import com.ds.feige.im.chat.po.UnreadMessagePreview;
import com.ds.feige.im.constants.ConversationType;
import com.ds.feige.im.constants.DynamicQueues;
import com.ds.feige.im.constants.FeigeWarn;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class ChatServiceImpl implements ChatService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatServiceImpl.class);
    @Autowired
    RabbitTemplate template;
    @Autowired
    UserConversationMapper userConversationMapper;
    @Autowired
    ConversationMessageMapper conversationMessageMapper;
    @Autowired
    UserService userService;
    @Autowired
    GroupUserService groupUserService;
    @Autowired
    @Qualifier("longIdKeyGenerator")
    IdKeyGenerator<Long> longIdKeyGenerator;
    @Autowired
    ConversationService conversationService;
    @Autowired
    UserMessageService userMessageService;

    @Override
    public SendMessageResult sendMsg(ConversationMessageRequest request) {
        final int conversationType = request.getConversationType();
        final long senderId = request.getUserId();
        final long targetId = request.getTargetId();
        //判断消息类型
        UserConversationInfo userConversation = conversationService.getUserConversation(senderId, targetId, conversationType);
        //只有单聊才可以用户发消息时触发创建会话,群聊必须是系统自动创建会话
        if (userConversation == null && conversationType == ConversationType.SINGLE_CONVERSATION_TYPE) {
            userConversation = conversationService.createSingleConversation(senderId, targetId);
        } else {
            throw new WarnMessageException(FeigeWarn.CONVERSATION_NOT_EXISTS);
        }
        //入存储库,t_conversation_message
        ConversationMessage conversationMsg = new ConversationMessage();
        long msgId = longIdKeyGenerator.generateId();
        conversationMsg.setSendSeqId(request.getSendSeqId());
        conversationMsg.setConversationId(userConversation.getConversationId());
        conversationMsg.setConversationType(conversationType);
        conversationMsg.setMsgContent(request.getMsgContent());
        conversationMsg.setMsgId(msgId);
        conversationMsg.setMsgType(request.getMsgType());
        conversationMsg.setSenderId(senderId);
        conversationMsg.setTargetId(targetId);
        int insertMsg = conversationMessageMapper.insert(conversationMsg);
        if (insertMsg != 1) {
            throw new IllegalStateException("Store message into t_conversation_message error :" + conversationMsg.toString());
        }
        //消息推送
        template.convertAndSend(DynamicQueues.RoutingKeys.CONVERSATION_SEND_MESSAGE, conversationMsg);
        SendMessageResult result = new SendMessageResult();
        result.setConversationId(conversationMsg.getConversationId());
        result.setMsgId(conversationMsg.getMsgId());
        return result;

    }


    @Override
    public List<ChatMessage> pullMsg(ConversationMessageQueryRequest request) {
        List<ChatMessage> messages = conversationMessageMapper.selectMessages(request.getUserId(),
                request.getConversationId(), request.getMaxMsgId(), request.getPageSize());
        return messages;
    }

    @Override
    public ChatMessage getMessage(long msgId) {
        ChatMessage chatMessage = conversationMessageMapper.getMsgById(msgId);
        return chatMessage;
    }

    @Override
    public ChatMessageAckResult ackMsg(long userId, List<Long> msgIds) {
        return userMessageService.ackMsg(userId, msgIds);
    }

    @Override
    public Collection<ConversationPreview> getConversationPreviews(long userId) {
        //获取未读消息数据
        List<UnreadMessagePreview> previews = userMessageService.getConversationUnreadPreview(userId);
        if (previews.isEmpty()) {
            return null;
        }
        Map<Long, ConversationPreview> previewMap = Maps.newHashMap();
        List<Long> msgIds = Lists.newArrayListWithCapacity(previews.size());
        previews.forEach(unreadMessagePreview -> {
            ConversationPreview conversationPreview = new ConversationPreview();
            BeanUtils.copyProperties(unreadMessagePreview, conversationPreview);
            previewMap.put(unreadMessagePreview.getConversationId(), conversationPreview);
            msgIds.add(unreadMessagePreview.getLastMsgId());

        });
        //获取会话里的最后一条消息内容
        List<ChatMessage> conversationLastMessages = conversationMessageMapper.findByMsgIds(msgIds);
        conversationLastMessages.forEach(lastMsg -> {
            ConversationPreview preview = previewMap.get(lastMsg.getConversationId());
            preview.setLastMsg(lastMsg);
        });
        //数据合并
        return previewMap.values();
    }

    @Override
    public void readMessage(ReadMessageRequest request) {
        //TODO 更新最大已读消息ID
        UserConversation conversation = userConversationMapper.getByUserAndConversationId(request.getUserId(), request.getConversationId());
        if (conversation == null) {
            throw new WarnMessageException(FeigeWarn.CONVERSATION_NOT_EXISTS);
        }
        Map<Long, List<Long>> readMsgResult = userMessageService.readMsg(request.getUserId(), request.getMsgIds());
        ReadMessageEvent event = new ReadMessageEvent();
        event.setReaderId(request.getUserId());
        event.setSenderAndMsgIds(readMsgResult);
        //TODO 推送已读回执,MQ异步处理
        template.convertAndSend(DynamicQueues.RoutingKeys.CONVERSATION_READ_MESSAGE, event);
    }
}
