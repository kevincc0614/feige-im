package com.ds.feige.im.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ds.feige.im.chat.dto.MessageToUser;
import com.ds.feige.im.chat.entity.ConversationMessage;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author DC
 */
public interface ConversationMessageMapper extends BaseMapper<ConversationMessage> {
    String SELECT_COLUMNS = "SELECT conversation_id,sender_id,msg_id,msg_content,msg_type,read_count,receiver_count,create_time FROM t_conversation_message ";

    @Select({SELECT_COLUMNS, "  where msg_id=#{msgId}"
    })
    MessageToUser getMessageById(long msgId);

    @Select({
            SELECT_COLUMNS, " WHERE conversation_id=#{conversationId}" +
            " AND msg_id<#{maxMsgId} ORDER BY msg_id ASC LIMIT #{pageSize} "
    })
    List<MessageToUser> findMessages(long userId, long conversationId, long maxMsgId, long pageSize);

    @Select({
            SELECT_COLUMNS, " WHERE conversation_id=#{conversationId}" +
            " AND update_time<=#{updateTime} ORDER BY msg_id ASC LIMIT #{pageSize} "
    })
    List<MessageToUser> findMessages(long userId, long conversationId, Date updateTime, long pageSize);


    @Select({"<script> ",
            SELECT_COLUMNS,
            " WHERE msg_id IN <foreach item='item' index='index' collection='msgIds' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            "</script>"
    })
    List<MessageToUser> findMessagesByIds(@Param("msgIds") Collection<Long> msgIds);

    @Select({"<script> ",
            "SELECT MAX(msg_id) last_msg_id,conversation_id FROM t_conversation_message WHERE conversation_id IN ",
            "<foreach item='item' index='index' collection='conversationIds' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            " GROUP BY conversation_id",
            "</script>"
    })
    List<Long> findLastMessagesByConversations(@Param("conversationIds") Collection<Long> conversationIds);

    @Update({"<script> ",
            "UPDATE t_conversation_message SET read_count=read_count+1 WHERE msg_id IN ",
            "<foreach item='item' index='index' collection='msgIds' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            "</script>"
    })
    int readMessages(@Param("msgIds") List<Long> msgIds);

    @Select({"<script> ",
            SELECT_COLUMNS,
            " WHERE sender_id=#{senderId} AND msg_id IN ",
            "<foreach item='item' index='index' collection='msgIds' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            "</script>"
    })
    List<MessageToUser> findUsersMessages(long senderId, @Param("msgIds") List<Long> msgIds);
}
