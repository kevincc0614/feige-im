package com.ds.feige.im.mark.dto;

import java.util.Date;

import com.ds.feige.im.account.dto.UserRequest;

import lombok.Data;

/**
 * @author DC
 */
@Data
public class MarkMessageQueryRequest extends UserRequest {
    private Long conversationId;
    private Date start;
    private Date end;
    private Integer markType;
}
