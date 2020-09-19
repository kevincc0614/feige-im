package com.ds.feige.im.mark.dto;

import com.ds.feige.im.account.dto.UserRequest;

import lombok.Data;

/**
 * @author DC
 */
@Data
public class MarkRequest extends UserRequest {
    private int markType;
    private long msgId;
    private String title;
    private String remark;
}
