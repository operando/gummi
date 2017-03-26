package com.os.operando.gummi;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JsonRpc3 {

    public static final String VERSION = "2.0";

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
    private Map<String, RequestType3<?>> requests = new HashMap<>();

    public JsonRpc3(RequestIdentifierGenerator requestIdentifierGenerator) {
        this.requestIdentifierGenerator = requestIdentifierGenerator;
    }

    public List<JsonObject> getRequests() {
        List<JsonObject> requests = new ArrayList<>();
        Set<Map.Entry<String, RequestType3<?>>> set = this.requests.entrySet();
        for (Map.Entry<String, RequestType3<?>> entry : set) {
            requests.add(buildJsonFromRequest(entry.getValue(), entry.getKey()));
        }
        return requests;
    }

    public <T> void addRequest(RequestType3<T> requestType) {
        String id = requestIdentifierGenerator.next();
        requests.put(id, requestType);
    }

    private <T> JsonObject buildJsonFromRequest(RequestType3<T> requestType, String id) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add(KEY_PARAMS, GSON.toJsonTree(requestType));
        jsonObject.addProperty(KEY_METHOD, requestType.getMethod());
        jsonObject.addProperty(KEY_JSONRPC, VERSION);
        jsonObject.addProperty(KEY_ID, id);
        return jsonObject;
    }

    public <T> Result<T> parseResponseJson(JsonObject jsonObject) {
        String version = jsonObject.get(KEY_JSONRPC).getAsString();
        if (!VERSION.equals(version)) {
            // TODO: error code
            JsonRpcException jsonRpcException = new JsonRpcException(1, "version mismatch.", jsonObject);
            return new Result<>(null, jsonRpcException);
        }

        JsonElement result = jsonObject.get(KEY_RESULT);
        String id = jsonObject.get(KEY_ID).getAsString();
        if (result != null) {
            RequestType3<?> requestType = requests.get(id);
            T response = GSON.fromJson(result, requestType.getType());
            if (response == null) {
                // TODO: error code
                JsonRpcException jsonRpcException = new JsonRpcException(1, "response is null.", jsonObject);
                return new Result<>(null, jsonRpcException);
            }
            return new Result<>(response, null);
        }

        JsonElement error = jsonObject.get(KEY_ERROR);
        if (error != null) {
            JsonObject errorObject = error.getAsJsonObject();
            JsonElement code = errorObject.get(KEY_CODE);
            JsonElement message = errorObject.get(KEY_MESSAGE);
            if (code != null && message != null) {
                JsonRpcException jsonRpcException = new JsonRpcException(code.getAsInt(), message.getAsString(), jsonObject);
                return new Result<>(null, jsonRpcException);
            }
        }
        return null;
    }
}