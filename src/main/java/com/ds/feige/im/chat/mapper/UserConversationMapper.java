package com.ds.feige.im.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ds.feige.im.chat.entity.UserConversation;
import org.apache.ibatis.annotations.Select;

public interface UserConversationMapper extends BaseMapper<UserConversation> {
    @Select("SELECT * FROM t_user_conversation where user_id=#{userId} and target_id=#{targetId} and conversation_type=#{conversationType}")
    UserConversation getConversation(long userId, long targetId, int conversationType);

    @Select("SELECT * FROM t_user_conversation where user_id=#{userId} and conversation_id=#{conversationId}")
    UserConversation getConversation(long userId, long conversationId);
}
