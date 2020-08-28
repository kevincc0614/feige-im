package com.ds.feige.im.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {
    static final ObjectMapper commonMapper=new ObjectMapper();
    public static String toJson(Object object) throws JsonProcessingException {
        return commonMapper.writeValueAsString(object);
    }
    public static <T> T jsonToBean(String json,Class<T> type) throws JsonProcessingException {
        return commonMapper.readValue(json, type);
    }
}
