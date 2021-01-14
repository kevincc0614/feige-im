package com.ds.feige.im.chat.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.redisson.api.RAtomicLong;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ds.feige.im.chat.dto.MessageOfUser;
import com.ds.feige.im.constants.CacheKeys;
import com.google.common.collect.Maps;

import lombok.extern.slf4j.Slf4j;

/**
 * @author DC
 */
@Service
@Slf4j
public class UserMessageServiceImpl implements UserMessageService {
    @Autowired
    RedissonClient redissonClient;

    @Override
    public void store(MessageOfUser message) {
        List<MessageOfUser> messageOfUsers = new ArrayList<>();
        messageOfUsers.add(message);
        store(messageOfUsers);
    }

    @Override
    public void store(List<MessageOfUser> messages) {
        messages.forEach(messageOfUser -> {
            long msgId = messageOfUser.getMsgId();
            long receiver = messageOfUser.getUserId();
            long conversationId = messageOfUser.getConversationId();
            long sender = messageOfUser.getSenderId();
            // 只有不是自己发送的消息才加未读
            if (receiver != sender) {
                // 用户会话未读消息Map
                RMap<Long, MessageOfUser> unReadMap = redissonClient
                    .getMap(CacheKeys.USER_CONVERSATION_UNREAD_MESSAGES + receiver + "." + conversationId);
                MessageOfUser putResult = unReadMap.put(msgId, messageOfUser);
                // 如果添加成功
                if (putResult == null) {
                    // 总未读+1
                    RAtomicLong totalUnread = redissonClient.getAtomicLong(CacheKeys.USER_UNREAD_TOTAL + receiver);
                    totalUnread.incrementAndGet();
                }
            }

        });
    }

    @Override
    public Map<Long, List<Long>> readMessages(long userId, long conversationId, Set<Long> msgIds) {
        // 用户会话未读消息的Map
        RMap<Long, MessageOfUser> unreadMap =
            redissonClient.getMap(CacheKeys.USER_CONVERSATION_UNREAD_MESSAGES + userId + "." + conversationId);
        log.info("Read msg:userId={},conversationId={},msgIds={}", userId, conversationId, msgIds);
        // TODO 是否要加分布式锁
        // 从未读Map中移除的消息
        Map<Long, MessageOfUser> readyRemoved = unreadMap.getAll(msgIds);
        Long[] msgIdArray = new Long[msgIds.size()];
        msgIds.toArray(msgIdArray);
        long readCount = unreadMap.fastRemove(msgIdArray);
        // 总未读减去读取的条数
        RAtomicLong totalUnread = redissonClient.getAtomicLong(CacheKeys.USER_UNREAD_TOTAL + userId);
        totalUnread.addAndGet(-readCount);
        Map<Long, List<Long>> result = Maps.newHashMap();
        // 构建用户ID和消息ID列表的Map,准备发送已读回执通知
        readyRemoved.values().forEach(m -> {
            List<Long> sendMsgIds = result.getOrDefault(m.getSenderId(), new ArrayList<>());
            sendMsgIds.add(m.getMsgId());
            result.put(m.getSenderId(), sendMsgIds);
        });
        return result;
    }

    @Override
    public Set<Long> readAndGetSenders(long userId, long conversationId, Set<Long> msgIds) {
        return readMessages(userId, conversationId, msgIds).keySet();
    }

    @Override
    public int getUserConversationUnread(long userId, long conversationId) {
        RMap<Long, MessageOfUser> unreadMap =
            redissonClient.getMap(CacheKeys.USER_CONVERSATION_UNREAD_MESSAGES + userId + "." + conversationId);
        return unreadMap.size();
    }

    @Override
    public Map<Long, Integer> getUserConversationsUnread(long userId, Set<Long> conversationIds) {
        Map<Long, Integer> result = Maps.newHashMap();
        for (Long conversationId : conversationIds) {
            RMap<Long, MessageOfUser> unreadMap =
                redissonClient.getMap(CacheKeys.USER_CONVERSATION_UNREAD_MESSAGES + userId + "." + conversationId);
            result.put(conversationId, unreadMap.size());
        }
        return result;
    }

    @Override
    public int getUserTotalUnread(long userId) {
        RAtomicLong totalUnread = redissonClient.getAtomicLong(CacheKeys.USER_UNREAD_TOTAL + userId);
        return Math.toIntExact(totalUnread.get());
    }
}
