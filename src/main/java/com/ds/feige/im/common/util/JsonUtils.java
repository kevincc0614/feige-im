package com.ds.feige.im.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

/**
 * @author DC
 */
public class JsonUtils {
    static final ObjectMapper commonMapper = new ObjectMapper();

    static {
        commonMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        commonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    }

    public static String toJson(Object object) throws JsonProcessingException {
        return commonMapper.writeValueAsString(object);
    }

    public static <T> T jsonToBean(String json, Class<T> type) throws JsonProcessingException {
        if (Strings.isNullOrEmpty(json)) {
            return null;
        }
        return commonMapper.readValue(json, type);
    }
}
