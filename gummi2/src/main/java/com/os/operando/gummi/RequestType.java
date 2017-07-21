package com.os.operando.gummi;

public interface RequestType<T> {

    String getMethod();

    Class<T> getResponseType();
}