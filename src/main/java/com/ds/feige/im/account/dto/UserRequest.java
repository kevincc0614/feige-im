package com.ds.feige.im.account.dto;

import lombok.Data;

import javax.validation.constraints.Positive;

/**
 * @author caedmon
 */
@Data
public class UserRequest {
    @Positive
    protected long userId;
}
