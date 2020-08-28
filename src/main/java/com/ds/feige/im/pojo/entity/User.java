package com.ds.feige.im.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;

@TableName("t_user")
public class User extends BaseEntity {
    /**用户对外的账户ID*/
    private String accountId;
    private String password;
    private String nickName;
    private String avatar;
    /**用户来源渠道*/
    private String source;
    private String mobile;
    /**邮箱*/
    private String email;
    /**性别 0 女 1 男*/
    private Integer gender;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }
}
