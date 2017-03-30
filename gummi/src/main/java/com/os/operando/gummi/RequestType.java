package com.os.operando.gummi;

public abstract class RequestType<T> {

    public abstract String getMethod();


    public interface ResponseCallback<T> {

        void onResponse(Result<T> result);
    }
    public abstract Class<T> getResponseType();
}