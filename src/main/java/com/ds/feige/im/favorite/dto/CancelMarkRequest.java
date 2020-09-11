package com.ds.feige.im.favorite.dto;

import com.ds.feige.im.account.dto.UserRequest;
import lombok.Data;

import javax.validation.constraints.Positive;

/**
 * @author DC
 */
@Data
public class CancelMarkRequest extends UserRequest {
    @Positive
    private long markId;
}
