package com.ds.feige.im.gateway.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ds.feige.im.gateway.entity.UserDevice;

/**
 * UserDeviceMapper
 *
 * @author DC
 */
public interface UserDeviceMapper extends BaseMapper<UserDevice> {
    @Select("SELECT * FROM t_user_device where user_id=#{userId} and device_id=#{deviceId}")
    UserDevice getByUserIdAndDeviceId(long userId, String deviceId);

    @Select("SELECT * FROM t_user_device where user_id=#{userId} AND status=#{status} AND ISNULL(device_token)=0 and LENGTH(trim(device_token))>0")
    List<UserDevice> getUserPushableDevices(long userId, int status);

    @Update("UPDATE t_user_device SET event_checkpoint=#{seqId} where user_id=#{userId} and  device_id=#{deviceId} AND (event_checkpoint<#{seqId} OR event_checkpoint IS NULL)")
    int updateCheckpoint(long userId, String deviceId, long seqId);

    @Select("SELECT event_checkpoint FROM t_user_device WHERE user_id#{userId} AND device_id=#{deviceId} limit 1")
    Long getDeviceCheckpoint(long userId, String deviceId);

    @Select("SELECT MAX(event_checkpoint) FROM t_user_device WHERE user_id=#{userId}")
    Long getMaxDeviceCheckpoint(long userId);

    @Update("UPDATE t_user_device SET status=#{newStatus} WHERE user_id=#{userId} and device_id=#{deviceId} and status=#{oldStatus}")
    int updateDeviceStatus(long userId, String deviceId, int oldStatus, int newStatus);
}
