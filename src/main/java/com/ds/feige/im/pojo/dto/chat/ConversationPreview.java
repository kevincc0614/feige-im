package com.ds.feige.im.pojo.dto.chat;

/**
 * @author DC
 */
public class ConversationPreview {
    private long conversationId;
    private String conversationName;
    private String conversationAvatar;
    private long lastMsgId;
    private int unreadCount;
    private ChatMessage lastMsg;
    public long getConversationId() {
        return conversationId;
    }

    public void setConversationId(long conversationId) {
        this.conversationId = conversationId;
    }

    public long getLastMsgId() {
        return lastMsgId;
    }

    public void setLastMsgId(long lastMsgId) {
        this.lastMsgId = lastMsgId;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public ChatMessage getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(ChatMessage lastMsg) {
        this.lastMsg = lastMsg;
    }

    public String getConversationName() {
        return conversationName;
    }

    public void setConversationName(String conversationName) {
        this.conversationName = conversationName;
    }

    public String getConversationAvatar() {
        return conversationAvatar;
    }

    public void setConversationAvatar(String conversationAvatar) {
        this.conversationAvatar = conversationAvatar;
    }
}
