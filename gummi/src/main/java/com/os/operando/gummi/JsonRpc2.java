package com.os.operando.gummi;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JsonRpc2 {

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
    private Map<String, Iremono<?, ?>> requests = new HashMap<>();

    public JsonRpc2(RequestIdentifierGenerator requestIdentifierGenerator) {
        this.requestIdentifierGenerator = requestIdentifierGenerator;
    }

    public List<JsonObject> getRequests() {
        List<JsonObject> requests = new ArrayList<>();
        Set<Map.Entry<String, Iremono<?, ?>>> set = this.requests.entrySet();
        for (Map.Entry<String, Iremono<?, ?>> entry : set) {
            requests.add(buildJsonFromRequest(entry.getValue().in(), entry.getKey()));
        }
        return requests;
    }

    public <T> void addRequest(Iremono<?, ?> iremono) {
        String id = requestIdentifierGenerator.next();
        requests.put(id, iremono);
    }

    private JsonObject buildJsonFromRequest(RequestType2 requestType, String id) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add(KEY_PARAMS, GSON.toJsonTree(requestType));
        jsonObject.addProperty(KEY_METHOD, requestType.getMethod());
        jsonObject.addProperty(KEY_JSONRPC, VERSION);
        jsonObject.addProperty(KEY_ID, id);
        return jsonObject;
    }

    public Result<?> parseResponseJson(JsonObject jsonObject) {
        Iremono<?, ?> iremono = requests.get(jsonObject.get(KEY_ID).getAsString());

        String version = jsonObject.get(KEY_JSONRPC).getAsString();
        if (!VERSION.equals(version)) {
            // TODO: error code
            JsonRpcException jsonRpcException = new JsonRpcException(1, "version mismatch.", jsonObject);
            return new Result<>(null, jsonRpcException);
        }

        JsonElement result = jsonObject.get(KEY_RESULT);
        if (result != null) {
            // TODO:resultを使ってResponseを組み立てる
//            if (response == null) {
//                // TODO: error code
//                JsonRpcException jsonRpcException = new JsonRpcException(1, "response is null.", jsonObject);
//                return new Result<>(null, jsonRpcException);
//            }
            return new Result<>(iremono.out(), null);
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