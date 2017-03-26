package com.os.operando.gummi.sample;

import com.os.operando.gummi.Iremono;
import com.os.operando.gummi.RequestType2;
import com.os.operando.gummi.sample.api.TestResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MogeMoge implements Iremono<MogeMoge.Moge, TestResponse> {

    private final Moge moge;

    @Override
    public Moge in() {
        return moge;
    }

    @Override
    public TestResponse out() {
        return null;
    }

    public static class Moge implements RequestType2 {

        @Override
        public String getMethod() {
            return "moge";
        }
    }
}
