package com.ds.feige.im.chat.mapper;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ds.feige.im.chat.entity.UserConversation;

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

    @Delete({"<script> ",
        "DELETE FROM t_user_conversation WHERE target_id=#{targetId} and conversation_type=#{conversationType} and user_id IN",
        "<foreach item='item' index='index' collection='userIds' open='(' separator=',' close=')'>", "#{item}",
        "</foreach>", "</script>"})
    int deleteByUsersAndTargetAndType(@Param("userIds") Collection<Long> userIds, long targetId, int conversationType);

    @Select({"<script> ", "SELECT * FROM t_user_conversation WHERE user_id=#{userId}  and conversation_id IN ",
        "<foreach item='item' index='index' collection='conversationIds' open='(' separator=',' close=')'>", "#{item}",
        "</foreach>", "</script>"})
    List<UserConversation> findByUserAndConversationIds(long userId,
        @Param("conversationIds") Collection<Long> conversationIds);

    @Select("SELECT * FROM t_user_conversation where conversation_id=#{conversationId}")
    List<UserConversation> findByConversationId(long conversationId);

    @Update("UPDATE t_user_conversation set last_event_time=#{lastEventTime} where conversation_id=#{conversationId}")
    int updateLastEventTime(long conversationId, Date lastEventTime);

    @Select("SELECT * FROM t_user_conversation where user_id=#{userId} and last_event_time>=#{lastEventTime}")
    List<UserConversation> findRecentConversations(long userId, Date lastEventTime);
}
