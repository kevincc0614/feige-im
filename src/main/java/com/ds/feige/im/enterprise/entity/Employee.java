package com.ds.feige.im.enterprise.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ds.feige.im.common.entity.BaseEntity;
import lombok.Data;

@TableName("t_employee")
@Data
public class Employee extends BaseEntity {
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 公司ID
     */
    private Long enterpriseId;
    /**
     * 姓名
     */
    private String name;

    private String avatar;
    /**
     * 工号
     */
    private String employeeNo;
    /**
     * 职位
     */
    private String title;
    /**
     * 工作邮箱
     */
    private String workEmail;
    /**
     * 角色
     */
    private String role;
}
