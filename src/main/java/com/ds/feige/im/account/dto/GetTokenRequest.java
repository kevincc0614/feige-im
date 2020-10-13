package com.ds.feige.im.account.dto;


import javax.validation.constraints.NotBlank;

import lombok.Data;

/**
 * @author DC
 */
@Data
public class GetTokenRequest {
    @NotBlank(message = "登录名不能为空")
    private String loginName;
    @NotBlank(message = "密码不能为空")
    private String password;
}
