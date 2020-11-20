package com.ds.feige.im.chat.mapper;

import java.util.Collection;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ds.feige.im.chat.dto.MessageToUser;
import com.ds.feige.im.chat.entity.ConversationMessage;

/**
 * @author DC
 */
public interface ConversationMessageMapper extends BaseMapper<ConversationMessage> {
    String SELECT_COLUMNS =
        "SELECT conversation_id,sender_id,msg_id,msg_content,msg_type,conversation_type,read_count,receiver_count,create_time FROM t_conversation_message ";

    @Select({SELECT_COLUMNS, "  WHERE msg_id=#{msgId}"})
    MessageToUser getMessageById(long msgId);

    @Select({SELECT_COLUMNS,
        " WHERE conversation_id=#{conversationId}" + " AND msg_id<=#{maxMsgId} ORDER BY msg_id ASC LIMIT #{pageSize} "})
    List<MessageToUser> findMessages(long userId, long conversationId, long maxMsgId, long pageSize);

    @Select({"<script> ", SELECT_COLUMNS,
        " WHERE msg_id IN <foreach item='item' index='index' collection='msgIds' open='(' separator=',' close=')'>",
        "#{item}", "</foreach>", "</script>"})
    List<MessageToUser> findMessagesByIds(@Param("msgIds") Collection<Long> msgIds);

    @Update({"<script> ", "UPDATE t_conversation_message SET read_count=read_count+1 WHERE msg_id IN ",
        "<foreach item='item' index='index' collection='msgIds' open='(' separator=',' close=')'>", "#{item}",
        "</foreach>", "</script>"})
    int readMessages(@Param("msgIds") List<Long> msgIds);

    @Select({"<script> ", SELECT_COLUMNS, " WHERE sender_id=#{senderId} AND msg_id IN ",
        "<foreach item='item' index='index' collection='msgIds' open='(' separator=',' close=')'>", "#{item}",
        "</foreach>", "</script>"})
    List<MessageToUser> findUsersMessages(long senderId, @Param("msgIds") List<Long> msgIds);
}
