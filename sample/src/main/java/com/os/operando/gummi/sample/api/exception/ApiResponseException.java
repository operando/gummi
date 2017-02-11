package com.os.operando.gummi.sample.api.exception;

import lombok.RequiredArgsConstructor;
import okhttp3.ResponseBody;

@RequiredArgsConstructor
public class ApiResponseException extends Exception {

    private final int statusCode;
    private final String message;
    private final ResponseBody responseBody;
}
