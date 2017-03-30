package com.os.operando.gummi;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JsonRpc {

    private static final String VERSION = "2.0";

    private static final String KEY_ID = "id";
    private static final String KEY_JSONRPC = "jsonrpc";
    private static final String KEY_RESULT = "result";
    private static final String KEY_ERROR = "error";
    private static final String KEY_CODE = "code";
    private static final String KEY_MESSAGE = "message";

    private static final String KEY_PARAMS = "params";
    private static final String KEY_METHOD = "method";

    private static final Gson GSON = new Gson();

    private final RequestIdentifierGenerator requestIdentifierGenerator;
    private Map<String, RequestType> requests = new HashMap<>();
    private Map<String, ResponseCallback> handlers = new HashMap<>();

    public JsonRpc(RequestIdentifierGenerator requestIdentifierGenerator) {
        this.requestIdentifierGenerator = requestIdentifierGenerator;
    }

    public List<JsonObject> getRequests() {
        List<JsonObject> requests = new ArrayList<>();
        Set<Map.Entry<String, RequestType>> set = this.requests.entrySet();
        for (Map.Entry<String, RequestType> entry : set) {
            requests.add(buildJsonFromRequest(entry.getValue(), entry.getKey()));
        }
        return requests;
    }

    public <T> void addRequest(RequestType<T> requestType, ResponseCallback<T> responseCallback) {
        String id = requestIdentifierGenerator.next();
        requests.put(id, requestType);
        handlers.put(id, responseCallback);
    }

    private <T> JsonObject buildJsonFromRequest(RequestType<T> requestType, String id) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add(KEY_PARAMS, GSON.toJsonTree(requestType));
        jsonObject.addProperty(KEY_METHOD, requestType.getMethod());
        jsonObject.addProperty(KEY_JSONRPC, VERSION);
        jsonObject.addProperty(KEY_ID, id);
        return jsonObject;
    }

    public void parseResponseJson(JsonObject jsonObject) {
        handleResponse(jsonObject);
    }

    private <T> void handleResponse(JsonObject jsonObject) {
        ResponseCallback<T> responseCallback = handlers.get(jsonObject.get(KEY_ID).getAsString());

        String version = jsonObject.get(KEY_JSONRPC).getAsString();
        if (!VERSION.equals(version)) {
            // TODO: error code
            JsonRpcException jsonRpcException = new JsonRpcException(1, "version mismatch.", jsonObject);
            Result<T> errorResult = new Result<>(null, jsonRpcException);
            responseCallback.onResponse(errorResult);
            return;
        }

        JsonElement result = jsonObject.get(KEY_RESULT);
        String id = jsonObject.get(KEY_ID).getAsString();
        if (result != null) {
            RequestType<T> requestType = requests.get(id);
            T response = GSON.fromJson(result, requestType.getResponseType());
            if (response == null) {
                // TODO: error code
                JsonRpcException jsonRpcException = new JsonRpcException(1, "response is null.", jsonObject);
                Result<T> errorResult = new Result<>(null, jsonRpcException);
                responseCallback.onResponse(errorResult);
                return;
            }
            Result<T> responseResult = new Result<>(response, null);
            responseCallback.onResponse(responseResult);
            return;
        }

        JsonElement error = jsonObject.get(KEY_ERROR);
        if (error != null) {
            JsonObject errorObject = error.getAsJsonObject();
            JsonElement code = errorObject.get(KEY_CODE);
            JsonElement message = errorObject.get(KEY_MESSAGE);
            if (code != null && message != null) {
                JsonRpcException jsonRpcException = new JsonRpcException(code.getAsInt(), message.getAsString(), jsonObject);
                Result<T> errorResult = new Result<>(null, jsonRpcException);
                responseCallback.onResponse(errorResult);
            }
        }
    }
}