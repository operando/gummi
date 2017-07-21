package com.os.operando.gummi;

public class Result<T> {
    public T value;
    public Throwable throwable;

    public Result(T value, Throwable throwable) {
        this.value = value;
        this.throwable = throwable;
    }

    public boolean isSuccessful() {
        return value != null && throwable == null;
    }
}