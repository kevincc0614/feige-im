package com.ds.feige.im.chat.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ds.feige.im.common.entity.BaseEntity;

import lombok.Data;

/**
 * @author DC
 */
@TableName("t_group_user")
@Data
public class GroupUser extends BaseEntity {
    private Long groupId;
    private Long userId;
    /** 群内昵称 */
    private String userName;
    /** 用户角色 管理员，普通群员等 */
    private String role;
    /** 进群方式 */
    private String joinType;
    /** 加入时间 */
    private Date joinTime;
    /** 邀请人 */
    private Long inviteUserId;
}
