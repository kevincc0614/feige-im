package com.ds.feige.im.constants;

/**
 * Socket Path 常量类
 *
 * @author DC
 */
public class SocketPaths {
    public static final String CS_USER_LOGOUT = "/user/logout";
    public static final String CS_PING_PONG = "/ping-pong";
    public static final String CS_SEND_CHAT_MESSAGE = "/chat/message/send";
    // public static final String CS_ACK_CHAT_MESSAGE = "/chat/message/ack";
    public static final String CS_PULL_CHAT_MESSAGE = "/chat/message/pull";
    public static final String CS_READ_CHAT_MESSAGE = "/chat/message/read";
    public static final String CS_RECENT_CONVERSATION_LIST = "/chat/conversation/recent-list";
    public static final String CS_CONVERSATION_INFO = "/chat/conversation/info";

    public static final String CS_PULL_EVENTS = "/event/pull";
    public static final String CS_UPDATE_EVENT_CHECKPOINT = "/event/checkpoint/update";
    public static final String SC_PUSH_EVENT = "/event/receive";
    public static final String SC_REMOTE_LOGIN = "/user/remote-login";

    // public static final String SC_PUSH_CHAT_MESSAGE = "/chat/message/receive";
    public static final String SC_CHAT_MESSAGE_READ_RECEIPT = "/chat/message/read-receipt";

    public static final String CS_CREATE_GROUP = "/group/create";
    public static final String CS_DISBAND_GROUP = "/group/disband";
    public static final String CS_USER_JOIN_GROUP = "/group/user/invite-join";
    public static final String CS_USER_EXIT_GROUP = "/group/user/exit";
    public static final String CS_KICK_GROUP_USER = "/group/user/kick";
    public static final String CS_SET_GROUP_USER_ROLE = "/group/user/set-role";
    public static final String CS_GET_GROUP_INFO = "/group/info";
    public static final String CS_GET_GROUP_MEMBERS = "/group/members";
    public static final String CS_PUB_GROUP_ANNOUNCEMENT = "/group/announcement/pub";

    public static final String CS_MARK_FAVORITES = "/mark/favorites";
    public static final String CS_MARK_GET = "/mark/get";
    public static final String CS_MARK_CANCEL_FAVORITES = "/mark/cancel-favorites";
    public static final String CS_MARK_REMARK = "/mark/remark";
    public static final String CS_MARK_CANCEL_REMARK = "/mark/cancel-remark";
    public static final String CS_MARK_QUERY = "/mark/query";

}
