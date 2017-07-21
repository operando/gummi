package com.os.operando.gummi.sample.api;

import com.os.operando.gummi.RequestIdentifierGenerator;

public class NumberIdGenerator implements RequestIdentifierGenerator {

    private int currentId = 0;

    @Override
    public String next() {
        currentId++;
        return Integer.toString(currentId);
    }
}