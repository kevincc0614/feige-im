package com.ds.feige.im.app.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ds.feige.im.app.entity.App;

/**
 * @author DC
 */
public interface AppMapper extends BaseMapper<App> {
    @Select("SELECT id,enterprise_id,secret,name,avatar,config,create_time,update_time FROM t_app WHERE enterprise_id=#{enterpriseId} AND secret=#{secret} limit 1")
    App getByEntAndSecret(long enterpriseId, String secret);

    @Select("SELECT id,enterprise_id,secret,name,avatar,config,create_time,update_time FROM t_app WHERE enterprise_id=#{enterpriseId}")
    List<App> findByEntId(long enterpriseId);

    @Select("SELECT id,enterprise_id,secret,name,avatar,config,create_time,update_time FROM t_app WHERE enterprise_id=#{enterpriseId} AND name=#{name} limit 1")
    App getByEntAndName(long enterpriseId, String name);
}
