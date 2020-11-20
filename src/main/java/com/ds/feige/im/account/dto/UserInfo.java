package com.ds.feige.im.account.dto;

import lombok.Data;

@Data
public class UserInfo {
    private long userId;
    private String loginName;
    private String nickName;
    private String avatar;
    private String source;
    private String mobile;
    private String email;
    private Integer gender;
    private String password;
}
