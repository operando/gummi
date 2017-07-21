package com.os.operando.gummi2;

public interface RequestType<T> {

    String getMethod();

    Class<T> getResponseType();
}