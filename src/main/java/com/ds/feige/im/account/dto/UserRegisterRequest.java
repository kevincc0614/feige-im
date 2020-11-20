package com.ds.feige.im.account.dto;

import javax.validation.constraints.Size;

import lombok.Data;

/**
 * @author DC
 */
@Data
public class UserRegisterRequest {

    private String loginName;
    @Size(min = 6, max = 16)
    private String password;
    private String source;
    private String nickName;
}
