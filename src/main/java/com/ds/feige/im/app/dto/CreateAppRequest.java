package com.ds.feige.im.app.dto;

import javax.validation.constraints.NotBlank;

import com.ds.feige.im.enterprise.dto.EnterpriseOpRequest;

import lombok.Data;

/**
 * @author DC
 */
@Data
public class CreateAppRequest extends EnterpriseOpRequest {
    @NotBlank
    private String name;
    private String avatar;
}
