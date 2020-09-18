package com.ds.feige.im.chat.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ds.base.nodepencies.exception.WarnMessageException;
import com.ds.base.nodepencies.strategy.id.IdKeyGenerator;
import com.ds.feige.im.account.dto.UserInfo;
import com.ds.feige.im.account.service.UserService;
import com.ds.feige.im.chat.dto.CreateGroupConversations;
import com.ds.feige.im.chat.dto.UserConversationInfo;
import com.ds.feige.im.chat.entity.UserConversation;
import com.ds.feige.im.chat.mapper.UserConversationMapper;
import com.ds.feige.im.constants.CacheKeys;
import com.ds.feige.im.constants.ConversationType;
import com.ds.feige.im.constants.FeigeWarn;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author DC
 */
@Service
@Slf4j
public class ConversationServiceImpl extends ServiceImpl<UserConversationMapper, UserConversation> implements ConversationService {
    @Autowired
    @Qualifier("longIdKeyGenerator")
    IdKeyGenerator<Long> longIdKeyGenerator;
    @Autowired
    UserService userService;
    @Autowired
    RedissonClient redissonClient;

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
    public UserConversationInfo getUserConversation(long userId, long conversationId) {
        UserConversation conversation = baseMapper.getByUserAndConversationId(userId, conversationId);
        UserConversationInfo userConversationInfo = new UserConversationInfo();
        BeanUtils.copyProperties(conversation, userConversationInfo);
        return userConversationInfo;
    }

    @Override
    public Set<Long> getUserIdsByConversation(long conversationId) {
        Set<Long> users = baseMapper.findUsersByConversationId(conversationId);
        return users;
    }

    @Override
    public UserConversationInfo createSingleConversation(long userId, long targetId) {
        //判断会话是否已经存在
        UserConversation conversation = baseMapper.getByUserTargetAndTyppe(userId, targetId, ConversationType.SINGLE_CONVERSATION_TYPE);
        if (conversation != null) {
            UserConversationInfo result = new UserConversationInfo();
            BeanUtils.copyProperties(conversation, result);
            log.warn("Conversation already exists:userId={},targetId={},conversationType={},conversationId={}", userId, targetId, conversation, conversation.getConversationId());
            return result;
        }
        //判断userId是否存在
        UserInfo createUser = userService.getUserById(userId);
        UserInfo targetUser = null;
        targetUser = userService.getUserById(targetId);
        if (targetUser == null) {
            throw new WarnMessageException(FeigeWarn.USER_NOT_EXISTS);
        }
        String conversationLockId = userId >= targetId ? userId + "" + targetId : targetId + "" + userId;
        //加分布式锁
        RLock lock = redissonClient.getLock(CacheKeys.CONVERSATION_LOCK_PREFIX + conversationLockId);
        try {
            if (lock.tryLock(10, 10, TimeUnit.SECONDS)) {
                conversation = baseMapper.getByUserTargetAndTyppe(userId, targetId, ConversationType.SINGLE_CONVERSATION_TYPE);
                //双重检查
                if (conversation == null) {
                    long conversationId = longIdKeyGenerator.generateId();
                    List<UserConversation> conversations = Lists.newArrayList();
                    //如果是单聊,则需要为双方共同创建会话
                    conversation = buildConversation(userId, targetId, conversationId, ConversationType.SINGLE_CONVERSATION_TYPE, targetUser.getNickName(), targetUser.getAvatar());
                    UserConversation targetConversation = buildConversation(targetId, userId, conversationId, ConversationType.SINGLE_CONVERSATION_TYPE, createUser.getNickName(), createUser.getNickName());
                    conversations.add(conversation);
                    conversations.add(targetConversation);
                    saveBatch(conversations);
                    log.info("Create conversations success:conversationId={},count={},senderId={},targetId={}", conversationId, conversations.size(), userId, targetId);
                } else {
                    log.warn("Conversation already exits after lock:userId={},targetId={}", userId, targetId);
                }
            } else {
                log.error("Lock conversation fail:userId={},targetId={}", userId, targetId);
                throw new WarnMessageException(FeigeWarn.SYSTEM_BUSY);
            }
        } catch (Exception e) {
            log.error("Lock conversation error:userId={},targetId={}", userId, targetId);
            throw new WarnMessageException(e, FeigeWarn.SYSTEM_ERROR);
        } finally {
            lock.unlock();
        }
        UserConversationInfo result = new UserConversationInfo();
        BeanUtils.copyProperties(conversation, result);
        return result;
    }

    @Override
    public int deleteConversation(long userId, long targetId, int conversationType) {
        int i = baseMapper.deleteByUserAndTargetAndType(userId, targetId, conversationType);
        log.info("Delete conversation success:userId={},targetId={},count={}", userId, targetId, i);
        return i;
    }

    @Override
    public long deleteGroupConversations(long groupId) {
        List<UserConversation> conversations = baseMapper.findByTargetIdAndType(groupId, ConversationType.GROUP_CONVERSATION_TYPE);
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
            UserConversation uc = buildConversation(uid, groupId, finalConversationId, ConversationType.GROUP_CONVERSATION_TYPE, name, avatar);
            conversations.add(uc);
        });
        saveBatch(conversations);
        log.info("Create group conversations success:conversations={}", conversations);
        return conversationId;
    }

    @Override
    public void updateConversationName(long userId, long conversationId, String name) {
        UserConversation conversation = baseMapper.getByUserAndConversationId(userId, conversationId);
    }

    @Override
    public void updateConversationAvatar(long userId, long conversationId, String avatar) {

    }

    @Override
    public void updateConversationOption(long userId, long conversationId, String option) {

    }

    @Override
    public void updateGroupConversationName(long conversationId, String name) {

    }

    @Override
    public void updateGroupConversationAvatar(long conversationId, String avatar) {

    }

    public static UserConversation buildConversation(long userId, long targetId, long conversationId, int conversationType, String conversationName, String conversationAvatar) {
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
