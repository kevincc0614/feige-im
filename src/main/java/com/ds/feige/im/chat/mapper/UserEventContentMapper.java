package com.ds.feige.im.chat.mapper;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ds.feige.im.chat.entity.UserEventContent;

/**
 * @author DC
 */
public interface UserEventContentMapper extends BaseMapper<UserEventContent> {

    @Select({"<script> ", "SELECT * FROM t_event_content  WHERE id IN ",
        "<foreach item='item' index='index' collection='ids' open='(' separator=',' close=')'>", "#{item}",
        "</foreach>", "</script>"})
    List<UserEventContent> findContentsByIds(@Param("ids") Set<Long> ids);
}
