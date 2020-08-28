package com.ds.feige.im.service.chat;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ds.base.nodepencies.exception.WarnMessageException;
import com.ds.base.nodepencies.strategy.id.IdKeyGenerator;
import com.ds.feige.im.constants.ConversationType;
import com.ds.feige.im.constants.FeigeWarn;
import com.ds.feige.im.mapper.UserConversationMapper;
import com.ds.feige.im.pojo.dto.chat.UserConversationInfo;
import com.ds.feige.im.pojo.entity.UserConversation;
import com.ds.feige.im.service.group.GroupUserService;
import com.ds.feige.im.service.user.UserService;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
/**
 * @author DC
 */
@Service
public class ConversationServiceImpl extends ServiceImpl<UserConversationMapper,UserConversation> implements ConversationService{
    @Autowired
    @Qualifier("longIdKeyGenerator")
    IdKeyGenerator<Long> longIdKeyGenerator;
    @Autowired
    UserService userService;
    @Autowired
    GroupUserService groupUserService;
    static final Logger LOGGER= LoggerFactory.getLogger(ConversationServiceImpl.class);
    @Override
    public UserConversationInfo getOrCreateConversation(long userId, long targetId, int conversationType) {
        LOGGER.info("Get or create conversation:userId={},targetId={},conversationType={}",userId,targetId,conversationType);
        //判断userId是否存在
        boolean senderExists=userService.userExists(userId);
        if(!senderExists){
            throw new WarnMessageException(FeigeWarn.USER_NOT_EXISTS);
        }
        //targetUser 是否存在 TODO 错误码最好带上ID信息
        if(conversationType== ConversationType.SINGLE_CONVERSATION_TYPE&&!userService.userExists(targetId)){
            throw new WarnMessageException(FeigeWarn.USER_NOT_EXISTS);
        }
        //targetGroup 是否存在
        if(conversationType==ConversationType.SINGLE_CONVERSATION_TYPE&&!groupUserService.groupExists(targetId)){
            throw new WarnMessageException(FeigeWarn.GROUP_NOT_EXISTS);
        }
        //获取会话,如果没有会话则创建一个
        //TODO 需要解决并发问题
        UserConversation senderConversation=baseMapper.getConversation(userId,targetId,conversationType);
        if(senderConversation==null){
            //如果是单聊,则需要为双方共同创建会话
            long conversationId=longIdKeyGenerator.generateId();
            List<UserConversation> conversations=Lists.newArrayListWithCapacity(2);
            senderConversation=new UserConversation();
            senderConversation.setConversationId(conversationId);
            senderConversation.setUserId(userId);
            senderConversation.setTargetId(targetId);
            senderConversation.setConversationType(conversationType);
            conversations.add(senderConversation);
            if(conversationType==ConversationType.SINGLE_CONVERSATION_TYPE){
                UserConversation targetConversation=new UserConversation();
                targetConversation.setConversationId(conversationId);
                targetConversation.setUserId(targetId);
                targetConversation.setTargetId(userId);
                targetConversation.setConversationType(conversationType);
                conversations.add(targetConversation);
            }
            saveBatch(conversations);
            LOGGER.info("Create conversation success:conversationId={},senderId={},targetId={}",conversationId,userId,targetId);
        }
        UserConversationInfo result=new UserConversationInfo();
        BeanUtils.copyProperties(senderConversation,result);
        return result;
    }
}
