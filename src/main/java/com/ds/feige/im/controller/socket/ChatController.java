package com.ds.feige.im.controller.socket;

import com.ds.feige.im.constants.SocketPaths;
import com.ds.feige.im.pojo.dto.ChatMsgRequest;
import com.ds.feige.im.pojo.dto.chat.*;
import com.ds.feige.im.pojo.dto.message.SendMsgResult;
import com.ds.feige.im.pojo.dto.user.UserRequest;
import com.ds.feige.im.service.chat.ChatService;
import com.ds.feige.im.service.user.SessionUserService;
import com.ds.feige.im.service.user.UserService;
import com.ds.feige.im.socket.annotation.SocketController;
import com.ds.feige.im.socket.annotation.SocketRequestMapping;
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
    public SendMsgResult send(@RequestBody @Valid ChatMsgRequest request) {
        return this.chatService.sendMsg(request);
    }

    /**
     * 客户端确认收到聊天消息
     */
    @SocketRequestMapping(SocketPaths.CS_ACK_CHAT_MESSAGE)
    public ChatMsgAckResult ack(@RequestBody @Valid ChatMsgAckRequest request) {
        ChatMsgAckResult result = chatService.ackMsg(request.getUserId(), request.getMsgIds());
        return result;
    }

    @SocketRequestMapping(SocketPaths.CS_PULL_CHAT_MESSAGE)
    public List<ChatMessage> pull(@RequestBody @Valid PullConversationMsgRequest request) {
        List<ChatMessage> messages = chatService.pullMsg(request);
        return messages;
    }

    @SocketRequestMapping(SocketPaths.CS_CONVERSATION_PREVIEWS)
    public List<ConversationPreview> getConversationPreviews(@RequestBody UserRequest request) {
        List<ConversationPreview> previews=chatService.getConversationPreviews(request.getUserId());
        return previews;
    }
}
