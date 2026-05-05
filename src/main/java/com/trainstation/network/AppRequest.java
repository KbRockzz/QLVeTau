package com.trainstation.network;

import java.io.Serializable;

public class AppRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private final RequestType type;
    private final Object[] params;

    public AppRequest(RequestType type, Object... params) {
        this.type = type;
        this.params = params;
    }

    public RequestType getType() { return type; }
    public Object[] getParams() { return params; }
}
