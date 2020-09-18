package com.ds.feige.im.constants;

import java.util.HashMap;
import java.util.Map;

public class AMQPConstants {
    public static final String SERVER_FORWARD_MESSAGE_QUEUE = "server.forward.message#";
    public static final String SERVER_FORWARD_DISCONNECT_MESSAGE_QUEUE = "server.forward.disconnect-message#";
    public static final String[] ALL_DYNAMIC_QUEUES =
        new String[] {SERVER_FORWARD_MESSAGE_QUEUE, SERVER_FORWARD_DISCONNECT_MESSAGE_QUEUE};
    public static final Map<String, String[]> BINDINGS = new HashMap<>();
    static {
        BINDINGS.put(RoutingKeys.GROUP_CREATED,
            new String[] {QueueNames.GROUP_CREATED_BROADCAST, QueueNames.GROUP_CREATED_CONVERSATION});
        BINDINGS.put(RoutingKeys.GROUP_USER_JOINED, new String[] {QueueNames.GROUP_USER_JOINED_BROADCAST});
        BINDINGS.put(RoutingKeys.GROUP_USER_EXITED, new String[] {QueueNames.GROUP_USER_EXITED_BROADCAST});
        BINDINGS.put(RoutingKeys.GROUP_DISBANDED, new String[] {QueueNames.GROUP_DISBANDED_BROADCAST});
        BINDINGS.put(RoutingKeys.GROUP_USER_KICKED, new String[] {QueueNames.GROUP_USER_KICKED_BROADCAST});
        BINDINGS.put(RoutingKeys.GROUP_CREATED, new String[] {QueueNames.GROUP_CREATED_BROADCAST});
        BINDINGS.put(RoutingKeys.GROUP_USER_ROLE_UPDATED, new String[] {QueueNames.GROUP_USER_ROLE_UPDATED_BROADCAST});
        BINDINGS.put(RoutingKeys.CONVERSATION_SEND_MESSAGE, new String[] {QueueNames.CONVERSATION_SEND_MESSAGE_SYNC});
        BINDINGS.put(RoutingKeys.CONVERSATION_READ_MESSAGE,
            new String[] {QueueNames.CONVERSATION_READ_MESSAGE_RECEIPT});
    }

    public static final class RoutingKeys {
        public static final String GROUP_CREATED = "group.created";
        public static final String GROUP_USER_JOINED = "group.user.joined";
        public static final String GROUP_USER_EXITED = "group.user.exited";
        public static final String GROUP_DISBANDED = "group.disbanded";
        public static final String GROUP_USER_KICKED = "group.user.kicked";
        public static final String GROUP_USER_ROLE_UPDATED = "group.user.role-updated";
        public static final String CONVERSATION_SEND_MESSAGE = "conversation.send.message";
        public static final String CONVERSATION_READ_MESSAGE = "conversation.read.message";
    }

    public static final class QueueNames {
        public static final String GROUP_CREATED_BROADCAST = "group.created.broadcast";
        // 创建会话
        public static final String GROUP_CREATED_CONVERSATION = "group.created.conversation";
        public static final String GROUP_USER_JOINED_BROADCAST = "group.user.joined.broadcast";
        public static final String GROUP_USER_EXITED_BROADCAST = "group.user.exited.broadcast";
        public static final String GROUP_DISBANDED_BROADCAST = "group.disbanded.broadcast";
        public static final String GROUP_USER_KICKED_BROADCAST = "group.user.kicked.broadcast";
        public static final String GROUP_USER_ROLE_UPDATED_BROADCAST = "group.user.role-updated.broadcast";
        public static final String CONVERSATION_SEND_MESSAGE_SYNC = "conversation.send.message.sync";
        public static final String CONVERSATION_READ_MESSAGE_RECEIPT = "conversation.read.message.receipt";
    }
}
