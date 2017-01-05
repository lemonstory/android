package com.xiaoningmeng.application;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Process;
import android.support.multidex.MultiDex;

import com.alibaba.baichuan.android.trade.AlibcTradeSDK;
import com.alibaba.baichuan.android.trade.callback.AlibcTradeInitCallback;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.squareup.leakcanary.LeakCanary;
import com.tencent.bugly.Bugly;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsListener;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaoningmeng.BuildConfig;
import com.xiaoningmeng.auth.UserAuth;
import com.xiaoningmeng.bean.AppInfo;
import com.xiaoningmeng.bean.UserInfo;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.http.CacheInterceptor;
import com.xiaoningmeng.http.OSSAuth;
import com.xiaoningmeng.http.UserAgentInterceptor;
import com.xiaoningmeng.player.MusicService;
import com.xiaoningmeng.utils.AppUtils;
import com.xiaoningmeng.utils.DebugUtils;

import org.litepal.LitePalApplication;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * @author HuangYanbin
 */
public class MyApplication extends LitePalApplication implements ServiceConnection {

    private static MyApplication mApplication;
    private String uid = "";
    public UserInfo userInfo;


    public static MyApplication getInstance() {

        return mApplication;
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {

        AppUtils.enableStrictMode();
        super.onCreate();
        if (BuildConfig.DEBUG) {
            BuildConfig.STETHO.init(this);
        }
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        // Normal app init code...
        mApplication = this;
        if (shouldInit()) {
            MiPushClient.registerPush(this, Constant.MI_APP_ID, Constant.MI_APP_KEY);
            AppInfo.getInstance();
            Fresco.initialize(this);
            OSSAuth.getInstance().init(this);

            //https://bugly.qq.com/docs/user-guide/instruction-manual-android-upgrade/
            Bugly.init(getApplicationContext(), "900008353", false);

            initOkHttpClient();
            //阿里百川
            AlibcTradeSDK.asyncInit(this, new AlibcTradeInitCallback() {
                @Override
                public void onSuccess() {
                    //初始化成功，设置相关的全局配置参数

//                    Toast.makeText(MyApplication.this, "TaeSDK 初始化成功", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int code, String msg) {
                    //初始化失败，可以根据code和msg判断失败原因，详情参见错误说明
//                    String content = "初始化异常，code = " + code + ", info = " + msg;
//                    Toast.makeText(MyApplication.this, content, Toast.LENGTH_SHORT).show();
                }
            });

            //umeng初始化各平台配置
            //微信 appid appsecret
            PlatformConfig.setWeixin(Constant.WEI_XIN_APP_ID, Constant.WEIN_XIN_APP_SECRET);
            //新浪微博 appkey appsecret
            PlatformConfig.setSinaWeibo(Constant.WEIBO_APP_ID, Constant.WEIN_XIN_APP_SECRET);
            // QQ和Qzone appid appkey
            PlatformConfig.setQQZone(Constant.QQ_APP_ID, Constant.QQ_APP_KEY);
            UMShareAPI.get(this);
            Config.REDIRECT_URL = Constant.SINA_OAUTH_CALLBACK;
            MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);// 设置统计场景类型为普通统计
        }


        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        //TbsDownloader.needDownload(this, false);
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub
//                DebugUtils.e( "onViewInitFinished is " + arg0);
            }

            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub
//                DebugUtils.e( "onCoreInitFinished ");

            }
        };
        QbSdk.setTbsListener(new TbsListener() {
            @Override
            public void onDownloadFinish(int i) {
//                DebugUtils.d("onDownloadFinish");
            }

            @Override
            public void onInstallFinish(int i) {
//                DebugUtils.d("onInstallFinish");
            }

            @Override
            public void onDownloadProgress(int i) {
//                DebugUtils.d("onDownloadProgress:"+i);
            }
        });
        QbSdk.initX5Environment(this, cb);


        //OPPO A53M报错
        //相关文档：
        //https://bugly.qq.com/v2/crash/apps/900008353/issues/4751?pid=1
        //https://github.com/Coolerfall/Android-AppDaemon/issues/11
        //http://bbs.coloros.com/thread-174655-1-1.html
        try {
            startMusicService();
        } catch (RuntimeException e) {
            DebugUtils.e(e.toString());
        }

        UserInfo loginUserInfo = UserAuth.getInstance().getLoginUserInfo(this);
        if (UserAuth.getInstance().isLogin(this)) {

            if (null != loginUserInfo) {
                this.setLoginUserInfo(loginUserInfo);
            } else {

                //TODO:如果用户已登录但是没有用户信息，则在走一遍登录流程(不合理)
                UserAuth.getInstance().clearLoginUserInfo(this);
            }
        }
    }

    public OkHttpClient initOkHttpClient() {

        File cacheFile = new File(getExternalCacheDir(), "HttpCache");
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 50); //50Mb
        ClearableCookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(this));
        UserAgentInterceptor userAgentInterceptor = new UserAgentInterceptor();
        CacheInterceptor cacheInterceptor = new CacheInterceptor();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        if (!BuildConfig.DEBUG) {
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        }

        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .addInterceptor(logging)
                .addNetworkInterceptor(cacheInterceptor)
                .addInterceptor(userAgentInterceptor)
                .addInterceptor(cacheInterceptor)
                .cache(cache)
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS);

        if (BuildConfig.DEBUG) {
            BuildConfig.STETHO.configureInterceptor(okHttpClientBuilder);
        }
        OkHttpClient okHttpClient = okHttpClientBuilder.build();
        return okHttpClient;
    }

    private boolean shouldInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    public String getLoginUid() {

        if ((uid == null || uid.equals("")) && userInfo != null) {
            uid = userInfo.getUid();
        }
        return uid;
    }

    public void setLoginUserInfo(UserInfo userInfo) {

        this.userInfo = userInfo;
        if (userInfo == null) {
            this.uid = null;
        } else {
            this.uid = userInfo.getUid();
        }
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder service) {
        if (service instanceof MusicService.ServiceBinder) {
            MusicService.ServiceBinder binder = (MusicService.ServiceBinder) service;
        }

    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

    }

    public void startMusicService() {
        Intent it = new Intent(this, MusicService.class);
        startService(it);
        bindMusicService();
    }

    public void stopMusicService() {
        Intent it = new Intent(this, MusicService.class);
        stopService(it);
    }

    private void bindMusicService() {
        Intent it = new Intent(this, MusicService.class);
        this.bindService(it, this, Service.BIND_AUTO_CREATE);
    }

    //TODO:http://stackoverflow.com/questions/12277673/android-services-error-service-not-registered/16713212#16713212
    public void unbindMusicService() {
        this.unbindService(this);
//		stopMusicService();
    }
}
