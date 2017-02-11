package com.os.operando.gummi.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.os.operando.gummi.R;
import com.os.operando.gummi.RequestType;
import com.os.operando.gummi.sample.api.RxApiClient;

import lombok.ToString;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RxApiClient rxApiClient = new RxApiClient();
        rxApiClient.responseFrom(new TestService(), new TestService())
                .flatMap(pair -> rxApiClient.responseFrom(new TestService(), new TestService()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pair -> {
                    Log.d(TAG, pair.getFirst().toString());
                    Log.d(TAG, pair.getSecond().toString());
                });
    }

    class TestService extends RequestType<TestResponse> {

        @Override
        public String getMethod() {
            return "TestService.Test";
        }
    }

    @ToString
    class TestResponse {
        String test;
    }
}
