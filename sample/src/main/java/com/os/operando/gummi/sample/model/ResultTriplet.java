package com.os.operando.gummi.sample.model;

import com.os.operando.gummi.Result;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ResultTriplet<F, S, T> {
    private final Result<F> first;
    private final Result<S> second;
    private final Result<T> thread;

    private ResultTriplet(Result<F> first, Result<S> second, Result<T> thread) {
        this.first = first;
        this.second = second;
        this.thread = thread;
    }

    public static <F, S, T> ResultTriplet<F, S, T> create(Result<F> first, Result<S> second, Result<T> thread) {
        return new ResultTriplet<>(first, second, thread);
    }
}