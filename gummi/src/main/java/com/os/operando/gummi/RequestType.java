package com.os.operando.gummi;

// It may be changed to interface in the future
public abstract class RequestType<T> {

    public abstract String getMethod();

    public abstract Class<T> getResponseType();
}