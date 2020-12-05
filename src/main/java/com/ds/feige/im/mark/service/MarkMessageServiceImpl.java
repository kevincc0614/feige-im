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
import com.google.common.base.Strings;

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
    public long favorites(FavoritesRequest request) {
        log.info("Favorites message start:request={}", request);
        long msgId = request.getMsgId();
        long userId = request.getUserId();
        // 判断消息是否存在
        MarkMessage markMessage = baseMapper.getByUserAndMsgId(userId, msgId);
        if (markMessage == null) {
            markMessage = createByMsgId(msgId, userId);
        }
        markMessage.setFavorites(true);
        saveOrUpdate(markMessage);
        log.info("Favorites message success:markId={}", markMessage.getId());
        return markMessage.getId();
    }

    @Override
    public boolean cancelFavorites(CancelFavoritesRequest request) {
        long markId = request.getMarkId();
        MarkMessage markMessage = getAndCheckAccess(request.getUserId(), markId);
        // 如果没有备注信息,删除记录
        if (Strings.isNullOrEmpty(markMessage.getRemark())) {
            log.info("Cancel favorites message success:markId={}", markMessage.getId());
            return removeById(markId);

        } else {
            // 如果有备注信息,则该条数据还不能删除
            markMessage.setFavorites(false);
            log.info("Cancel favorites message success:markId={}", markMessage.getId());
            return updateById(markMessage);

        }

    }

    public MarkMessage createByMsgId(long msgId, long userId) {
        MessageToUser chatMessage = chatService.getMessage(msgId);
        if (chatMessage == null) {
            throw new WarnMessageException(FeigeWarn.CHAT_MSG_NOT_EXISTS);
        }
        MarkMessage markMessage = new MarkMessage();
        markMessage.setFavorites(true);
        markMessage.setUserId(userId);
        markMessage.setConversationId(chatMessage.getConversationId());
        markMessage.setMsgType(chatMessage.getMsgType());
        markMessage.setMsgContent(chatMessage.getMsgContent());
        markMessage.setMsgId(msgId);
        return markMessage;
    }

    @Override
    public long remark(RemarkRequest request) {
        long userId = request.getUserId();
        long msgId = request.getMsgId();
        String remark = request.getRemark();
        String title = request.getTitle();
        MarkMessage message = baseMapper.getByUserAndMsgId(userId, msgId);
        // 如果不存在,则需要创建一条记录
        if (message == null) {
            message = createByMsgId(msgId, userId);
        }
        // TODO 判断长度
        message.setRemark(remark);
        message.setTitle(title);
        saveOrUpdate(message);
        return message.getId();
    }

    public MarkMessage getAndCheckAccess(long userId, long markId) {
        MarkMessage markMessage = getById(markId);
        if (markMessage == null) {
            throw new WarnMessageException(FeigeWarn.MARK_MESSAGE_NOT_EXISTS);
        }
        // 只能取消收藏自己标记的消息
        if (userId != markMessage.getUserId()) {
            throw new WarnMessageException(FeigeWarn.PERMISSION_DIED);
        }
        return markMessage;
    }

    @Override
    public boolean cancelRemark(CancelRemarkRequest request) {
        long userId = request.getUserId();
        long markId = request.getMarkId();
        MarkMessage markMessage = getAndCheckAccess(userId, markId);
        // 如果没有收藏,可以删除
        if (markMessage.getFavorites() == null || !markMessage.getFavorites()) {
            return removeById(markId);
        } else {
            markMessage.setRemark(null);
            return updateById(markMessage);
        }
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
        if (request.getFavorites() != null) {
            wrapper.eq("favorites", request.getFavorites());
        }
        if (request.getRemarked() != null) {
            // 有备注的
            if (request.getRemarked()) {
                wrapper.isNotNull("remark");
            } else {
                wrapper.isNull("remark");
            }
        }
        if (request.getMsgIds() != null && !request.getMsgIds().isEmpty()) {
            wrapper.in("msg_id", request.getMsgIds());
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
