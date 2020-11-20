package com.ds.feige.im.account.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ds.feige.im.common.entity.BaseEntity;

import lombok.Data;

@TableName("t_user")
@Data
public class User extends BaseEntity {
    private String loginName;
    private String password;
    private String nickName;
    private String avatar;
    private String source;
    private String mobile;
    private String email;
    private Integer gender;
    /** 账户状态 */
    private Integer state;
    /** 加密盐 */
    private String salt;
}
