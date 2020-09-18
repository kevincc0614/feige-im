package com.ds.feige.im.gateway.socket.connection;

import java.util.Date;

import com.ds.feige.im.constants.DeviceType;

import lombok.Data;

/**
 * Socket链接元数据,通过元数据可以构造本地链接或者远程链接对象
 *
 * @author DC
 */
@Data
public class ConnectionMeta {
    private long userId;
    /**
     * 链接所在服务器ID
     */
    private String instanceId;
    /**
     * 设备ID
     */
    private String deviceId;
    /**
     * IP地址
     */
    private String ipAddress;
    /**
     * 链接类型 移动端 PC端 web端
     */
    private DeviceType deviceType;
    /**
     * WebSocketSession.id
     */
    private String sessionId;

    private Date lastActiveTime;
}
