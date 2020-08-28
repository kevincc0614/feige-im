package com.ds.feige.im.pojo.dto.user;

import javax.validation.constraints.Positive;

/**
 * @author caedmon
 */
public class UserRequest {
    @Positive
    protected long userId;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
