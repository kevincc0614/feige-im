package com.ds.feige.im.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ds.feige.im.chat.entity.UserMessage;
import com.ds.feige.im.chat.po.SenderAndMsg;
import com.ds.feige.im.chat.po.UnreadMessagePreview;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface UserMessageMapper extends BaseMapper<UserMessage> {

    @Select("SELECT * FROM t_user_message WHERE msg_id=#{msgId}")
    UserMessage getByMsgId(long msgId);

    /**
     * @param userId
     * @param minUnAckMsgId
     */
    @Select("SELECT COUNT(msg_id)  unread_count,MAX(msg_id) last_msg_id,conversation_id conversation_id " +
            "FROM t_user_message WHERE user_id=#{userId} and msg_type!=10 and msg_id>#{minUnAckMsgId} and state<2 GROUP BY conversation_id")
    List<UnreadMessagePreview> getConversationUnreadPreview(long userId, long minUnAckMsgId);

    @Select("SELECT MIN(msg_id) FROM t_user_message WHERE user_id=#{userId} AND state=0")
    Long getMinUnAckMsgId(long userId);

    @Update({"<script>",
            "UPDATE t_user_message SET state=#{state} WHERE user_id=#{userId} AND msg_id IN ",
            "<foreach item='item' index='index' collection='msgIds' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach></script>"})
    int batchUpdateState(long userId, @Param("msgIds") List<Long> msgIds, int state);

    @Select({"<script>",
            "SELECT sender_id,msg_id FROM t_user_message WHERE user_id=#{userId}  msg_id IN ",
            "<foreach item='item' index='index' collection='msgIds' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach></script>"})
    List<SenderAndMsg> findSenderAndMsgList(long userId, @Param("msgIds") List<Long> msgIds);

}
