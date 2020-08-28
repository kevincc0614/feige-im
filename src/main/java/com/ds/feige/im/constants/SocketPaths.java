package com.ds.feige.im.constants;

/**
 * Socket Path 常量类
 *
 * @author DC
 */
public class SocketPaths {
    public static final String CS_SEND_CHAT_MESSAGE = "/chat/message/send";
    public static final String CS_ACK_CHAT_MESSAGE = "/chat/message/ack";
    public static final String CS_PULL_CHAT_MESSAGE = "/chat/message/pull";
    public static final String CS_CONVERSATION_PREVIEWS = "/conversation/previews";


    public static final String SC_PUSH_CHAT_MESSAGE = "/chat/message/receive";


    public static final String CS_CREATE_GROUP="/group/create";
    public static final String CS_DISBAND_GROUP="/group/disband";
    public static final String CS_USER_JOIN_GROUP="/group/user/invite-join";
    public static final String CS_USER_EXIT_GROUP="/group/user/exit";
    public static final String CS_KICK_GROUP_USER="/group/user/kick";
    public static final String CS_SET_GROUP_USER_ROLE="/group/user/set-role";
    public static final String CS_GET_GROUP_INFO="/group/info";
    public static final String CS_GET_GROUP_USERS="/group/users";

}
