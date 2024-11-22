package com.gallery_app.core_service.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nullable;

public class JsonUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    /**
     * Converts an object to its JSON string representation.
     *
     * @param object the object to convert
     * @return JSON string representation of the object
     */
    @Nullable
    public static String toJson(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            //
        }
        return null;
    }

    /**
     * Converts a JSON string to an object of the specified class.
     *
     * @param json  the JSON string
     * @param clazz the target class
     * @param <T>   the type of the class
     * @return the object represented by the JSON string
     */
    @Nullable
    public static <T> T fromJson(String json, Class<T> clazz) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            //
        }
        return null;
    }

    /**
     * Converts a JSON string to an object of a specified type reference (for generic types).
     *
     * @param json    the JSON string
     * @param typeRef the target type reference
     * @param <T>     the type of the class
     * @return the object represented by the JSON string
     */
    public static <T> T fromJson(String json, com.fasterxml.jackson.core.type.TypeReference<T> typeRef) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(json, typeRef);
        } catch (JsonProcessingException e) {
            //
        }
        return null;
    }
}

