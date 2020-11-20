package com.ds.feige.im.enterprise.dto;

import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

import lombok.Data;

/**
 * @author caedmon
 */
@Data
public class AddEmpRequest extends EnterpriseOpRequest {
    @Positive
    private Long userId;
    private String name;
    private String employeeNo;
    @NotBlank
    private String title;
    private String workEmail;
    private String role;
    private Map<Long, Boolean> departments;
}
