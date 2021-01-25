package com.ds.feige.im.enterprise.po;

import com.ds.feige.im.common.domain.UserIdHolder;

import lombok.Data;

/**
 * 员工部门信息
 * 
 * @author DC
 */
@Data
public class EmpDepBindingPo implements UserIdHolder {
    private Long userId;
    private Long enterpriseId;
    private Long departmentId;
    private Boolean leader;
    private String departmentName;
}
