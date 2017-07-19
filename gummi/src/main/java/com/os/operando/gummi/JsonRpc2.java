package com.os.operando.gummi;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;

public class JsonRpc2 {

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

    public JsonRpc2(RequestIdentifierGenerator requestIdentifierGenerator) {
        this.requestIdentifierGenerator = requestIdentifierGenerator;
    }

    public <T> JsonRpcRequest<T> createRequest(RequestType<T> requestType) {
        String id = requestIdentifierGenerator.next();
        return new JsonRpcRequest<>(requestType, id, buildJsonFromRequest(requestType, id));
    }

    private JsonObject buildJsonFromRequest(RequestType<?> requestType, String id) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add(KEY_PARAMS, GSON.toJsonTree(requestType));
        jsonObject.addProperty(KEY_METHOD, requestType.getMethod());
        jsonObject.addProperty(KEY_JSONRPC, VERSION);
        jsonObject.addProperty(KEY_ID, id);
        return jsonObject;
    }

    public <T> Result<T> parseResponseJson(List<JsonObject> responseJsonList, JsonRpcRequest<T> jsonRpcRequest) {
        for (JsonObject responseJson : responseJsonList) {
            String id = responseJson.get(KEY_ID).getAsString();
            if (!jsonRpcRequest.id.equals(id)) {
                continue;
            }

            String version = responseJson.get(KEY_JSONRPC).getAsString();
            if (!VERSION.equals(version)) {
                // TODO: error code
                JsonRpcException jsonRpcException = new JsonRpcException(1, "version mismatch.", responseJson);
                return new Result<>(null, jsonRpcException);
            }

            JsonElement result = responseJson.get(KEY_RESULT);
            if (result != null) {
                RequestType<T> requestType = jsonRpcRequest.requestType;
                T response = GSON.fromJson(result, requestType.getResponseType());
                if (response == null) {
                    // TODO: error code
                    JsonRpcException jsonRpcException = new JsonRpcException(1, "response is null.", responseJson);
                    return new Result<>(null, jsonRpcException);
                }
                return new Result<>(response, null);
            }

            JsonElement error = responseJson.get(KEY_ERROR);
            if (error != null) {
                JsonObject errorObject = error.getAsJsonObject();
                JsonElement code = errorObject.get(KEY_CODE);
                JsonElement message = errorObject.get(KEY_MESSAGE);
                if (code != null && message != null) {
                    JsonRpcException jsonRpcException = new JsonRpcException(code.getAsInt(), message.getAsString(), responseJson);
                    return new Result<>(null, jsonRpcException);
                }
            }
        }

        // TODO: error code
        JsonRpcException jsonRpcException = new JsonRpcException(0, "A response with the same id as the request could not be found.", null);
        return new Result<>(null, jsonRpcException);
    }
}