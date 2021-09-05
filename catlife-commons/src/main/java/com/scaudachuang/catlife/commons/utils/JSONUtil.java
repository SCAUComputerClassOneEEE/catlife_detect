package com.scaudachuang.catlife.commons.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * @author hiluyx
 * @since 2021/9/1 11:15
 **/
public class JSONUtil {
    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static <T> T readValue(String str, Class<T> cl) throws IOException {
        if (str == null || str.equals(""))
            throw new NullPointerException("str content is null ro empty.");
        return mapper.readValue(str, cl);
    }

    public static String writeValue(Object o) throws JsonProcessingException {
        return mapper.writeValueAsString(o);
    }

    public static <K, T> Map<K, Set<T>> readMapSetValue(String str) throws IOException {
        return mapper.readValue(str, new TypeReference<Map<K, Set<T>>>(){});
    }
}
