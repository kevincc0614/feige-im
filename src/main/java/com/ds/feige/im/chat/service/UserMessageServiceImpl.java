package com.ds.feige.im.chat.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ds.feige.im.chat.dto.ChatMsgAckResult;
import com.ds.feige.im.chat.dto.ConversationPreview;
import com.ds.feige.im.chat.dto.UserMsg;
import com.ds.feige.im.chat.entity.UserMessage;
import com.ds.feige.im.chat.mapper.UserMsgMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author DC
 */
@Service
public class UserMessageServiceImpl extends ServiceImpl<UserMsgMapper,UserMessage> implements UserMessageService {

    public UserMessageServiceImpl(){

    }
    @Override
    public long store(UserMsg message) {
        //TODO 缓存优化
        UserMessage entity=buildUserMessageEntity(message);
        save(entity);
        return entity.getMsgId();
    }
    @Override
    public void store(List<UserMsg> messages) {
        //TODO 缓存优化
        List<UserMessage> entities=messages.stream().map(msg->buildUserMessageEntity(msg)).collect(Collectors.toList());
        saveBatch(entities);
    }
    UserMessage buildUserMessageEntity(UserMsg message){
        UserMessage entity=new UserMessage();
        entity.setUserId(message.getUserId());
        entity.setMsgId(message.getMsgId());
        entity.setConversationId(message.getConversationId());
        entity.setState(0);
        return entity;
    }


    @Override
    public ChatMsgAckResult ackMsg(long userId, List<Long> msgIds) {
        //TODO msgIds不能过大,要设定上限
        int ackCount=baseMapper.batchUpdateAck(userId,msgIds);
        ChatMsgAckResult result=new ChatMsgAckResult();
        result.setAckCount(ackCount);
        return result;
    }

    @Override
    public List<ConversationPreview> getConversationPreview(long userId) {
        Long minUnAckMsgId=baseMapper.getMinUnAckMsgId(userId);
        if(minUnAckMsgId==null){
            minUnAckMsgId=0L;
        }
        List<ConversationPreview> previews=baseMapper.getConversationPreview(userId,minUnAckMsgId);
        return previews;
    }
}
