package com.ds.feige.im.push.dto;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

/**
 * 推送消息
 *
 * @author DC
 */
@Data
public class PushMessage {
    private long userId;
    private String pushId;
    private String title;
    private String subTitle;
    private String body;
    private Map<String, Object> properties;
    private Integer badgeNumber;

    public void addProperty(String key, Object value) {
        if (properties == null) {
            properties = new HashMap<>(17);
        }
        properties.put(key, value);
    }

    public Map<String, String> getAllStringProperties() {
        Map<String, String> result = new HashMap<>();
        if (this.properties != null) {
            this.properties.forEach((k, v) -> result.put(k, String.valueOf(v)));
        }
        return result;
    }
}
