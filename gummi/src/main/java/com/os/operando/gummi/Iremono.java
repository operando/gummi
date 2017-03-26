package com.os.operando.gummi;

public interface Iremono<I extends RequestType2, O> {

    I in();

    O out();
}