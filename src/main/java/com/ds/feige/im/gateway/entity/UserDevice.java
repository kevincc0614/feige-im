package com.ds.feige.im.gateway.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ds.feige.im.common.entity.BaseEntity;

import lombok.Data;

/**
 * 用户设备信息
 *
 * @author DC
 */
@TableName("t_user_device")
@Data
public class UserDevice extends BaseEntity {
    private Long userId;
    private String deviceId;
    /**
     * 设备类型 @see DeviceType
     */
    private String deviceType;
    private String deviceName;
    /**
     * 推送token
     */
    private String pushToken;
    /**
     * 设备状态
     */
    private Integer status;
    private Date lastLoginTime;
}
