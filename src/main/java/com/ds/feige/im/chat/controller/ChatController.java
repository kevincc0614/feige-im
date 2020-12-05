package com.ds.feige.im.chat.controller;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.socket.WebSocketSession;

import com.ds.feige.im.account.service.UserService;
import com.ds.feige.im.chat.dto.*;
import com.ds.feige.im.chat.service.ChatService;
import com.ds.feige.im.chat.service.ConversationService;
import com.ds.feige.im.constants.SessionAttributeKeys;
import com.ds.feige.im.constants.SocketPaths;
import com.ds.feige.im.gateway.domain.SessionUser;
import com.ds.feige.im.gateway.service.SessionUserService;
import com.ds.feige.im.gateway.socket.annotation.SocketController;
import com.ds.feige.im.gateway.socket.annotation.SocketRequestMapping;
import com.ds.feige.im.gateway.socket.annotation.UserId;
import com.ds.feige.im.gateway.socket.connection.ConnectionMeta;
import com.ds.feige.im.mark.dto.MarkMessageInfo;
import com.ds.feige.im.mark.dto.MarkMessageQueryRequest;
import com.ds.feige.im.mark.service.MarkMessageService;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;

/**
 * @author DC
 */
@SocketController
public class ChatController {
    @Autowired
    ChatService chatService;
    @Autowired
    UserService userService;
    @Autowired
    SessionUserService sessionUserService;
    @Autowired
    ConversationService conversationService;
    @Autowired
    MarkMessageService markMessageService;

    /**
     * 发送聊天消息
     */
    @SocketRequestMapping(SocketPaths.CS_SEND_CHAT_MESSAGE)
    public MessageToUser send(WebSocketSession session, @RequestBody @Valid MessageToConversation request) {
        SessionUser sessionUser = sessionUserService.getSessionUser(request.getUserId());
        // TODO 这部分代码有待优化,代码结构和层次需要更加清晰
        String deviceId = (String)session.getAttributes().get(SessionAttributeKeys.DEVICE_ID);
        ConnectionMeta connectionMeta = sessionUser.getConnectionMetaByDeviceId(deviceId);
        request.setSenderConnectionId(connectionMeta.getSessionId());
        return this.chatService.sendToConversation(request);
    }

    @SocketRequestMapping(SocketPaths.CS_PULL_CHAT_MESSAGE)
    public List<MessageToUser> pull(@RequestBody @Valid ConversationMessageQueryRequest request) {
        List<MessageToUser> messages = chatService.pullMessages(request);
        Map<Long, MessageToUser> map = Maps.newHashMap();
        if (messages != null && !messages.isEmpty()) {
            messages.forEach(m -> {
                map.put(m.getMsgId(), m);
            });
            MarkMessageQueryRequest queryRequest = new MarkMessageQueryRequest();
            queryRequest.setMsgIds(map.keySet());
            List<MarkMessageInfo> markMessageInfos = markMessageService.query(queryRequest);
            markMessageInfos.forEach(markMessageInfo -> {
                MessageToUser entity = map.get(markMessageInfo.getMsgId());
                if (entity != null) {
                    boolean favorites = markMessageInfo.getFavorites() != null && markMessageInfo.getFavorites();
                    boolean remarked = !Strings.isNullOrEmpty(markMessageInfo.getRemark());
                    if (favorites && remarked) {
                        entity.setMarkType(3);
                    } else if (favorites) {
                        entity.setMarkType(1);
                    } else if (remarked) {
                        entity.setMarkType(2);
                    }
                }
            });
        }
        return messages;
    }

    @SocketRequestMapping(SocketPaths.CS_RECENT_CONVERSATION_LIST)
    public Map<String, Object> getRecentConversations(@RequestBody @Valid RecentConversationsRequest request) {
        Collection<UserConversationDetails> previews =
            chatService.getRecentConversations(request.getUserId(), request.getLastEventTime());
        Map<String, Object> result = Maps.newHashMap();
        result.put("previews", previews);
        if (request.getLastSendMsgIds() != null && request.getLastSendMsgIds().size() > 0) {
            List<MessageToUser> lastSendMessages =
                chatService.getUserMessages(request.getUserId(), request.getLastSendMsgIds());
            result.put("lastSendMessages", lastSendMessages);

        }
        return result;
    }

    @SocketRequestMapping(value = SocketPaths.CS_READ_CHAT_MESSAGE)
    public long readMessage(@RequestBody ReadMessageRequest req,
        @RequestAttribute(SessionAttributeKeys.DEVICE_ID) String deviceId) {
        SessionUser sessionUser = sessionUserService.getSessionUser(req.getUserId());
        // TODO 这部分代码有待优化,代码结构和层次需要更加清晰
        ConnectionMeta connectionMeta = sessionUser.getConnectionMetaByDeviceId(deviceId);
        // 把发送已读请求的链接排除,避免重复推送
        req.setReaderConnectionId(connectionMeta.getSessionId());
        chatService.userReadMessages(req);
        return System.currentTimeMillis();
    }

    @SocketRequestMapping(SocketPaths.CS_CONVERSATION_INFO)
    public UserConversationDetails getConversationDetails(@UserId long userId,
        @RequestParam("conversationId") long conversationId) {
        return chatService.getConversationDetails(userId, conversationId);
    }
}
