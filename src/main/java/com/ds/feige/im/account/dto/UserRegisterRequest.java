package com.ds.feige.im.account.dto;

import javax.validation.constraints.Size;

/**
 * @author DC
 */
public class UserRegisterRequest {

    private String mobile;
    @Size(min = 6, max = 16)
    private String password;
    private String source;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
