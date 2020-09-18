package com.ds.feige.im.enterprise.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;

import lombok.Data;

/**
 * @author caedmon
 */
@Data
public class CreateDepRequest extends EnterpriseOpRequest {
    @PositiveOrZero(message = "部门ID必须大于等于0")
    private long parentId;
    @NotBlank(message = "部门名称不能为空")
    private String departmentName;
    private String departmentEnName;
    private int priority;

}
