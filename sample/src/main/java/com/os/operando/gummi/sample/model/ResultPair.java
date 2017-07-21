package com.os.operando.gummi.sample.model;

import com.os.operando.gummi2.Result;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ResultPair<F, S> {
    private final Result<F> first;
    private final Result<S> second;

    private ResultPair(Result<F> first, Result<S> second) {
        this.first = first;
        this.second = second;
    }

    public static <F, S> ResultPair<F, S> create(Result<F> first, Result<S> second) {
        return new ResultPair<>(first, second);
    }
}