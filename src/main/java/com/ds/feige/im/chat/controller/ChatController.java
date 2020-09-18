package com.ds.feige.im.chat.controller;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;

import com.ds.feige.im.account.service.UserService;
import com.ds.feige.im.chat.dto.*;
import com.ds.feige.im.chat.service.ChatService;
import com.ds.feige.im.constants.SocketPaths;
import com.ds.feige.im.gateway.service.SessionUserService;
import com.ds.feige.im.gateway.socket.annotation.SocketController;
import com.ds.feige.im.gateway.socket.annotation.SocketRequestMapping;
import com.google.common.collect.Maps;

/**
 * @author DC
 */
@SocketController
@SocketRequestMapping
public class ChatController {
    ChatService chatService;
    UserService userService;
    SessionUserService sessionUserService;

    @Autowired
    public ChatController(ChatService chatService, UserService userService, SessionUserService sessionUserService) {
        this.chatService = chatService;
        this.userService = userService;
        this.sessionUserService = sessionUserService;
    }

    /**
     * 发送聊天消息
     */
    @SocketRequestMapping(SocketPaths.CS_SEND_CHAT_MESSAGE)
    public SendMessageResult send(@RequestBody @Valid MessageToConversation request) {

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
}
