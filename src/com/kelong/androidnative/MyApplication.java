package com.kelong.androidnative;

import android.app.Application;

import com.kelong.utils.ApplicationConfig;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ApplicationConfig.context = this;
    }
}
