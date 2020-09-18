package com.ds.feige.im.constants;

public class MsgType {
    /**
     * 聊天基本类型,小于20的都计入未读数
     */
    public static final int TEXT = 0, PIC = 1, AUDIO = 2, VIDEO = 3, FILE = 4;
    /**
     * 会话通知,不计入未读数
     **/
    public static final int NOTICE = 20;
    /**
     * 已读回执,不计入未读数
     */
    public static final int READ_RECEIPT = 30;
    /**
     * 自定义
     */
    public static final int CUSTOM = 100;
}
