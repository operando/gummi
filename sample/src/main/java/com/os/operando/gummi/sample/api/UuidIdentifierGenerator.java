package com.os.operando.gummi.sample.api;

import com.os.operando.gummi2.RequestIdentifierGenerator;

import java.util.UUID;

public class UuidIdentifierGenerator implements RequestIdentifierGenerator {

    private final String uuid = UUID.randomUUID().toString();
    private long currentIdentifier = 0;

    @Override
    public String next() {
        currentIdentifier++;
        return new StringBuilder().append(uuid).append("-").append(currentIdentifier).toString();
    }
}
