package com.ds.feige.im.enterprise.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

import lombok.Data;

/**
 * @author caedmon
 */
@Data
public class CreateEmpRequest extends EnterpriseOpRequest {
    @Positive
    private Long userId;
    private String name;
    private String employeeNo;
    @NotBlank
    private String title;
    private String workEmail;
    private String role;
}
