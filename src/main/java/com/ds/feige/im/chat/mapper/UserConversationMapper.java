package com.ds.feige.im.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ds.feige.im.chat.entity.UserConversation;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

public interface UserConversationMapper extends BaseMapper<UserConversation> {
    @Select("SELECT * FROM t_user_conversation where user_id=#{userId} and target_id=#{targetId} and conversation_type=#{conversationType}")
    UserConversation getByUserTargetAndTyppe(long userId, long targetId, int conversationType);

    @Select("SELECT conversation_id FROM t_user_conversation where target_id=#{targetId} and conversation_type=#{conversationType} limit 1")
    Long getConversationIdByTargetId(long targetId, int conversationType);

    @Select("SELECT * FROM t_user_conversation where user_id=#{userId} and conversation_id=#{conversationId}")
    UserConversation getByUserAndConversationId(long userId, long conversationId);

    @Select("SELECT * FROM t_user_conversation where target_id=#{targetId} and conversation_type=#{conversationType}")
    List<UserConversation> findByTargetIdAndType(long targetId, long conversationType);

    @Select("SELECT user_id FROM t_user_conversation where conversation_id=#{conversationId}")
    Set<Long> findUsersByConversationId(long conversationId);

    @Delete("DELETE FROM t_user_conversation where conversation_id=#{conversationId}")
    int deleteByConversationId(long conversationId);

    @Delete("DELETE FROM t_user_conversation where target_id=#{targetId} and conversation_type=#{conversationType}")
    int deleteByTargetIdAndType(long targetId, int conversationType);

    @Delete("DELETE FROM t_user_conversation WHERE user_id=#{userId} AND target_id=#{targetId} and conversation_type=#{conversationType}")
    int deleteByUserAndTargetAndType(long userId, long targetId, int conversationType);

    @Select("SELECT COUNT(*) FROM t_user_conversation WHERE conversation_id=#{conversationId}")
    int getMembersNum(long conversationId);
}
