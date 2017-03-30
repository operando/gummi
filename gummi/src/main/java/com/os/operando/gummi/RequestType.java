package com.os.operando.gummi;

public abstract class RequestType<T> {

    public abstract String getMethod();

    public abstract Class<T> getResponseType();
}