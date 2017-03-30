package com.os.operando.gummi;

public interface ResponseCallback<T> {
    void onResponse(Result<T> result);
}