package com.os.operando.gummi.sample.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.os.operando.gummi.JsonRpc;
import com.os.operando.gummi.sample.api.exception.ApiResponseException;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.List;

import lombok.RequiredArgsConstructor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;

@RequiredArgsConstructor
public class ApiClient {

    public static JsonRpc createJsonRpc() {
        return new JsonRpc(new NumberIdGenerator());
    }

    public interface ApiCallback {
        void callback();

        void failure(Throwable throwable);
    }

    public void request(JsonRpc jsonrpc, final ApiCallback apiCallback) {
        responseFromJsonRpc(jsonrpc, apiCallback);
    }

    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private void responseFromJsonRpc(final JsonRpc jsonrpc, final ApiCallback apiCallback) {
        System.out.println("Thread : " + Thread.currentThread().getName());

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(loggingInterceptor);

        OkHttpClient client = builder.build();

        Gson gson = new Gson();
        TypeAdapter<List<JsonObject>> adapter = gson.getAdapter(new TypeToken<List<JsonObject>>(){});
        Buffer buffer = new Buffer();
        Writer writer = new OutputStreamWriter(buffer.outputStream(), UTF_8);
        JsonWriter jsonWriter = null;
        try {
            jsonWriter = gson.newJsonWriter(writer);
            adapter.write(jsonWriter, jsonrpc.getRequests());
            jsonWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(MEDIA_TYPE, buffer.readByteString());

        Request request = new Request.Builder()
                .url("https://dl.dropboxusercontent.com/u/97368150/test.json")
                .post(requestBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                int statusCode = response.code();
                if (apiCallback != null) {
                    apiCallback.failure(new ApiResponseException(statusCode, response.message(), response.body()));
                }
                return;
            }

            ResponseBody value = response.body();
            JsonReader jsonReader = gson.newJsonReader(value.charStream());
            try {
                List<JsonObject> jsonObjects = adapter.read(jsonReader);
                for (JsonObject jsonObject : jsonObjects) {
                    jsonrpc.parseResponseJson(jsonObject);
                }
                if (apiCallback != null) {
                    apiCallback.callback();
                }
            } finally {
                value.close();
            }
        } catch (IOException e) {
            if (apiCallback != null) {
                apiCallback.failure(e);
            }
        }
    }
}
