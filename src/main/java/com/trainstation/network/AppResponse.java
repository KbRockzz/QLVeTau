package com.trainstation.network;

import java.io.Serializable;

public class AppResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private final boolean success;
    private final Object data;
    private final String message;

    public AppResponse(boolean success, Object data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }

    public static AppResponse ok(Object data) { return new AppResponse(true, data, null); }
    public static AppResponse error(String msg) { return new AppResponse(false, null, msg); }

    public boolean isSuccess() { return success; }
    public Object getData() { return data; }
    public String getMessage() { return message; }
}
