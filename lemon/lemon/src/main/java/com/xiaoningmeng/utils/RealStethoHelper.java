package com.xiaoningmeng.utils;

import android.content.Context;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import okhttp3.OkHttpClient;

public class RealStethoHelper implements StethoHelper {

    @Override
    public void init(Context context) {
        Stetho.initialize(
                Stetho.newInitializerBuilder(context)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(context))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(context))
                        .build());
    }

    @Override
    public void configureInterceptor(OkHttpClient httpClient) {
        httpClient.networkInterceptors().add(new StethoInterceptor());
    }

}
