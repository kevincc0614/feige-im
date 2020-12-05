package com.ds.feige.im.event;

import com.ds.feige.im.event.dto.UserEventData;

/**
 * @author DC
 */
public interface UserEventCallback {

    void onSuccess(UserEventData data);

    void onFailure(UserEventData data, Throwable throwable);
}
