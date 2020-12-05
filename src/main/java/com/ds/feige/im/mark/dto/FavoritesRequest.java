package com.ds.feige.im.mark.dto;

import com.ds.feige.im.account.dto.UserRequest;

import lombok.Data;

/**
 * @author DC
 */
@Data
public class FavoritesRequest extends UserRequest {
    private long msgId;
}
