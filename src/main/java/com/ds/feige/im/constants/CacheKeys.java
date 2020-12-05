package com.ds.feige.im.constants;

public class CacheKeys {
    public static final String SESSION_USERS_MAP = "feige-im.user.session-users";
    public static final String SESSION_USER_STATE = "feige-im.user.state.";
    public static final String USER_ENTITY = "feige-im.user.entity.";
    public static final String USER_TOKEN = "feige-im.user.token.";
    public static final String SESSION_USER_CONNECTIONS = "feige-im.user.connections.";
    public static final String SESSION_USER_CONNECTION = "feige-im.user.connection.";
    public static final String SESSION_USER_LOCK_PREFIX = "feige-im.user.session-lock.";
    public static final String CONVERSATION_LOCK_PREFIX = "feige-im.conversation.lock.";
    public static final String CHAT_GROUP_INFO = "feige-im.chat.group.";

    public static final String CHAT_MESSAGE_READ_RECEIPTS = "feige-im.chat.message.read-receipts.";
    public static final String USER_EVENT_SEQ = "feige-im.user.event.seq";
    /** 用户未读会话列表缓存ID列表,已最后消息时间作为SetScores */
    public static final String USER_CONVERSATION_ID_SET = "feige-im.user.conversation-ids.";
    public static final String CONVERSATION_LAST_MSG_ID = "feige-im.conversation.last-msg-id.";
    /** 用户会话未读消息的Map key是msgId,value 是MessgeOfUser */
    public static final String USER_CONVERSATION_UNREAD_MESSAGES = "D.feige-im.user.conversation.unread.";
    /** 用户总未读 */
    public static final String USER_UNREAD_TOTAL = "D.feige-im.user.unread.total.";
}
