package com.ds.feige.im.chat.controller;

import com.ds.feige.im.account.dto.UserRequest;
import com.ds.feige.im.account.service.UserService;
import com.ds.feige.im.chat.dto.*;
import com.ds.feige.im.chat.service.ChatService;
import com.ds.feige.im.constants.SocketPaths;
import com.ds.feige.im.gateway.service.SessionUserService;
import com.ds.feige.im.gateway.socket.annotation.SocketController;
import com.ds.feige.im.gateway.socket.annotation.SocketRequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

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
    public SendMessageResult send(@RequestBody @Valid ConversationMessageRequest request) {

        return this.chatService.sendMsg(request);
    }

    /**
     * 客户端确认收到聊天消息
     */
    @SocketRequestMapping(SocketPaths.CS_ACK_CHAT_MESSAGE)
    public ChatMessageAckResult ack(@RequestBody @Valid ChatMessageAckRequest request) {
        ChatMessageAckResult result = chatService.ackMsg(request.getUserId(), request.getMsgIds());
        return result;
    }

    @SocketRequestMapping(SocketPaths.CS_PULL_CHAT_MESSAGE)
    public List<ChatMessage> pull(@RequestBody @Valid ConversationMessageQueryRequest request) {
        List<ChatMessage> messages = chatService.pullMsg(request);
        return messages;
    }

    @SocketRequestMapping(SocketPaths.CS_CONVERSATION_PREVIEWS)
    public List<ConversationPreview> getConversationPreviews(@RequestBody UserRequest request) {
        List<ConversationPreview> previews = chatService.getConversationPreviews(request.getUserId());
        return previews;
    }

    @SocketRequestMapping(value = SocketPaths.CS_READ_CHAT_MESSAGE, response = false)
    public void readMessage(@RequestBody ReadMessageRequest req) {
        chatService.readMessage(req);
    }
}
