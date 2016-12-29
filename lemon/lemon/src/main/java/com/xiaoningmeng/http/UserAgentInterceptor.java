package com.xiaoningmeng.http;

import com.xiaoningmeng.bean.AppInfo;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by gaoyong on 2016/12/27.
 */

public class UserAgentInterceptor implements Interceptor {

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request original = chain.request();

        // Request customization: add request headers
        Request.Builder requestBuilder = original.newBuilder()
                .header("User-Agent", AppInfo.getInstance().getUAStr()); // <-- this is the important line

        Request request = requestBuilder.build();
        return chain.proceed(request);
    }
}
