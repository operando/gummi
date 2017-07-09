package com.os.operando.gummi;

import com.google.gson.JsonObject;

public class JsonRpcRequest<T> {
    public RequestType<T> requestType;
    public String id;
    public JsonObject jsonObject;

    public JsonRpcRequest(RequestType<T> requestType, String id, JsonObject jsonObject) {
        this.requestType = requestType;
        this.id = id;
        this.jsonObject = jsonObject;
    }
}