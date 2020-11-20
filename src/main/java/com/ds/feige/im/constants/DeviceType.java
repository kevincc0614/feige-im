package com.ds.feige.im.constants;

/**
 * @author DC
 */

public enum DeviceType {

    IOS(DeviceType.MOBILE_TYPE, "ios"), ANDROID(DeviceType.MOBILE_TYPE, "android"), MACOS(DeviceType.PC_TYPE, "mac"),
    WINDOWS(DeviceType.PC_TYPE, "windows"), WEB(DeviceType.WEB_TYPE, "web");

    public static final int MOBILE_TYPE = 1, PC_TYPE = 2, WEB_TYPE = 3;
    /**
     * 1 移动端 2 PC端 3 Web端
     */
    public int type;
    /**
     * 系统平台 IOS ANDROID MAC WINDOWS WEB 等
     */
    public String platform;

    DeviceType(int type, String platform) {
        this.type = type;
        this.platform = platform;
    }
}
