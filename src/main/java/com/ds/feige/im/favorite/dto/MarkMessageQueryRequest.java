package com.ds.feige.im.favorite.dto;

import com.ds.feige.im.account.dto.UserRequest;
import lombok.Data;

import java.util.Date;

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
