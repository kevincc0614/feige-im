package com.ds.feige.im.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ds.feige.im.chat.entity.Group;
import org.apache.ibatis.annotations.Update;

/**
 * @author DC
 */
public interface GroupMapper extends BaseMapper<Group> {


    @Update("UPDATE t_group SET  conversation_id=#{conversationId} WHERE id=#{groupId}")
    int conversationCreated(long groupId, long conversationId);
}
