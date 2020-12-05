package com.ds.feige.im.mark.dto;

import com.ds.feige.im.account.dto.UserRequest;

import lombok.Data;

/**
 * @author DC
 */
@Data
public class CancelRemarkRequest extends UserRequest {
    private long markId;
}
