package com.os.operando.gummi.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.os.operando.gummi.R;
import com.os.operando.gummi.sample.api.RxApiClient;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.api).setOnClickListener(v -> {
            RxApiClient rxApiClient = new RxApiClient();
            rxApiClient.responseFrom(new TestService(), new TestService())
                    .flatMap(a -> rxApiClient.responseFrom(new TestService(), new TestService()))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(pair -> {
                        Log.d(TAG, pair.getFirst().toString());
                        Log.d(TAG, pair.getSecond().toString());
                    }, Throwable::printStackTrace);
        });
    }
}
