package com.ds.feige.im.chat.controller;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.google.common.collect.Maps;

/**
 * @author DC
 */
@SocketController
@SocketRequestMapping
public class ChatController {
    @Autowired
    ChatService chatService;
    @Autowired
    UserService userService;
    @Autowired
    SessionUserService sessionUserService;
    @Autowired
    ConversationService conversationService;

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

    /**
     * 客户端确认收到聊天消息
     */
    @SocketRequestMapping(SocketPaths.CS_ACK_CHAT_MESSAGE)
    public ChatMessageAckResult ack(@RequestBody @Valid MessageAckRequest request) {
        ChatMessageAckResult result = chatService.ackMessages(request.getUserId(), request.getMsgIds());
        return result;
    }

    @SocketRequestMapping(SocketPaths.CS_PULL_CHAT_MESSAGE)
    public List<MessageToUser> pull(@RequestBody @Valid ConversationMessageQueryRequest request) {
        List<MessageToUser> messages = chatService.pullMessages(request);
        return messages;
    }

    @SocketRequestMapping(SocketPaths.CS_CONVERSATION_PREVIEWS)
    public Map<String, Object> getConversationPreviews(@RequestBody ConversationPreviewRequest request) {
        Collection<ConversationPreview> previews = chatService.getConversationPreviews(request.getUserId());
        Map<String, Object> result = Maps.newHashMap();
        result.put("previews", previews);
        if (request.getLastSendMsgIds() != null && request.getLastSendMsgIds().size() > 0) {
            List<MessageToUser> lastSendMessages =
                chatService.getUserMessages(request.getUserId(), request.getLastSendMsgIds());
            result.put("lastSendMessages", lastSendMessages);

        }
        return result;
    }

    @SocketRequestMapping(value = SocketPaths.CS_READ_CHAT_MESSAGE, response = false)
    public void readMessage(@RequestBody ReadMessageRequest req) {
        chatService.readMessage(req);
    }

    @SocketRequestMapping(SocketPaths.CS_CONVERSATION_INFO)
    public UserConversationInfo getConversation(@UserId long userId,
        @RequestParam("conversationId") long conversationId) {
        return conversationService.getUserConversation(userId, conversationId);
    }
}
