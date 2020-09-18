package com.ds.feige.im.chat.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ds.feige.im.chat.dto.ChatMessageAckResult;
import com.ds.feige.im.chat.dto.MessageOfUser;
import com.ds.feige.im.chat.entity.UserMessage;
import com.ds.feige.im.chat.mapper.UserMessageMapper;
import com.ds.feige.im.chat.po.SenderAndMsg;
import com.ds.feige.im.chat.po.UnreadMessagePreview;
import com.google.common.collect.Maps;

/**
 * @author DC
 */
@Service
public class UserMessageServiceImpl extends ServiceImpl<UserMessageMapper, UserMessage> implements UserMessageService {
    static final Logger LOGGER = LoggerFactory.getLogger(UserMessageServiceImpl.class);

    public UserMessageServiceImpl() {

    }

    @Override
    public long store(MessageOfUser message) {
        // TODO 缓存优化
        UserMessage entity = buildUserMessageEntity(message);
        save(entity);
        return entity.getMsgId();
    }

    @Override
    public void store(List<MessageOfUser> messages) {
        // TODO 缓存优化
        List<UserMessage> entities =
            messages.stream().map(msg -> buildUserMessageEntity(msg)).collect(Collectors.toList());
        saveBatch(entities);
    }

    UserMessage buildUserMessageEntity(MessageOfUser message) {
        UserMessage entity = new UserMessage();
        entity.setUserId(message.getUserId());
        entity.setMsgId(message.getMsgId());
        entity.setConversationId(message.getConversationId());
        entity.setSenderId(message.getSenderId());
        entity.setState(0);
        entity.setMsgType(message.getMsgType());
        return entity;
    }

    @Override
    public ChatMessageAckResult ackMsg(long userId, List<Long> msgIds) {
        // TODO msgIds不能过大,要设定上限
        int ackCount = baseMapper.batchUpdateState(userId, msgIds, 1);
        ChatMessageAckResult result = new ChatMessageAckResult();
        result.setAckCount(ackCount);
        return result;
    }

    @Override
    public Map<Long, List<Long>> readMsg(long userId, List<Long> msgIds) {
        int readCount = baseMapper.batchUpdateState(userId, msgIds, 2);
        LOGGER.info("Read msg:userId={},count={},msgIds={}", userId, readCount, msgIds);
        List<SenderAndMsg> senderAndMsgList = baseMapper.findSenderAndMsgList(userId, msgIds);
        Map<Long, List<Long>> senderAndMsgIdMap = Maps.newHashMap();
        senderAndMsgList.forEach(senderAndMsg -> {
            List<Long> value = senderAndMsgIdMap.getOrDefault(senderAndMsg.getSenderId(), new ArrayList<>());
            value.add(senderAndMsg.getMsgId());
            senderAndMsgIdMap.putIfAbsent(senderAndMsg.getSenderId(), value);
        });
        return senderAndMsgIdMap;
    }

    @Override
    public List<UnreadMessagePreview> getConversationUnreadPreview(long userId) {
        Long minUnAckMsgId = baseMapper.getMinUnAckMsgId(userId);
        if (minUnAckMsgId == null) {
            minUnAckMsgId = 0L;
        }
        List<UnreadMessagePreview> previews = baseMapper.getUnreadMessagePreview(userId, minUnAckMsgId);
        return previews;
    }
}
