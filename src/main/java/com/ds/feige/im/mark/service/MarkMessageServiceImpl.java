package com.ds.feige.im.mark.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ds.base.nodepencies.exception.WarnMessageException;
import com.ds.feige.im.chat.dto.MessageToUser;
import com.ds.feige.im.chat.service.ChatService;
import com.ds.feige.im.common.util.BeansConverter;
import com.ds.feige.im.constants.FeigeWarn;
import com.ds.feige.im.mark.dto.*;
import com.ds.feige.im.mark.entity.MarkMessage;
import com.ds.feige.im.mark.mapper.MarkMessageMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * @author DC
 */
@Slf4j
@Service
public class MarkMessageServiceImpl extends ServiceImpl<MarkMessageMapper, MarkMessage> implements MarkMessageService {
    @Autowired
    ChatService chatService;

    @Override
    public long mark(MarkRequest request) {
        log.info("Mark message start:request={}", request);
        long msgId = request.getMsgId();
        MessageToUser chatMessage = chatService.getMessage(msgId);
        if (chatMessage == null) {
            throw new WarnMessageException(FeigeWarn.CHAT_MSG_NOT_EXISTS);
        }
        String msgContent = chatMessage.getMsgContent();
        int msgType = chatMessage.getMsgType();
        long conversationId = chatMessage.getConversationId();
        long userId = request.getUserId();
        String title = request.getTitle();
        String remark = request.getRemark();
        MarkMessage message = new MarkMessage();
        message.setMarkType(request.getMarkType());
        message.setUserId(userId);
        message.setConversationId(conversationId);
        message.setMsgType(msgType);
        message.setMsgContent(msgContent);
        message.setMsgId(msgId);
        message.setTitle(title);
        message.setRemark(remark);
        baseMapper.insert(message);
        log.info("Mark message success:markMessage={}", message);
        return message.getId();
    }

    @Override
    public boolean cancelMark(CancelMarkRequest request) {
        final long userId = request.getUserId();
        final long markId = request.getMarkId();
        MarkMessage message = super.getById(markId);
        if (message == null) {
            log.error("Mark message not exists:markId={}", markId);
            return false;
        }
        if (message.getUserId() != userId) {
            throw new WarnMessageException(FeigeWarn.PERMISSION_DIED);
        }
        boolean result = super.removeById(markId);
        log.info("Cancel mark:markId={}", markId);
        return result;
    }

    @Override
    public void updateRemark(UpdateRemarkRequest request) {
        MarkMessage message = super.getById(request.getMarkId());
        if (message == null) {
            throw new WarnMessageException(FeigeWarn.FAVORITE_MARK_MESSAGE_NOT_EXISTS);
        }
        message.setRemark(request.getRemark());
        message.setTitle(request.getTitle());
        save(message);
    }

    @Override
    public List<MarkMessageInfo> query(MarkMessageQueryRequest request) {
        QueryWrapper<MarkMessage> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", request.getUserId());
        if (request.getConversationId() != null) {
            wrapper.eq("conversation_id", request.getConversationId());
        }
        if (request.getStart() != null) {
            wrapper.gt("update_time", request.getStart());
        }
        if (request.getEnd() != null) {
            wrapper.lt("update_time", request.getEnd());
        }
        if (request.getMarkType() != null) {
            wrapper.eq("mark_type", request.getMarkType());
        }
        List<MarkMessage> messages = super.list(wrapper);
        return BeansConverter.markMessagesToMarkMessageInfos(messages);
    }

    @Override
    public MarkMessageInfo getByMarkId(long id) {
        MarkMessage message = super.getById(id);
        return BeansConverter.markMessageToMarkMessageInfo(message);
    }
}
