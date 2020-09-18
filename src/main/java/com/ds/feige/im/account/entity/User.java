package com.ds.feige.im.account.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ds.feige.im.common.entity.BaseEntity;
import lombok.Data;

@TableName("t_user")
@Data
public class User extends BaseEntity {
    /**
     * 用户对外的账户ID
     */
    private String accountId;
    private String password;
    private String nickName;
    private String avatar;
    /**
     * 用户来源渠道
     */
    private String source;
    private String mobile;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 性别 0 女 1 男
     */
    private Integer gender;
    /**
     * 账户状态
     */
    private Integer state;
    private String salt;
}
