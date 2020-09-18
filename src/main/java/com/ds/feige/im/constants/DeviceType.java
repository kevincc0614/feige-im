package com.ds.feige.im.constants;

/**
 * @author DC
 */

public enum DeviceType {

    IOS_MOBILE(DeviceType.MOBILE_TYPE, "IOS"), ANDROID_MOBILE(DeviceType.MOBILE_TYPE, "ANDROID"),
    MAC_PC(DeviceType.PC_TYPE, "MAC"), WINDOWS_PC(DeviceType.PC_TYPE, "Windows"), WEB(DeviceType.WEB_TYPE, "WEB");

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
