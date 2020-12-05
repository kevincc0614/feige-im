package com.ds.feige.im.account.dto;

import javax.validation.constraints.Positive;

import lombok.Data;

/**
 * @author caedmon
 */
@Data
public class UserRequest {
    @Positive
    protected long userId;
}
