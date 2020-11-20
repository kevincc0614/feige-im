package com.ds.feige.im.enterprise.dto;

import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

import lombok.Data;

/**
 * @author DC
 */
@Data
public class CreateEmpRequest extends EnterpriseOpRequest {
    /** 员工姓名 */
    @NotBlank
    private String name;
    @NotBlank
    private String loginName;
    @NotBlank
    private String password;
    private String avatar;
    private String employeeNo;
    @NotBlank
    private String title;
    private String workEmail;
    @Positive(message = "部门ID不合法")
    private Long departmentId;
    private Boolean leader;
    private Set<Long> roles;
}
