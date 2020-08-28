package com.ds.feige.im.chat.service;

import com.ds.feige.im.chat.dto.UserConversationInfo;

public interface ConversationService {
    /**
     * 获取聊天会话
     * @param userId 会话所属人ID
     * @param targetId 目标ID
     * @param conversationType 会话类型 单聊或群聊
     * */
    UserConversationInfo getOrCreateConversation(long userId, long targetId, int conversationType);


}
