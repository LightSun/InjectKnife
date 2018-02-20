package com.heaven7.android.injectknife.gradle.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.heaven7.android.injectknife.gradle.sample.test.SimpleTest;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new SimpleTest().onCreate();
    }
}
