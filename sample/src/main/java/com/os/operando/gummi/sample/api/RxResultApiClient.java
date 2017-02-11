package com.os.operando.gummi.sample.api;

import com.os.operando.gummi.JsonRpc;
import com.os.operando.gummi.RequestType;
import com.os.operando.gummi.Result;
import com.os.operando.gummi.sample.model.ResultPair;
import com.os.operando.gummi.sample.model.ResultTriplet;

import rx.Observable;
import rx.Subscriber;

public class RxResultApiClient {

    private final ApiClient apiClient;

    public RxResultApiClient() {
        apiClient = new ApiClient();
    }

    public <T> Observable<Result<T>> resultFrom(RequestType<T> requestType) {
        return Observable.create(
                new Observable.OnSubscribe<Result<T>>() {

                    private Result<T> tResult;

                    @Override
                    public void call(Subscriber<? super Result<T>> subscriber) {
                        JsonRpc jsonrpc = ApiClient.createJsonRpc();
                        jsonrpc.addRequest(requestType, result -> tResult = result);

                        apiClient.request(jsonrpc, new ApiClient.ApiCallback() {
                            @Override
                            public void callback() {
                                subscriber.onNext(tResult);
                                subscriber.onCompleted();
                            }

                            @Override
                            public void failure(Throwable throwable) {
                                subscriber.onError(throwable);
                            }
                        });
                    }
                }

        );
    }

    public <T1, T2> Observable<ResultPair<T1, T2>> resultFrom(RequestType<T1> requestType1, RequestType<T2> requestType2) {
        return Observable.create(new Observable.OnSubscribe<ResultPair<T1, T2>>() {

            private Result<T1> t1;
            private Result<T2> t2;

            @Override
            public void call(Subscriber<? super ResultPair<T1, T2>> subscriber) {
                JsonRpc jsonrpc = ApiClient.createJsonRpc();

                jsonrpc.addRequest(requestType1, result -> t1 = result);
                jsonrpc.addRequest(requestType2, result -> t2 = result);

                apiClient.request(jsonrpc, new ApiClient.ApiCallback() {
                    @Override
                    public void callback() {
                        ResultPair<T1, T2> tuple2 = ResultPair.create(t1, t2);
                        subscriber.onNext(tuple2);
                        subscriber.onCompleted();
                    }

                    @Override
                    public void failure(Throwable throwable) {
                        subscriber.onError(throwable);
                    }
                });
            }
        });
    }

    public <T1, T2, T3> Observable<ResultTriplet<T1, T2, T3>> resultFrom(RequestType<T1> requestType1, RequestType<T2> requestType2, RequestType<T3> requestType3) {
        return Observable.create(new Observable.OnSubscribe<ResultTriplet<T1, T2, T3>>() {

            private Result<T1> t1;
            private Result<T2> t2;
            private Result<T3> t3;

            @Override
            public void call(Subscriber<? super ResultTriplet<T1, T2, T3>> subscriber) {
                JsonRpc jsonrpc = ApiClient.createJsonRpc();

                jsonrpc.addRequest(requestType1, result -> t1 = result);
                jsonrpc.addRequest(requestType2, result -> t2 = result);
                jsonrpc.addRequest(requestType3, result -> t3 = result);

                apiClient.request(jsonrpc, new ApiClient.ApiCallback() {
                    @Override
                    public void callback() {
                        ResultTriplet<T1, T2, T3> tuple2 = ResultTriplet.create(t1, t2, t3);
                        subscriber.onNext(tuple2);
                        subscriber.onCompleted();
                    }

                    @Override
                    public void failure(Throwable throwable) {
                        subscriber.onError(throwable);
                    }
                });
            }
        });
    }
}
