package com.ds.feige.im.service.chat;

import com.ds.feige.im.pojo.dto.chat.UserConversationInfo;

public interface ConversationService {
    /**
     * 获取聊天会话
     * @param userId 会话所属人ID
     * @param targetId 目标ID
     * @param conversationType 会话类型 单聊或群聊
     * */
    UserConversationInfo getOrCreateConversation(long userId, long targetId, int conversationType);


}
