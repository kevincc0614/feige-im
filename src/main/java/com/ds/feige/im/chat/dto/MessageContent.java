package com.ds.feige.im.chat.dto;

import com.ds.feige.im.common.util.JsonUtils;
import com.google.common.collect.Maps;

import java.io.IOException;
import java.util.Map;

/**
 * @author DC
 */
public class MessageContent {
    public static final String TEXT = "text";
    public static final String PIC = "picUrl";
    public static final String FILE = "fileUrl";
    private Map<String, Object> values = Maps.newHashMap();

    public void put(String key, Object value) {
        values.put(key, value);
    }

    public String toJson() throws IOException {
        return JsonUtils.toJson(values);
    }
}
