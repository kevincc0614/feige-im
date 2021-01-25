package com.ds.feige.im.chat.service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.redisson.api.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ds.base.nodepencies.exception.WarnMessageException;
import com.ds.base.nodepencies.strategy.id.IdKeyGenerator;
import com.ds.feige.im.account.service.UserService;
import com.ds.feige.im.chat.dto.*;
import com.ds.feige.im.chat.dto.event.ConversationMessageEvent;
import com.ds.feige.im.chat.dto.event.ReadMessageEvent;
import com.ds.feige.im.chat.entity.ConversationMessage;
import com.ds.feige.im.chat.mapper.ConversationMessageMapper;
import com.ds.feige.im.common.util.BeansConverter;
import com.ds.feige.im.constants.AMQPConstants;
import com.ds.feige.im.constants.CacheKeys;
import com.ds.feige.im.constants.ConversationType;
import com.ds.feige.im.constants.FeigeWarn;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ChatServiceImpl extends ServiceImpl<ConversationMessageMapper, ConversationMessage>
    implements ChatService {
    @Autowired
    RabbitTemplate template;
    @Autowired
    UserService userService;
    @Autowired
    @Qualifier("longIdKeyGenerator")
    IdKeyGenerator<Long> longIdKeyGenerator;
    @Autowired
    ConversationService conversationService;
    @Autowired
    UserMessageService userMessageService;
    @Autowired
    RedissonClient redissonClient;

    @Override
    @Transactional
    public MessageToUser sendToConversation(MessageToConversation request) {
        final int conversationType = request.getConversationType();
        final long senderId = request.getUserId();
        final long targetId = request.getTargetId();
        log.info("Send conversation message start:request={}", request);
        long start = System.currentTimeMillis();
        // 判断消息类型
        UserConversationInfo userConversation =
            conversationService.getUserConversation(senderId, targetId, conversationType);
        // 只有单聊才可以用户发消息时触发创建会话,群聊必须是系统自动创建会话
        if (userConversation == null) {
            if (conversationType == ConversationType.SINGLE_CONVERSATION_TYPE) {
                userConversation = conversationService.createSingleConversation(senderId, targetId);
            } else {
                throw new WarnMessageException(FeigeWarn.CONVERSATION_NOT_EXISTS);
            }
        }
        ConversationMessage conversationMessage = saveMessage(request, userConversation);
        ConversationMessageEvent event = buildEvent(conversationMessage);
        Set<String> connectionIds = Sets.newHashSet();
        connectionIds.add(request.getSenderConnectionId());
        event.setExcludeConnectionIds(connectionIds);
        template.convertAndSend(AMQPConstants.RoutingKeys.CONVERSATION_SEND_MESSAGE, event);
        return BeansConverter.conversationMsgToMessageToUser(event);

    }

    private ConversationMessage saveMessage(MessageToConversation request, UserConversationInfo userConversation) {
        final int conversationType = request.getConversationType();
        final long senderId = request.getUserId();
        final long targetId = request.getTargetId();
        // 入存储库,t_conversation_message
        ConversationMessage message = new ConversationMessage();
        long msgId = longIdKeyGenerator.generateId();
        message.setConversationId(userConversation.getConversationId());
        message.setConversationType(conversationType);
        message.setMsgContent(request.getMsgContent());
        message.setMsgId(msgId);
        message.setMsgType(request.getMsgType());
        message.setSenderId(senderId);
        message.setTargetId(targetId);
        message.setReadCount(0);
        Set<Long> receiverIds;
        // 消息接收人数位置为会话人数-1
        int receiverCount;
        switch (conversationType) {
            case ConversationType.SINGLE_CONVERSATION_TYPE:
                receiverCount = 1;
                receiverIds = Sets.newHashSet();
                receiverIds.add(message.getTargetId());
                break;
            case ConversationType.GROUP_CONVERSATION_TYPE:
                receiverIds = conversationService.getUserIdsByConversation(userConversation.getConversationId());
                receiverCount = receiverIds.size() - 1;
                break;
            default:
                throw new IllegalStateException("Conversation type invalid " + message.getConversationType());
        }
        message.setReceiverCount(receiverCount);
        save(message);
        return message;
    }

    public static ConversationMessageEvent buildEvent(ConversationMessage message) {
        // 消息推送
        ConversationMessageEvent event = new ConversationMessageEvent();
        // 会话信息
        event.setConversationId(message.getConversationId());
        // 消息内容
        event.setMsgContent(message.getMsgContent());
        event.setMsgId(message.getMsgId());
        event.setMsgType(message.getMsgType());
        event.setExtra(message.getExtra());
        event.setSenderId(message.getSenderId());
        // 已读未读
        event.setReceiverCount(message.getReceiverCount());
        event.setCreateTime(message.getCreateTime());
        message.setTargetId(message.getTargetId());
        return event;
    }

    @Override
    public List<MessageToUser> pullMessages(ConversationMessageQueryRequest request) {
        List<MessageToUser> messages = baseMapper.findMessages(request.getUserId(), request.getConversationId(),
            request.getMaxMsgId(), request.getPageSize());
        // 将已读回执赋值
        Set<Long> msgIds = messages.stream().map(m -> m.getMsgId()).collect(Collectors.toSet());
        Map<Long, Map<Long, Long>> readReceiptsMap = getMessageReadReceipts(msgIds);
        messages.forEach(messageToUser -> messageToUser.setReadReceipts(readReceiptsMap.get(messageToUser.getMsgId())));
        return messages;
    }

    @Override
    public MessageToUser getMessage(long msgId) {
        MessageToUser chatMessage = baseMapper.getMessageById(msgId);
        return chatMessage;
    }


    @Override
    public Collection<UserConversationDetails> getRecentConversations(long userId, long lastEventTime) {
        List<UserConversationInfo> infos = conversationService.getUserConversations(userId, lastEventTime);
        if (infos == null || infos.isEmpty()) {
            return new ArrayList<>();
        }
        Set<Long> lastMsgIds = Sets.newHashSet();
        Map<Long, UserConversationDetails> details = Maps.newHashMap();
        infos.forEach(info -> {
            lastMsgIds.add(info.getLastMsgId());
            details.put(info.getConversationId(), convertToDetails(info));
        });
        // 数据合并
        List<MessageToUser> messages = baseMapper.findMessagesByIds(lastMsgIds);
        Map<Long, MessageToUser> messageToUserMap =
            messages.stream().collect(Collectors.toMap(MessageToUser::getConversationId, Function.identity()));
        details.forEach((cid, c) -> {
            MessageToUser messageToUser = messageToUserMap.get(cid);
            c.setLastMsg(messageToUser);
        });
        return details.values();
    }

    public static UserConversationDetails convertToDetails(UserConversationInfo uc) {
        UserConversationDetails details = new UserConversationDetails();
        details.setConversationId(uc.getConversationId());
        details.setConversationName(uc.getConversationName());
        details.setConversationAvatar(uc.getConversationAvatar());
        details.setConversationType(uc.getConversationType());
        details.setPriority(uc.getPriority());
        details.setExtra(uc.getExtra());
        details.setTargetId(uc.getTargetId());
        details.setUnreadCount(uc.getUnreadCount());
        return details;
    }

    @Override
    public UserConversationDetails getConversationDetails(long userId, long conversationId) {
        UserConversationInfo userConversationInfo = conversationService.getUserConversation(userId, conversationId);
        if (userConversationInfo == null) {
            return null;
        }
        UserConversationDetails details = convertToDetails(userConversationInfo);
        MessageToUser lastMsg = baseMapper.getMessageById(userConversationInfo.getLastMsgId());
        details.setLastMsg(lastMsg);
        return details;
    }

    @Override
    public void userReadMessages(ReadMessageRequest request) {
        long conversationId = request.getConversationId();
        UserConversationInfo conversation =
            conversationService.getUserConversation(request.getUserId(), request.getConversationId());
        if (conversation == null) {
            throw new WarnMessageException(FeigeWarn.CONVERSATION_NOT_EXISTS);
        }
        Set<Long> successReadMsgSet = addMessageReadReceipts(request.getUserId(), request.getMsgIds());
        // 用户收件箱处理已读
        Map<Long, List<Long>> msgSenders =
            userMessageService.readMessages(request.getUserId(), conversationId, request.getMsgIds());
        // 存储库处理已读,将未读数-1
        if (successReadMsgSet != null && successReadMsgSet.size() > 0) {
            baseMapper.readMessages(successReadMsgSet);
            // TODO 所有人已读之后,考虑要清除数据
            ReadMessageEvent event = new ReadMessageEvent();
            event.setReaderId(request.getUserId());
            event.setSenderAndMsgIds(msgSenders);
            event.setReadTime(System.currentTimeMillis());
            event.setConversationId(request.getConversationId());
            Set<String> connectionIds = Sets.newHashSet();
            connectionIds.add(request.getReaderConnectionId());
            event.setExcludeConnectionIds(connectionIds);
            template.convertAndSend(AMQPConstants.RoutingKeys.CONVERSATION_READ_MESSAGE, event);
        }

    }

    /**
     * 给对应消息添加已读回执
     */
    private Set<Long> addMessageReadReceipts(long userId, Set<Long> msgIds) {
        log.info("Ready to add message read receipts:userId={},msgIds={}", userId, msgIds);
        // 更新已读回执缓存
        RBatch readMessageBatch = redissonClient.createBatch();
        Set<Long> result = Sets.newHashSet();
        Map<Long, RFuture<Boolean>> futureMap = Maps.newHashMap();
        for (Long msgId : msgIds) {
            RMapAsync<Long, Long> receipts = readMessageBatch.getMap(CacheKeys.CHAT_MESSAGE_READ_RECEIPTS + msgId);
            // <Key,Value> -> <用户ID,读取时间戳>
            RFuture<Boolean> future = receipts.fastPutIfAbsentAsync(userId, System.currentTimeMillis());
            futureMap.put(msgId, future);
        }
        BatchResult batchResult = readMessageBatch.execute();
        futureMap.forEach((k, v) -> {
            try {
                // 只有更新返回true的才是第一次读
                if (v.get()) {
                    result.add(k);
                }
            } catch (Exception e) {
                log.error("Get message read receipt update result error:msgId={}", k, e);
            }
        });
        log.info("Update read receipts cache success:{}", result);
        return result;
    }

    @Override
    public Map<Long, Long> getMessageReadReceipts(long msgId) {
        RMap<Long, Long> result = redissonClient.getMap(CacheKeys.CHAT_MESSAGE_READ_RECEIPTS + msgId);
        return result;
    }

    public Map<Long, Map<Long, Long>> getMessageReadReceipts(Set<Long> msgIds) {
        log.info("Ready to get message read read receipts:msgIds={}", msgIds);
        RBatch batch = redissonClient.createBatch(BatchOptions.defaults());
        Map<Long, Map<Long, Long>> result = Maps.newHashMap();
        Map<Long, RFuture<Map<Long, Long>>> futureMap = Maps.newHashMap();
        for (Long msgId : msgIds) {
            RMapAsync<Long, Long> readReceipts = batch.getMap(CacheKeys.CHAT_MESSAGE_READ_RECEIPTS + msgId);
            // 这里断点的话会导致线程阻塞
            RFuture<Map<Long, Long>> future = readReceipts.readAllMapAsync();
            futureMap.put(msgId, future);
        }
        batch.execute();
        futureMap.forEach((k, async) -> {
            try {
                Map<Long, Long> cacheValue = async.getNow();
                result.put(k, cacheValue);
            } catch (Exception e) {
                log.error("Get message read read receipts error:msgId={}", k);
            }
        });
        return result;
    }
    @Override
    public List<MessageToUser> getUserMessages(long userId, List<Long> msgIds) {
        if (msgIds.size() <= 0 || msgIds.size() > 500) {
            throw new IllegalArgumentException("MsgIds is over limit");
        }
        List<MessageToUser> messages = baseMapper.findUsersMessages(userId, msgIds);
        return messages;
    }
}
