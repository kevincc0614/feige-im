package com.ds.feige.im.chat.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ds.feige.im.event.entity.UserEvent;

/**
 * @author DC
 */
public interface UserEventMapper extends BaseMapper<UserEvent> {

    @Select("SELECT * FROM t_user_event where user_id=#{userId} and seq_id>#{startSeqId} limit ${size}")
    List<UserEvent> findEventsByStartSeqId(long userId, long startSeqId, int size);

    @Select("SELECT MAX(seq_id) FROM t_user_event WHERE user_id=#{userId}")
    Long getMaxUserSeqId(long userId);
}
