package com.ds.feige.im.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ds.feige.im.chat.dto.ChatMessage;
import com.ds.feige.im.chat.entity.ConversationMessage;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Collection;
import java.util.List;

/**
 * @author DC
 */
public interface ConversationMessageMapper extends BaseMapper<ConversationMessage> {
    @Select("SELECT sender_id,conversation_id,sender_id,send_seq_id,msg_id,msg_content,msg_type,create_time FROM t_conversation_message WHERE conversation_id=#{conversationId}" +
            " AND msg_id<#{maxMsgId} ORDER BY msg_id ASC LIMIT #{pageSize} ")
    List<ChatMessage> selectMessages(long userId, long conversationId, long maxMsgId, long pageSize);

    @Select("SELECT conversation_id,sender_id,send_seq_id,msg_id,msg_content,msg_type,create_time FROM t_conversation_message where msg_id=#{msgId}")
    ChatMessage getMsgById(long msgId);

    @Select({"<script> ",
            "SELECT conversation_id,sender_id,send_seq_id,msg_id,msg_content,msg_type,create_time FROM t_conversation_message WHERE msg_id IN",
            "<foreach item='item' index='index' collection='msgIds' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            "</script>"
    })
    List<ChatMessage> findByMsgIds(@Param("msgIds") Collection<Long> msgIds);
}
