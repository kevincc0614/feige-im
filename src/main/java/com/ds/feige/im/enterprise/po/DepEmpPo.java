package com.ds.feige.im.enterprise.po;

import com.ds.feige.im.common.domain.UserIdHolder;

import lombok.Data;

/**
 * 部门员工信息
 * 
 * @author DC
 */
@Data
public class DepEmpPo implements UserIdHolder {
    /** 用户ID **/
    private Long userId;
    /** 公司ID */
    private Long enterpriseId;
    /** 姓名 */
    private String name;
    private String avatar;
    /** 工号 */
    private String employeeNo;
    /** 职位 */
    private String title;
    /** 工作邮箱 */
    private String workEmail;
    /** 角色 */
    private String role;
    private Long departmentId;
    private String departmentName;
    private Boolean leader;
}
