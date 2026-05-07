package com.trainstation.network;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public final class JsonMapperProvider {
    private static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());

    private JsonMapperProvider() {}

    public static ObjectMapper getMapper() {
        return MAPPER;
    }
}
