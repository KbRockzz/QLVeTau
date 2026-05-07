package com.trainstation.network;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.io.Serializable;

public class AppRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());
    private final RequestType type;
    private final Object[] params;

    @JsonCreator
    public AppRequest(@JsonProperty("type") RequestType type,
                      @JsonProperty("params") Object... params) {
        this.type = type;
        this.params = params;
    }

    public RequestType getType() { return type; }
    public Object[] getParams() { return params; }

    public String toJson() throws JsonProcessingException {
        return MAPPER.writeValueAsString(this);
    }

    public static AppRequest fromJson(String json) throws IOException {
        return MAPPER.readValue(json, AppRequest.class);
    }
}
