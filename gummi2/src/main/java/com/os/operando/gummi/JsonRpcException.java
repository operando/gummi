package com.os.operando.gummi;

import com.google.gson.JsonObject;

public class JsonRpcException extends Exception {
    private final Integer code;
    private final String message;
    private final JsonObject data;
    private final JsonRpcRequest<?> jsonRpcRequest;

    public JsonRpcException(Integer code, String message, JsonObject data, JsonRpcRequest<?> jsonRpcRequest) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.jsonRpcRequest = jsonRpcRequest;
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

    public JsonRpcRequest<?> getJsonRpcRequest() {
        return jsonRpcRequest;
    }
}