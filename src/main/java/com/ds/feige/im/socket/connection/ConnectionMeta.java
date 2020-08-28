package com.ds.feige.im.socket.connection;
/**
 * Socket链接元数据,通过元数据可以构造本地链接或者远程链接对象
 * @author DC
 * */
public class ConnectionMeta {
    private long userId;
    /**链接所在服务器ID*/
    private String instanceId;
    /**设备ID*/
    private String deviceId;
    /**IP地址*/
    private String ipAddress;
    /**链接类型 移动端 PC端  web端*/
    private int deviceType;
    /**WebSocketSession.id*/
    private String sessionId;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String toString() {
        return "ConnectionMeta{" +
                "instanceId='" + instanceId + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", deviceType=" + deviceType +
                ", sessionId='" + sessionId + '\'' +
                '}';
    }
}
