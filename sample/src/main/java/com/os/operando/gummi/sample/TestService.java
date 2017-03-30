package com.os.operando.gummi.sample;

import com.os.operando.gummi.RequestType;
import com.os.operando.gummi.sample.model.TestResponse;

public class TestService extends RequestType<TestResponse> {

    @Override
    public String getMethod() {
        return "TestService.Test";
    }

    @Override
    public Class<TestResponse> getResponseType() {
        return TestResponse.class;
    }
}