package com.ds.feige.im.chat.dto;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import com.ds.feige.im.common.util.JsonUtils;
import com.google.common.collect.Maps;

/**
 * @author DC
 */
public class MessageContent {
    protected Map<String, Object> values = Maps.newHashMap();

    public void put(String key, Object value) {
        values.put(key, value);
    }
    public String toJson() throws IOException {
        return JsonUtils.toJson(values);
    }

    public static class GroupCreatedMessage extends MessageContent {
        public GroupCreatedMessage(long groupId, long operatorId, long conversationId) {
            super.put("event", "group.created");
            super.put("groupId", groupId);
            super.put("operatorId", operatorId);
            super.put("conversationId", conversationId);
        }
    }

    public static class GroupDisbandMessage extends MessageContent {
        public GroupDisbandMessage(long groupId, long operatorId) {
            super.put("event", "group.disbanded");
            super.put("groupId", groupId);
            super.put("operatorId", operatorId);
        }
    }

    public static class GroupUserJoinMessage extends MessageContent {
        public GroupUserJoinMessage(long groupId, Collection<Long> joinedUsers, long operatorId, int joinType) {
            super.put("event", "group.user-joined");
            super.put("groupId", groupId);
            super.put("joinedUsers", joinedUsers);
            super.put("operatorId", operatorId);
            super.put("joinType", joinType);
        }
    }

    public static class GroupUserKickedMessage extends MessageContent {
        public GroupUserKickedMessage(long groupId, Collection<Long> kickedUsers, long operatorId) {
            super.put("event", "group.user-kicked");
            super.put("groupId", groupId);
            super.put("operatorId", operatorId);
            super.put("kickedUsers", kickedUsers);
        }
    }

    public static class GroupUserExitedMessage extends MessageContent {
        public GroupUserExitedMessage(long groupId, long exitUserId) {
            super.put("event", "group.user-exited");
            super.put("groupId", groupId);
            super.put("userId", exitUserId);
        }
    }

    public static class TextMessage extends MessageContent {
        public TextMessage(String text, Collection<Long> atUsers) {
            super.put("text", text);
            super.put("atUsers", atUsers);
        }
    }

    public static class PicMessage extends MessageContent {
        public PicMessage(String downloadUrl, int width, int height) {
            super.put("downloadUrl", downloadUrl);
            super.put("width", width);
            super.put("height", height);
        }
    }
}
