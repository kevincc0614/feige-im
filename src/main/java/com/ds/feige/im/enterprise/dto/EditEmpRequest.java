package com.ds.feige.im.enterprise.dto;

import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

import lombok.Data;

/**
 * @author DC
 */
@Data
public class EditEmpRequest extends EnterpriseOpRequest {
    /** 用户ID */
    private long userId;
    /** 登录账号名 */
    private String loginName;
    private String password;
    @NotBlank
    /** 员工姓名 */
    private String name;
    /** 工号 */
    @NotBlank
    private String employeeNo;
    /** 职位 */
    private String title;
    /** 工作邮箱 */
    private String workEmail;
    /** 角色ID集合 */
    private Set<Long> roles;
    /** 部门<部门ID,是否领导> */
    @Positive
    private Long departmentId;
    private Boolean leader;
}
