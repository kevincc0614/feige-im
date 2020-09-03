package com.ds.feige.im.gateway.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ds.feige.im.gateway.entity.UserDevice;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * UserDeviceMapper
 *
 * @author DC
 */
public interface UserDeviceMapper extends BaseMapper<UserDevice> {
    @Select("SELECT * FROM t_user_device where user_id=#{userId} and device_id=#{deviceId}")
    UserDevice getByUserIdAndDeviceId(long userId, String deviceId);


    @Select("SELECT * FROM t_user_device where user_id=#{userId} AND status=#{status}")
    List<UserDevice> getDevicesByUserId(long userId, int status);
}
