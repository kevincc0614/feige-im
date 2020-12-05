package com.ds.feige.im.mark.mapper;

import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ds.feige.im.mark.entity.MarkMessage;

/**
 * @author DC
 */
public interface MarkMessageMapper extends BaseMapper<MarkMessage> {
    @Select("SELECT * FROM t_mark_message WHERE user_id=#{userId} AND msg_id=#{msgId}")
    MarkMessage getByUserAndMsgId(long userId, long msgId);
}
