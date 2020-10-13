package com.ds.feige.im.app.dto;

import javax.validation.constraints.Positive;

import lombok.Data;

/**
 * @author DC
 */
@Data
public class EnterpriseAppRequest {
    @Positive
    protected long enterpriseId;
    @Positive
    protected long appId;
}
