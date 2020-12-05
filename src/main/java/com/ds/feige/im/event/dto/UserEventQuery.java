package com.ds.feige.im.event.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;

import com.ds.feige.im.account.dto.UserRequest;

import lombok.Data;

/**
 * @author DC
 */
@Data
public class UserEventQuery extends UserRequest {
    @PositiveOrZero
    private Long startSeqId;
    @Max(300)
    @Min(1)
    private Integer size = 300;
}
