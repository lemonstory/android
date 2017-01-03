package com.xiaoningmeng.utils;

import android.content.Context;

import okhttp3.OkHttpClient;

public class FakeStethoHelper implements StethoHelper {

    @Override
    public void init(Context context) {
        // Noop
    }

    @Override
    public void configureInterceptor(OkHttpClient.Builder httpClientBuilder) {
        // Noop
    }

}