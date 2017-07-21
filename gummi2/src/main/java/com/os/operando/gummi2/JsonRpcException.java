package com.os.operando.gummi2;

import com.google.gson.JsonObject;

public class JsonRpcException extends Exception {
    private final Integer code;
    private final String message;
    private final JsonObject data;

    public JsonRpcException(Integer code, String message, JsonObject data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public JsonObject getData() {
        return data;
    }
}