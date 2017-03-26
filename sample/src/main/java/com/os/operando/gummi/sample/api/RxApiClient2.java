package com.os.operando.gummi.sample.api;

import com.annimon.stream.Stream;
import com.os.operando.guild.Pair;
import com.os.operando.gummi.JsonRpc;
import com.os.operando.gummi.RequestType;
import com.os.operando.gummi.RequestType3;
import com.os.operando.gummi.Result;

import rx.Observable;
import rx.Subscriber;

public class RxApiClient2 {

    private final ApiClient2 apiClient;

    public RxApiClient2() {
        apiClient = new ApiClient2();
    }

    public <T> Observable<T> responseFrom(RequestType<T> requestType) {
        return Observable.create(new Observable.OnSubscribe<T>() {

            private Result<T> tResult;

            @Override
            public void call(Subscriber<? super T> subscriber) {
                JsonRpc jsonrpc = ApiClient.createJsonRpc();
                jsonrpc.addRequest(requestType, result -> tResult = result);

//                apiClient.request(jsonrpc, new ApiClient.ApiCallback() {
//                    @Override
//                    public void callback() {
//                        if (tResult.isSuccessful()) {
//                            subscriber.onNext(tResult.value);
//                            subscriber.onCompleted();
//                        } else {
//                            subscriber.onError(tResult.throwable);
//                        }
//                    }
//
//                    @Override
//                    public void failure(Throwable throwable) {
//                        subscriber.onError(throwable);
//                    }
//                });
            }
        });
    }

    public <T1, T2> Observable<Pair<T1, T2>> responseFrom(RequestType3<T1> requestType1, RequestType3<T2> requestType2) {
        return Observable.create(subscriber -> {
            System.out.println("Thread : " + Thread.currentThread().getName());

            Pair<Result<T1>, Result<T2>> pair = apiClient.request(requestType1, requestType2);
            Result<T1> t1 = pair.getFirst();
            Result<T2> t2 = pair.getSecond();

            if (t1.isSuccessful() && t2.isSuccessful()) {
                Pair<T1, T2> tuple2 = Pair.create(t1.value, t2.value);
                subscriber.onNext(tuple2);
                subscriber.onCompleted();
            } else {
                getThrowableStream(t1.throwable, t2.throwable)
                        .forEach(subscriber::onError);
            }
        });
    }

    private Stream<Throwable> getThrowableStream(Throwable... throwable) {
        // はじめのThrowableを通知する
        return Stream.of(throwable).filter(v -> v != null).limit(1);
    }
}
