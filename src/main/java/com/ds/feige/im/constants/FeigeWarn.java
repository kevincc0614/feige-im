package com.ds.feige.im.constants;

import com.ds.base.nodepencies.exception.WarnMessageEntry;

public enum FeigeWarn implements WarnMessageEntry {
    SYSTEM_ERROR("SYS_500", "系统未知异常", "系统未知异常"),
    REQUEST_VALIDATE_ERROR("SYS_400", "参数校验错误:[{1}]", "参数校验错误:[{1}]"),
    PATH_NOT_FOUND("SYS_404", "PATH不合法", "PATH不合法"),
    PERMISSION_DIED("SYS_403", "没有权限", "没有权限"),
    SYSTEM_BUSY("SYS_503", "系统忙,请稍后再试", "锁定失败或超时"),

    PWD_ERROR("User_10001", "密码错误", "密码错误"),
    USER_NOT_EXISTS("User_10002", "用户不存在", "用户不存在"),
    TOKEN_VERIFY_ERROR("User_10003", "签名校验异常", "签名校验异常"),
    USER_LOCK_TIMEOUT("User_100004", "请求超时", "锁定用户资源超时"),
    ACCOUNT_REGISTERD("User_100005", "账号已经被注册过", "账号已经被注册过"),
    USER_LOCK_INTERRUPT("User_100006", "请求超时", "锁定用户资源时线程中断"),
    TOKEN_EXPIRED("User_100007", "Token已过期", "Token已过期"),
    DEVICE_NOT_EXISTS("User_100008", "设备不存在", "设备不存在{1}"),
    LOGIN_NAME_IS_EMPTY("user_100009", "登录名不能为空", "登录名不能为空"), PASSWORD_INSECURE("User_100010", "密码不安全", "密码不安全"),
    LOGIN_NAME_EXISTS("User_100011", "用户名已存在", "用户名已存在"),

    GROUP_NOT_EXISTS("Group_100001", "群聊不存在{1}", "群聊不存在{1}"),
    GROUP_USER_OVER_LIMIT("Group_100002", "已超过最大人数限制", "已超过最大人数限制"),
    GROUP_PERMISSION_NOT_ALLOWED("Group_100003", "没有操作权限", "没有操作权限"),
    GROUP_USER_NOT_EXISTS("Group_100004", "用户不在群组{1}", "用户不在群组{1}"),

    PARENT_DEPARTMENT_NOT_EXISTS("Department_100001", "上级部门不存在", "上级部门不存在"),
    DEPARTMENT_NAME_EXISTS("Department_100002", "部门名不能重复", "部门名不能重复"),
    EMPLOYEE_EXISTS_IN_DEPARTMENT("Department_100003", "员工已经在部门内", "员工已经在部门内"),
    DEPARTMENT_NOT_EXISTS("Department_100004", "部门不存在", "部门不存在"),
    HAS_CHILD_DEPS_CAN_NOT_DELETE("Department_100005", "存在子部门,不能直接删除", "存在子部门,不能直接删除"),

    EMPLOYEE_NOT_EXISTS("Employee_100001", "员工不存在", "员工不存在"),
    EMPLOYEE_IS_EXISTS("Employee_100002", "员工已存在", "员工已存在"),
    EMPLOYEE_ROLE_NOT_ADMIN("Employee_100003", "只有管理员才能进行操作", "只有管理员才能进行操作"),
    CAN_NOT_DELETE_SELF("Employee_100004", "不能删除自己", "不能删除自己"),
    CAN_NOT_DELETE_SUPER_ADMIN("Employee_100005", "不能删除超级管理员", "不能删除超级管理员"),

    CHAT_MSG_ID_EXISTS("CHAT_100001", "消息ID已存在", "消息ID已存在"),
    CONVERSATION_NOT_EXISTS("CHAT_100002", "会话不存在", "会话不存在"),
    CONVERSATION_HAS_BEEN_CREATED("CHAT_100003", "会话已经被创建", "会话已经被创建"),
    CHAT_MSG_NOT_EXISTS("CHAT_100004", "消息不存在", "消息不存在"),

    APP_NOT_EXISTS("APP_100001", "应用不存在", "应用不存在"), APP_NAME_EXISTS("APP_100002", "应用名已存在", "应用名已存在"),
    APP_SECRET_INVALID("APP_100004", "不合法的secret参数", "不合法的secret参数"),
    USER_OPENID_INVALID("APP_100007", "OpenId不合法", "OpenId不合法"),
    APP_TOKEN_EXPIRED("APP_100008", "AccessToken已过期", "AccessToken已过期"),

    ROLE_NOT_EXISTS("SECURITY_100003", "应用角色不存在", "应用角色不存在"),
    ROLE_AUTHORITY_EXISTS("SECURITY_100005", "角色权限已存在", "角色权限已存在"),
    ROLE_AUTHORITY_NAME_EXISTS("SECURITY_100006", "权限名已存在", "权限名已存在"),
    AUTHORITY_IS_EMPTY("SECURITY_100006", "权限为空", "权限为空"),

    MARK_MESSAGE_NOT_EXISTS("MARK_100001", "标记消息不存在", "标记消息不存在"),
    MESSAGE_HAS_MARKED("MARK_100002", "消息已被标记过", "消息已被标记过");
    private String code;
    private String originalMessage;
    private String displayMessage;

    FeigeWarn(String code, String originalMessage, String displayMessage) {
        this.code = code;
        this.originalMessage = originalMessage;
        this.displayMessage = displayMessage;
    }

    FeigeWarn(String code, String displayMessage) {
        this.code = code;
        this.originalMessage = displayMessage;
        this.displayMessage = displayMessage;
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
