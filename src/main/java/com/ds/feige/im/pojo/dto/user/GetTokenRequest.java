package com.ds.feige.im.pojo.dto.user;


import javax.validation.constraints.NotBlank;

/**
 * @author caedmon
 */
public class GetTokenRequest {
    @NotBlank(message = "登录名不能为空")
    private String loginName;
    @NotBlank(message = "密码不能为空")
    private String password;

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserLoginRequest{" +
                "loginName='" + loginName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
