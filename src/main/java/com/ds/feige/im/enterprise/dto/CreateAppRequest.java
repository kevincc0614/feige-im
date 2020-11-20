package com.ds.feige.im.enterprise.dto;

import javax.validation.constraints.NotBlank;

import lombok.Data;

/**
 * @author DC
 */
@Data
public class CreateAppRequest extends EnterpriseOpRequest {
    @NotBlank
    private String name;
    private String avatar;
    private String config;
}
