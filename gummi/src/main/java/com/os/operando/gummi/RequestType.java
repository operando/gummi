package com.os.operando.gummi;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class RequestType<T> {

    public abstract String getMethod();

    public Type getType() {
        System.out.print(((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
        return ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public interface ResponseCallback<T> {

        void onResponse(Result<T> result);
    }
}