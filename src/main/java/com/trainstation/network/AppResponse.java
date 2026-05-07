package com.trainstation.network;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.io.Serializable;

public class AppResponse implements Serializable {
    private static final long serialVersionUID = 1L;
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
        return JsonMapperProvider.getMapper().writeValueAsString(this);
    }

    public static AppResponse fromJson(String json) throws IOException {
        return JsonMapperProvider.getMapper().readValue(json, AppResponse.class);
    }
}
