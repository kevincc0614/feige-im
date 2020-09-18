package com.ds.feige.im.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author DC
 */
public class JsonUtils {
    static final ObjectMapper commonMapper = new ObjectMapper();

    static {
        commonMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static String toJson(Object object) throws JsonProcessingException {
        return commonMapper.writeValueAsString(object);
    }

    public static <T> T jsonToBean(String json, Class<T> type) throws JsonProcessingException {
        return commonMapper.readValue(json, type);
    }
}
