package com.ds.feige.im.mark.dto;

import com.ds.feige.im.account.dto.UserRequest;

import lombok.Data;

/**
 * @author DC
 */
@Data
public class UpdateRemarkRequest extends UserRequest {
    private long markId;
    private String title;
    private String remark;
}
