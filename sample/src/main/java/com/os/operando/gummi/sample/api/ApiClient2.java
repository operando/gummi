package com.os.operando.gummi.sample.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.os.operando.guild.Pair;
import com.os.operando.gummi.JsonRpc3;
import com.os.operando.gummi.RequestType3;
import com.os.operando.gummi.Result;

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
public class ApiClient2 {

    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private static JsonRpc3 createJsonRpc() {
        return new JsonRpc3(new NumberIdGenerator());
    }

    public <T1, T2> Pair<Result<T1>, Result<T2>> request(RequestType3<T1> requestType1, RequestType3<T2> requestType2) {
        JsonRpc3 jsonrpc = createJsonRpc();

        jsonrpc.addRequest(requestType1);
        jsonrpc.addRequest(requestType2);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(loggingInterceptor);

        OkHttpClient client = builder.build();

        Gson gson = new Gson();
        TypeAdapter<List<JsonObject>> adapter = gson.getAdapter(new TypeToken<List<JsonObject>>() {
        });
        Buffer buffer = new Buffer();
        Writer writer = new OutputStreamWriter(buffer.outputStream(), UTF_8);
        JsonWriter jsonWriter;
        try {
            jsonWriter = gson.newJsonWriter(writer);
            adapter.write(jsonWriter, jsonrpc.getRequests());
            jsonWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(MEDIA_TYPE, buffer.readByteString());

        Request request = new Request.Builder()
                .url("https://www.dropbox.com/s/4q4w5zldfxeei96/test.json?dl=1")
                .post(requestBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                int statusCode = response.code();
//                if (apiCallback != null) {
//                    apiCallback.failure(new ApiResponseException(statusCode, response.message(), response.body()));
//                }
                return null;
            }

            ResponseBody value = response.body();
            JsonReader jsonReader = gson.newJsonReader(value.charStream());
            try {
                List<JsonObject> jsonObjects = adapter.read(jsonReader);
//                for (JsonObject jsonObject : jsonObjects) {
//                    Result<?> result = jsonrpc.parseResponseJson(jsonObject);
//                }
                return Pair.create(jsonrpc.parseResponseJson(jsonObjects.get(0)),
                        jsonrpc.parseResponseJson(jsonObjects.get(1)));
            } finally {
                value.close();
            }
        } catch (IOException e) {
//            if (apiCallback != null) {
//                apiCallback.failure(e);
//            }
            return null;
        }
    }
}
