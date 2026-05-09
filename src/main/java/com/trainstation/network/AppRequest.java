package com.trainstation.network;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.io.Serializable;

public class AppRequest implements Serializable {
    private static final long serialVersionUID = 1L;
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
        return JsonMapperProvider.getMapper().writeValueAsString(this);
    }

    public static AppRequest fromJson(String json) throws IOException {
        return JsonMapperProvider.getMapper().readValue(json, AppRequest.class);
    }
}
