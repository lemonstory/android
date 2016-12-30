package com.xiaoningmeng.utils;

import android.content.Context;

import okhttp3.OkHttpClient;

//https://github.com/facebook/stetho/issues/16

// StethoHelper interface, AKA "NetworkEventReporterImpl"
public interface StethoHelper {
    void init(Context context);

    void configureInterceptor(OkHttpClient httpClient);
}