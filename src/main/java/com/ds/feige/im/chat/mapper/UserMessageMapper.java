package com.ds.feige.im.chat.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ds.feige.im.chat.entity.UserMessage;
import com.ds.feige.im.chat.po.SenderAndMsg;
import com.ds.feige.im.chat.po.UnreadMessagePreview;

public interface UserMessageMapper extends BaseMapper<UserMessage> {

    @Select("SELECT * FROM t_user_message WHERE msg_id=#{msgId}")
    UserMessage getByMsgId(long msgId);

    /**
     * @param userId
     * @param minUnAckMsgId
     */
    @Select("SELECT COUNT(m.msg_id)  unread_count,MAX(m.msg_id) last_msg_id,m.conversation_id conversation_id,c.conversation_name conversation_name "
        + "FROM t_user_message m RIGHT JOIN t_user_conversation c ON m.conversation_id=c.conversation_id AND m.user_id=c.user_id "
        + "WHERE c.user_id=#{userId} and m.msg_type<20 and m.msg_id>=#{minUnAckMsgId} and m.state<2 GROUP BY conversation_id,conversation_name")
    List<UnreadMessagePreview> getUnreadMessagePreview(long userId, long minUnAckMsgId);

    @Select("SELECT MIN(msg_id) FROM t_user_message WHERE user_id=#{userId} AND state=0")
    Long getMinUnAckMsgId(long userId);

    @Update({"<script>", "UPDATE t_user_message SET state=#{state} WHERE user_id=#{userId} AND msg_id IN ",
        "<foreach item='item' index='index' collection='msgIds' open='(' separator=',' close=')'>", "#{item}",
        "</foreach></script>"})
    int batchUpdateState(long userId, @Param("msgIds") List<Long> msgIds, int state);

    @Select({"<script>", "SELECT sender_id,msg_id FROM t_user_message WHERE user_id=#{userId}  msg_id IN ",
        "<foreach item='item' index='index' collection='msgIds' open='(' separator=',' close=')'>", "#{item}",
        "</foreach></script>"})
    List<SenderAndMsg> findSenderAndMsgList(long userId, @Param("msgIds") List<Long> msgIds);

}
