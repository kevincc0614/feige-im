package com.ds.feige.im.constants;

import com.ds.base.nodepencies.exception.WarnMessageEntry;

public enum  FeigeWarn implements WarnMessageEntry {
    SYSTEM_ERROR("SYS_500","系统未知异常","系统未知异常"),
    REQUEST_VALIDATE_ERROR("SYS_400","参数校验错误:[{1}]","参数校验错误:[{1}]"),
    PATH_NOT_FOUND("SYS_404","PATH不合法","PATH不合法"),

    NAME_OR_PWD_ERROR("User_10001","用户名或密码错误","用户名或密码错误"),
    USER_NOT_EXISTS("User_10002","用户不存在","用户不存在"),
    TOKEN_VERIFY_ERROR("User_10003","签名校验异常","签名校验异常"),
    USER_LOCK_TIMEOUT("User_100004","请求超时","锁定用户资源超时"),
    ACCOUNT_REGISTERD("User_100005","账号已经被注册过","账号已经被注册过"),
    USER_LOCK_INTERRUPT("User_100006","请求超时","锁定用户资源时线程中断"),
    TOKEN_EXPIRED("User_100007","Token已过期","Token已过期"),

    GROUP_NOT_EXISTS("Group_100001","群聊不存在","群聊不存在"),
    GROUP_USER_OVER_LIMIT("Group_100002","已超过最大人数限制","已超过最大人数限制"),
    GROUP_PERMISSION_NOT_ALLOWED("Group_100003","没有操作权限","没有操作权限"),
    GROUP_USER_NOT_EXISTS("Group_100004","用户不在群组{1}","用户不在群组{1}"),

    PARENT_DEPARTMENT_NOT_EXISTS("Department_100001","上级部门不存在","上级部门不存在"),
    DEPARTMENT_NAME_EXISTS("Department_100002","部门名不能重复","部门名不能重复"),
    EMPLOYEE_EXISTS_IN_DEPARTMENT("Department_100003","员工已经在部门内","员工已经在部门内"),
    DEPARTMENT_NOT_EXISTS("Department_100004","部门不存在","部门不存在"),

    EMPLOYEE_NOT_EXISTS("Employee_100001","员工不存在","员工不存在"),
    EMPLOYEE_IS_EXISTS("Employee_100002","员工已存在","员工已存在"),

    CHAT_MSG_ID_EXISTS("CHAT_100001","消息ID已存在","消息ID已存在");
    private String code;
    private String originalMessage;
    private String displayMessage;
    FeigeWarn(String code,String originalMessage,String displayMessage){
        this.code=code;
        this.originalMessage=originalMessage;
        this.displayMessage=displayMessage;
    }
    FeigeWarn(String code,String displayMessage){
        this.code=code;
        this.originalMessage=displayMessage;
        this.displayMessage=displayMessage;
    }
    @Override
    public String code() {
        return this.code;
    }

    @Override
    public String originalMessage() {
        return this.originalMessage;
    }

    @Override
    public String displayMessage() {
        return this.displayMessage;
    }
}
