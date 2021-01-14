package com.ds.feige.im.account.dto;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

/**
 * @author DC
 */
@Data
public class LogoutRequest extends UserRequest {
    private String deviceId;
    private Map<String, Object> properties;

    public void addProperty(String key, Object value) {
        if (properties == null) {
            this.properties = new HashMap<>();
        }
        this.properties.put(key, value);
    }
}
