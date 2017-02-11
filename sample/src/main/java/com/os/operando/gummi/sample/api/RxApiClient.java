package com.os.operando.gummi.sample.api;

import com.annimon.stream.Stream;
import com.os.operando.guild.Pair;
import com.os.operando.guild.Triplet;
import com.os.operando.gummi.JsonRpc;
import com.os.operando.gummi.RequestType;
import com.os.operando.gummi.Result;

import rx.Observable;
import rx.Subscriber;

public class RxApiClient {

    private final ApiClient apiClient;

    public RxApiClient() {
        apiClient = new ApiClient();
    }

    public <T> Observable<T> responseFrom(RequestType<T> requestType) {
        return Observable.create(new Observable.OnSubscribe<T>() {

            private Result<T> tResult;

            @Override
            public void call(Subscriber<? super T> subscriber) {
                JsonRpc jsonrpc = ApiClient.createJsonRpc();
                jsonrpc.addRequest(requestType, result -> tResult = result);

                apiClient.request(jsonrpc, new ApiClient.ApiCallback() {
                    @Override
                    public void callback() {
                        if (tResult.isSuccessful()) {
                            subscriber.onNext(tResult.value);
                            subscriber.onCompleted();
                        } else {
                            subscriber.onError(tResult.throwable);
                        }
                    }

                    @Override
                    public void failure(Throwable throwable) {
                        subscriber.onError(throwable);
                    }
                });
            }
        });
    }

    public <T1, T2> Observable<Pair<T1, T2>> responseFrom(RequestType<T1> requestType1, RequestType<T2> requestType2) {
        return Observable.create(new Observable.OnSubscribe<Pair<T1, T2>>() {

            private Result<T1> t1;
            private Result<T2> t2;

            @Override
            public void call(Subscriber<? super Pair<T1, T2>> subscriber) {
                System.out.println("Thread : " + Thread.currentThread().getName());

                JsonRpc jsonrpc = ApiClient.createJsonRpc();

                jsonrpc.addRequest(requestType1, result -> t1 = result);
                jsonrpc.addRequest(requestType2, result -> t2 = result);

                apiClient.request(jsonrpc, new ApiClient.ApiCallback() {
                    @Override
                    public void callback() {
                        if (t1.isSuccessful() && t2.isSuccessful()) {
                            Pair<T1, T2> tuple2 = Pair.create(t1.value, t2.value);
                            subscriber.onNext(tuple2);
                            subscriber.onCompleted();
                        } else {
                            getThrowableStream(t1.throwable, t2.throwable)
                                    .forEach(subscriber::onError);
                        }
                    }

                    @Override
                    public void failure(Throwable throwable) {
                        subscriber.onError(throwable);
                    }
                });
            }
        });
    }

    public <T1, T2, T3> Observable<Triplet<T1, T2, T3>> responseFrom(RequestType<T1> requestType, RequestType<T2> requestType2, RequestType<T3> requestType3) {
        return Observable.create(new Observable.OnSubscribe<Triplet<T1, T2, T3>>() {

            private Result<T1> t1;
            private Result<T2> t2;
            private Result<T3> t3;

            @Override
            public void call(Subscriber<? super Triplet<T1, T2, T3>> subscriber) {
                JsonRpc jsonrpc = ApiClient.createJsonRpc();

                jsonrpc.addRequest(requestType, result -> t1 = result);
                jsonrpc.addRequest(requestType2, result -> t2 = result);
                jsonrpc.addRequest(requestType3, result -> t3 = result);

                apiClient.request(jsonrpc, new ApiClient.ApiCallback() {
                    @Override
                    public void callback() {
                        if (t1.isSuccessful() && t2.isSuccessful() && t3.isSuccessful()) {
                            Triplet<T1, T2, T3> triplet = Triplet.create(t1.value, t2.value, t3.value);
                            subscriber.onNext(triplet);
                            subscriber.onCompleted();
                        } else {
                            getThrowableStream(t1.throwable, t2.throwable, t3.throwable)
                                    .forEach(subscriber::onError);
                        }
                    }

                    @Override
                    public void failure(Throwable throwable) {
                        subscriber.onError(throwable);
                    }
                });
            }
        });
    }

    private Stream<Throwable> getThrowableStream(Throwable... throwable) {
        // はじめのThrowableを通知する
        return Stream.of(throwable).filter(v -> v != null).limit(1);
    }
}
