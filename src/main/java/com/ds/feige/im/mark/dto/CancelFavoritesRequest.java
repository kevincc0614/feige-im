package com.ds.feige.im.mark.dto;

import javax.validation.constraints.Positive;

import com.ds.feige.im.account.dto.UserRequest;

import lombok.Data;

/**
 * @author DC
 */
@Data
public class CancelFavoritesRequest extends UserRequest {
    @Positive
    private long markId;
}
