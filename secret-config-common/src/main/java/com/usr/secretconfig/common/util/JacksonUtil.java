package com.usr.secretconfig.common.util;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * 
 * @author dht31261
 * @date 2025年10月12日 20:24:56
 */
public class JacksonUtil {

    public static ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        objectMapper.enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    public static String toJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return getObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T toObject(String json, Class<T> type) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        if (type.getTypeName().equals(String.class.getTypeName())) {
            return (T) json;
        }
        try {
            return getObjectMapper().readValue(json, type);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> toList(String json, Class<T> type) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        return toObject(json, new TypeReference<List<T>>() {
        });
    }

    @SuppressWarnings("unchecked")
    public static <T> T toObject(String json, TypeReference<T> type) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        if (type.getType().getTypeName().equals(String.class.getTypeName())) {
            return (T) json;
        }
        try {
            return getObjectMapper().readValue(json, type);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}