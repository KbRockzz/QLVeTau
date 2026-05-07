package com.trainstation.network;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.io.Serializable;

public class AppResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());
    private final boolean success;
    private final Object data;
    private final String message;

    @JsonCreator
    public AppResponse(@JsonProperty("success") boolean success,
                       @JsonProperty("data") Object data,
                       @JsonProperty("message") String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }

    public static AppResponse ok(Object data) { return new AppResponse(true, data, null); }
    public static AppResponse error(String msg) { return new AppResponse(false, null, msg); }

    public boolean isSuccess() { return success; }
    public Object getData() { return data; }
    public String getMessage() { return message; }

    public String toJson() throws JsonProcessingException {
        return MAPPER.writeValueAsString(this);
    }

    public static AppResponse fromJson(String json) throws IOException {
        return MAPPER.readValue(json, AppResponse.class);
    }
}
