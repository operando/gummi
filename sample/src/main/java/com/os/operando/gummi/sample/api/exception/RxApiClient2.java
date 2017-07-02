package com.os.operando.gummi.sample.api.exception;

import com.annimon.stream.Stream;
import com.google.gson.JsonObject;
import com.os.operando.guild.Pair;
import com.os.operando.guild.Triplet;
import com.os.operando.gummi.JsonRpc2;
import com.os.operando.gummi.RequestType;
import com.os.operando.gummi.Result;
import com.os.operando.gummi.sample.api.ApiClient2;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.Single;

public class RxApiClient2 {

    private final ApiClient2 apiClient;

    public RxApiClient2() {
        apiClient = new ApiClient2();
    }

    public <T> Single<T> responseFrom(RequestType<T> requestType) {
        return Single.create(singleSubscriber -> {
            System.out.println("Thread : " + Thread.currentThread().getName());

            JsonRpc2 jsonrpc = ApiClient2.createJsonRpc();
            JsonObject jsonObject = jsonrpc.createRequest(requestType);
            JsonRpc2.Request<T> request = new JsonRpc2.Request<>(requestType, jsonObject.get("id").getAsString());

            List<JsonObject> results = apiClient.request2(jsonrpc, Collections.singletonList(request));
            Result<T> tResult = jsonrpc.parseResponseJson2(results.get(0), request);
            if (tResult.isSuccessful()) {
                singleSubscriber.onSuccess(tResult.value);
            } else {
                singleSubscriber.onError(tResult.throwable);
            }
        });
    }

    public <T1, T2> Observable<Pair<T1, T2>> responseFrom(RequestType<T1> requestType1, RequestType<T2> requestType2) {
        return Observable.create(subscriber -> {
            System.out.println("Thread : " + Thread.currentThread().getName());
            JsonRpc2 jsonrpc = ApiClient2.createJsonRpc();

            List<JsonObject> jsonObjects = jsonrpc.createRequest(requestType1, requestType2);
            JsonRpc2.Request<T1> request1 = new JsonRpc2.Request<>(requestType1, jsonObjects.get(0).get("id").getAsString());
            JsonRpc2.Request<T2> request2 = new JsonRpc2.Request<>(requestType2, jsonObjects.get(1).get("id").getAsString());

            List<JsonObject> results = apiClient.request2(jsonrpc, Arrays.asList(request1, request2));

            Result<T1> t1Result = jsonrpc.parseResponseJson(results, request1);
            Result<T2> t2Result = jsonrpc.parseResponseJson(results, request2);
            if (t1Result.isSuccessful() && t2Result.isSuccessful()) {
                Pair<T1, T2> tuple2 = Pair.create(t1Result.value, t2Result.value);
                subscriber.onNext(tuple2);
                subscriber.onCompleted();
            } else {
                getThrowableStream(t1Result.throwable, t2Result.throwable).forEach(subscriber::onError);
            }
        });
    }

    public <T1, T2, T3> Observable<Triplet<T1, T2, T3>> responseFrom(RequestType<T1> requestType1, RequestType<T2> requestType2, RequestType<T3> requestType3) {
        return Observable.create(subscriber -> {
            JsonRpc2 jsonrpc = ApiClient2.createJsonRpc();

            List<JsonObject> jsonObjects = jsonrpc.createRequest(requestType1, requestType2, requestType3);
            JsonRpc2.Request<T1> request1 = new JsonRpc2.Request<>(requestType1, jsonObjects.get(0).get("id").getAsString());
            JsonRpc2.Request<T2> request2 = new JsonRpc2.Request<>(requestType2, jsonObjects.get(1).get("id").getAsString());
            JsonRpc2.Request<T3> request3 = new JsonRpc2.Request<>(requestType3, jsonObjects.get(2).get("id").getAsString());

            List<JsonObject> results = apiClient.request2(jsonrpc, Arrays.asList(request1, request2));

            Result<T1> t1Result = jsonrpc.parseResponseJson(results, request1);
            Result<T2> t2Result = jsonrpc.parseResponseJson(results, request2);
            Result<T3> t3Result = jsonrpc.parseResponseJson(results, request3);

            if (t1Result.isSuccessful() && t2Result.isSuccessful() && t3Result.isSuccessful()) {
                Triplet<T1, T2, T3> triplet = Triplet.create(t1Result.value, t2Result.value, t3Result.value);
                subscriber.onNext(triplet);
                subscriber.onCompleted();
            } else {
                getThrowableStream(t1Result.throwable, t2Result.throwable, t3Result.throwable)
                        .forEach(subscriber::onError);
            }
        });
    }

    private Stream<Throwable> getThrowableStream(Throwable... throwable) {
        // はじめのThrowableを通知する
        return Stream.of(throwable).filter(v -> v != null).limit(1);
    }
}
