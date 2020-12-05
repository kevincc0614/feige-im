package com.ds.feige.im.chat.service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.redisson.api.RAtomicLong;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ds.base.nodepencies.exception.WarnMessageException;
import com.ds.base.nodepencies.strategy.id.IdKeyGenerator;
import com.ds.feige.im.account.dto.UserInfo;
import com.ds.feige.im.account.service.UserService;
import com.ds.feige.im.chat.dto.CreateGroupConversations;
import com.ds.feige.im.chat.dto.MessageOfUser;
import com.ds.feige.im.chat.dto.UserConversationInfo;
import com.ds.feige.im.chat.dto.event.ConversationMessageEvent;
import com.ds.feige.im.chat.entity.UserConversation;
import com.ds.feige.im.chat.mapper.ConversationMessageMapper;
import com.ds.feige.im.chat.mapper.UserConversationMapper;
import com.ds.feige.im.constants.CacheKeys;
import com.ds.feige.im.constants.ConversationType;
import com.ds.feige.im.constants.FeigeWarn;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

/**
 * @author DC
 */
@Service
@Slf4j
public class ConversationServiceImpl extends ServiceImpl<UserConversationMapper, UserConversation>
    implements ConversationService {
    @Autowired
    @Qualifier("longIdKeyGenerator")
    IdKeyGenerator<Long> longIdKeyGenerator;
    @Autowired
    UserService userService;
    @Autowired
    RedissonClient redissonClient;
    @Autowired
    ConversationMessageMapper conversationMessageMapper;

    @Autowired
    UserMessageService userMessageService;
    @Override
    public UserConversationInfo getUserConversation(long userId, long targetId, int conversationType) {
        log.info("Get conversation:userId={},targetId={},conversationType={}", userId, targetId, conversationType);
        UserConversation conversation = baseMapper.getByUserTargetAndTyppe(userId, targetId, conversationType);
        if (conversation == null) {
            return null;
        }
        UserConversationInfo result = new UserConversationInfo();
        BeanUtils.copyProperties(conversation, result);
        return result;
    }

    @Override
    public List<UserConversationInfo> getUserConversations(long userId, long lastEventTime) {
        // TODO 考虑直接数据库库读取
        // RScoredSortedSet<Long> scoredSortedSet =
        // redissonClient.getScoredSortedSet(CacheKeys.USER_CONVERSATION_ID_SET + userId);
        // Collection<ScoredEntry<Long>> entries =
        // scoredSortedSet.entryRange(lastEventTime, true, System.currentTimeMillis(), false);
        // Set<Long> conversationIds = Sets.newHashSet();
        // for (ScoredEntry<Long> entry : entries) {
        // conversationIds.add(entry.getValue());
        // }
        List<UserConversation> conversations = baseMapper.findRecentConversations(userId, new Date(lastEventTime));
        List<UserConversationInfo> result = conversations.stream().map(c -> {
            UserConversationInfo conversationInfo = convertToConversationInfo(c);
            return conversationInfo;
        }).collect(Collectors.toList());
        return result;
    }

    @Override
    public List<MessageOfUser> handleNewMessage(ConversationMessageEvent message) {
        long conversationId = message.getConversationId();
        long msgId = message.getMsgId();
        Set<Long> receivers = getUserIdsByConversation(conversationId);
        // 更新用户缓存
        Date messageTime = message.getCreateTime();
        // for (Long receiver : receivers) {
        // // 更新会话最后消息时间
        // RScoredSortedSet set = redissonClient.getScoredSortedSet(CacheKeys.USER_CONVERSATION_ID_SET + receiver);
        // set.addScore(conversationId, messageTime.getTime());
        // }
        boolean lastMsgIdUpdate = updateLastMsgId(conversationId, msgId);
        // 更新数据库最后事件时间,会话的用户对应未读+1,排除掉消息发送者
        if (lastMsgIdUpdate) {
            int count = baseMapper.updateLastEventTime(conversationId, messageTime);
        }
        // TODO 如果更新失败怎么处理
        List<MessageOfUser> list =
            receivers.stream().map(userId -> buildMessageOfUser(userId, message)).collect(Collectors.toList());
        return list;
    }

    public static MessageOfUser buildMessageOfUser(long userId, ConversationMessageEvent event) {
        MessageOfUser userMessage = new MessageOfUser();
        userMessage.setConversationId(event.getConversationId());
        userMessage.setMsgId(event.getMsgId());
        userMessage.setUserId(userId);
        userMessage.setSenderId(event.getSenderId());
        userMessage.setMsgType(event.getMsgType());
        userMessage.setMsgContent(event.getMsgContent());
        return userMessage;

    }

    private boolean updateLastMsgId(long conversationId, long msgId) {
        // 更新会话的最后一条消息
        RAtomicLong lastMsgId = redissonClient.getAtomicLong(CacheKeys.CONVERSATION_LAST_MSG_ID + conversationId);
        // 乐观锁更新,重试三次
        int retry = 0;
        do {
            Long oldLastMsgId = lastMsgId.get();
            // 老的消息ID比新的小,并且没有被并发修改过,才更新
            if (oldLastMsgId < msgId) {
                if (lastMsgId.compareAndSet(oldLastMsgId, msgId)) {
                    log.info("Update last msg id success:conversationId={},oldMsgId={},newMsgId={}", conversationId,
                        oldLastMsgId, msgId);
                    return true;
                }
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                    log.error("Update last msg id retrying error:conversationId={},newMsgId={}", conversationId, msgId,
                        e);
                }
                retry++;
            } else {
                log.info("Update last msg id fail,old >= new :conversationId={},oldMsgId={},newMsgId={}",
                    conversationId, oldLastMsgId, msgId);
                // 老的消息比新的ID大,则不用更新
                return false;
            }

        } while (retry <= 3);
        log.info("Update last msg id fail,retry more than 3 times :conversationId={},newMsgId={}", conversationId,
            msgId);
        return false;
    }

    private long getLastMsgId(long conversationId) {
        RAtomicLong lastMsgId = redissonClient.getAtomicLong(CacheKeys.CONVERSATION_LAST_MSG_ID + conversationId);
        return lastMsgId.get();
    }
    @Override
    public UserConversationInfo getUserConversation(long userId, long conversationId) {
        UserConversation conversation = baseMapper.getByUserAndConversationId(userId, conversationId);
        if (conversation == null) {
            return null;
        }

        return convertToConversationInfo(conversation);
    }

    UserConversationInfo convertToConversationInfo(UserConversation conversation) {
        long userId = conversation.getUserId();
        long conversationId = conversation.getConversationId();
        UserConversationInfo userConversationInfo = new UserConversationInfo();
        BeanUtils.copyProperties(conversation, userConversationInfo);
        // 获取未读数和最后消息ID
        int unreadCount = userMessageService.getUserConversationUnread(userId, conversationId);
        long lastMsgId = getLastMsgId(conversationId);
        userConversationInfo.setUnreadCount(unreadCount);
        userConversationInfo.setLastMsgId(lastMsgId);
        return userConversationInfo;
    }

    @Override
    public List<UserConversationInfo> getUserConversations(long userId, Collection<Long> conversationIds) {
        List<UserConversationInfo> result = Lists.newArrayList();
        if (conversationIds == null || conversationIds.isEmpty()) {
            return result;
        }
        List<UserConversation> conversations = baseMapper.findByUserAndConversationIds(userId, conversationIds);
        for (UserConversation conversation : conversations) {
            UserConversationInfo info = convertToConversationInfo(conversation);
            result.add(info);
        }
        return result;
    }

    @Override
    public Set<Long> getUserIdsByConversation(long conversationId) {
        Set<Long> users = baseMapper.findUsersByConversationId(conversationId);
        return users;
    }

    @Override
    public UserConversationInfo createSingleConversation(long userId, long targetId) {
        // 判断会话是否已经存在
        UserConversation conversation =
            baseMapper.getByUserTargetAndTyppe(userId, targetId, ConversationType.SINGLE_CONVERSATION_TYPE);
        if (conversation != null) {
            UserConversationInfo result = new UserConversationInfo();
            BeanUtils.copyProperties(conversation, result);
            log.warn("Conversation already exists:userId={},targetId={},conversationType={},conversationId={}", userId,
                targetId, conversation, conversation.getConversationId());
            return result;
        }
        // 判断userId是否存在
        UserInfo createUser = userService.getUserById(userId);
        UserInfo targetUser = null;
        targetUser = userService.getUserById(targetId);
        if (targetUser == null) {
            throw new WarnMessageException(FeigeWarn.USER_NOT_EXISTS);
        }
        String conversationLockId = userId >= targetId ? userId + "" + targetId : targetId + "" + userId;
        // 加分布式锁
        RLock lock = redissonClient.getLock(CacheKeys.CONVERSATION_LOCK_PREFIX + conversationLockId);
        try {
            if (lock.tryLock(3, 3, TimeUnit.SECONDS)) {
                conversation =
                    baseMapper.getByUserTargetAndTyppe(userId, targetId, ConversationType.SINGLE_CONVERSATION_TYPE);
                // 双重检查
                if (conversation == null) {
                    long conversationId = longIdKeyGenerator.generateId();
                    List<UserConversation> conversations = Lists.newArrayList();
                    // 如果是单聊,则需要为双方共同创建会话
                    conversation = buildConversation(userId, targetId, conversationId,
                        ConversationType.SINGLE_CONVERSATION_TYPE, targetUser.getNickName(), targetUser.getAvatar());
                    UserConversation targetConversation = buildConversation(targetId, userId, conversationId,
                        ConversationType.SINGLE_CONVERSATION_TYPE, createUser.getNickName(), createUser.getAvatar());
                    conversations.add(conversation);
                    conversations.add(targetConversation);
                    saveBatch(conversations);
                    log.info("Create single conversations  success:{}", conversations);
                } else {
                    log.warn("Conversation already exits after lock:userId={},targetId={}", userId, targetId);
                }
            } else {
                log.error("Lock conversation fail:userId={},targetId={}", userId, targetId);
                throw new WarnMessageException(FeigeWarn.SYSTEM_BUSY);
            }
        } catch (Exception e) {
            log.error("Lock conversation error:userId={},targetId={}", userId, targetId, e);
            throw new WarnMessageException(e, FeigeWarn.SYSTEM_ERROR);
        } finally {
            lock.unlock();
        }
        UserConversationInfo result = convertToConversationInfo(conversation);
        return result;
    }

    @Override
    public int deleteConversation(long userId, long targetId, int conversationType) {
        int i = baseMapper.deleteByUserAndTargetAndType(userId, targetId, conversationType);
        // TODO 删除缓存
        log.info("Delete conversation success:userId={},targetId={},count={}", userId, targetId, i);
        return i;
    }

    @Override
    public int deleteConversations(Collection<Long> userIds, long targetId, int conversationType) {
        int i = baseMapper.deleteByUsersAndTargetAndType(userIds, targetId, conversationType);
        log.info("Delete conversations success:userIds={},targetId={},count={}", userIds, targetId, i);
        return i;
    }

    @Override
    public long deleteConversations(long groupId) {
        List<UserConversation> conversations =
            baseMapper.findByTargetIdAndType(groupId, ConversationType.GROUP_CONVERSATION_TYPE);
        if (conversations.isEmpty()) {
            return 0;
        }
        List<Long> members = new ArrayList<>();
        conversations.forEach(c -> members.add(c.getUserId()));
        int i = baseMapper.deleteByTargetIdAndType(groupId, ConversationType.GROUP_CONVERSATION_TYPE);
        log.info("Delete group conversations success:groupId={}", groupId);
        return conversations.get(0).getConversationId();
    }

    @Override
    public long createGroupConversations(CreateGroupConversations request) {
        final long groupId = request.getGroupId();
        final String name = request.getName();
        final String avatar = request.getAvatar();
        Long conversationId = baseMapper.getConversationIdByTargetId(groupId, ConversationType.GROUP_CONVERSATION_TYPE);
        if (conversationId == null || conversationId == 0) {
            conversationId = longIdKeyGenerator.generateId();
        }
        Set<Long> members = request.getMembers();
        List<UserConversation> conversations = new ArrayList<>();
        Long finalConversationId = conversationId;
        members.forEach(uid -> {
            UserConversation uc = buildConversation(uid, groupId, finalConversationId,
                ConversationType.GROUP_CONVERSATION_TYPE, name, avatar);
            conversations.add(uc);
        });
        saveBatch(conversations);
        log.info("Create group conversations success:conversations={}", conversations);
        return conversationId;
    }

    public static UserConversation buildConversation(long userId, long targetId, long conversationId,
        int conversationType, String conversationName, String conversationAvatar) {
        UserConversation conversation = new UserConversation();
        conversation.setConversationId(conversationId);
        conversation.setUserId(userId);
        conversation.setTargetId(targetId);
        conversation.setConversationType(conversationType);
        conversation.setConversationName(conversationName);
        conversation.setConversationAvatar(conversationAvatar);
        return conversation;
    }

}
