package com.ds.feige.im.service.chat;

import com.ds.base.nodepencies.strategy.id.IdKeyGenerator;
import com.ds.feige.im.mapper.ConversationMsgMapper;
import com.ds.feige.im.mapper.UserConversationMapper;
import com.ds.feige.im.pojo.dto.ChatMsgRequest;
import com.ds.feige.im.pojo.dto.chat.*;
import com.ds.feige.im.pojo.dto.message.SendMsgResult;
import com.ds.feige.im.pojo.entity.ConversationMessage;
import com.ds.feige.im.service.group.GroupUserService;
import com.ds.feige.im.service.user.UserService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

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
    ConversationMsgMapper conversationMsgMapper;
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
    public SendMsgResult sendMsg(ChatMsgRequest request) {
        final int conversationType = request.getConversationType();
        final long senderId = request.getUserId();
        final long targetId = request.getTargetId();
        UserConversationInfo userConversation = conversationService.getOrCreateConversation(senderId, targetId, conversationType);
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
        int insertMsg = conversationMsgMapper.insert(conversationMsg);
        if (insertMsg != 1) {
            throw new IllegalStateException("Store message into t_conversation_message error :" + conversationMsg.toString());
        }
        template.convertAndSend("conversation.send.message",conversationMsg);
        //消息推送
        SendMsgResult result = new SendMsgResult();
        result.setConversationId(conversationMsg.getConversationId());
        result.setMsgId(conversationMsg.getMsgId());
        return result;
        //TODO 在线推送,如果在线,Redis查找所属服务器推送
        //TODO 不光要推送给消息接收方,也要推送给同一用户在线的不同设备
        //TODO 离线推送,如果不在线,走离线消息推送
    }


    @Override
    public List<ChatMessage> pullMsg(PullConversationMsgRequest request) {
        List<ChatMessage> messages = conversationMsgMapper.selectMessages(request.getUserId(),
                request.getConversationId(), request.getMaxMsgId(), request.getPageSize());
        return messages;
    }

    @Override
    public ChatMsgAckResult ackMsg(long userId, List<Long> msgIds) {
        return userMessageService.ackMsg(userId, msgIds);
    }

    @Override
    public List<ConversationPreview> getConversationPreviews(long userId) {
        List<ConversationPreview> previews = userMessageService.getConversationPreview(userId);
        Map<Long, ConversationPreview> previewMap = Maps.newHashMap();
        List<Long> msgIds = Lists.newArrayListWithCapacity(previews.size());
        previews.forEach(preview -> {
            previewMap.put(preview.getConversationId(), preview);
            msgIds.add(preview.getLastMsgId());

        });
        List<ChatMessage> conversationLastMessages = conversationMsgMapper.findByMsgIds(msgIds);
        conversationLastMessages.forEach(lastMsg -> {
            ConversationPreview preview = previewMap.get(lastMsg.getConversationId());
            preview.setLastMsg(lastMsg);
        });
        return previews;
    }
}
