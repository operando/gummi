package com.os.operando.gummi.sample.api;

import com.google.gson.JsonObject;
import com.os.operando.gummi.sample.model.ResultPair;
import com.os.operando.gummi.sample.model.ResultTriplet;
import com.os.operando.gummi2.JsonRpc;
import com.os.operando.gummi2.JsonRpcRequest;
import com.os.operando.gummi2.RequestType;
import com.os.operando.gummi2.Result;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import rx.Single;

public class RxResultApiClient {

    private final ApiClient apiClient;

    public RxResultApiClient() {
        apiClient = new ApiClient();
    }

    public <T> Single<Result<T>> resultFrom(RequestType<T> requestType) {
        return Single.create(singleSubscriber -> {
            JsonRpc jsonrpc = ApiClient.createJsonRpc();

            JsonRpcRequest<T> request = jsonrpc.createRequest(requestType);

            List<JsonObject> results = apiClient.request(Collections.singletonList(request));
            Result<T> tResult = jsonrpc.parseResponseJson(results, request);

            singleSubscriber.onSuccess(tResult);
        });
    }

    public <T1, T2> Single<ResultPair<T1, T2>> resultFrom(RequestType<T1> requestType1, RequestType<T2> requestType2) {
        return Single.create(singleSubscriber -> {
            JsonRpc jsonrpc = ApiClient.createJsonRpc();

            JsonRpcRequest<T1> request1 = jsonrpc.createRequest(requestType1);
            JsonRpcRequest<T2> request2 = jsonrpc.createRequest(requestType2);

            List<JsonObject> results = apiClient.request(Arrays.asList(request1, request2));

            Result<T1> t1Result = jsonrpc.parseResponseJson(results, request1);
            Result<T2> t2Result = jsonrpc.parseResponseJson(results, request2);

            singleSubscriber.onSuccess(ResultPair.create(t1Result, t2Result));
        });
    }

    public <T1, T2, T3> Single<ResultTriplet<T1, T2, T3>> resultFrom(RequestType<T1> requestType1, RequestType<T2> requestType2, RequestType<T3> requestType3) {
        return Single.create(singleSubscriber -> {
            JsonRpc jsonrpc = ApiClient.createJsonRpc();

            JsonRpcRequest<T1> request1 = jsonrpc.createRequest(requestType1);
            JsonRpcRequest<T2> request2 = jsonrpc.createRequest(requestType2);
            JsonRpcRequest<T3> request3 = jsonrpc.createRequest(requestType3);

            List<JsonObject> results = apiClient.request(Arrays.asList(request1, request2, request3));

            Result<T1> t1Result = jsonrpc.parseResponseJson(results, request1);
            Result<T2> t2Result = jsonrpc.parseResponseJson(results, request2);
            Result<T3> t3Result = jsonrpc.parseResponseJson(results, request3);

            singleSubscriber.onSuccess(ResultTriplet.create(t1Result, t2Result, t3Result));
        });
    }
}