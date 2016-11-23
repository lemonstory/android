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
import com.squareup.leakcanary.LeakCanary;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.bugly.crashreport.CrashReport.UserStrategy;
import com.umeng.socialize.PlatformConfig;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaoningmeng.bean.AppInfo;
import com.xiaoningmeng.bean.UserInfo;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.http.CacheInterceptor;
import com.xiaoningmeng.http.OSSAuth;
import com.xiaoningmeng.player.MusicService;
import com.xiaoningmeng.utils.AppUtils;
import com.xiaoningmeng.utils.DebugUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.cookie.CookieJarImpl;
import com.zhy.http.okhttp.cookie.store.PersistentCookieStore;
import com.zhy.http.okhttp.log.LoggerInterceptor;

import org.litepal.LitePalApplication;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

/**
 * @author HuangYanbin
 */
public class MyApplication extends LitePalApplication implements ServiceConnection {

    private static MyApplication mApplication;
    private boolean mIsLogin;
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
        super.onCreate();
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

            //https://bugly.qq.com/docs/user-guide/instruction-manual-android/?v=20161115202144
            //Bugly上报进程的策略配置
            Context context = getApplicationContext();
            String packageName = context.getPackageName();
            String processName = AppUtils.getProcessName(android.os.Process.myPid());
            UserStrategy strategy = new UserStrategy(context);
            strategy.setUploadProcess(processName == null || processName.equals(packageName));

            //Bugly上报进程的策略配置
            CrashReport.initCrashReport(getApplicationContext(), strategy);

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
        }
        startMusicService();
    }

    //参考文档:https://github.com/hongyangAndroid/okhttputils
    public void initOkHttpClient() {

        File cacheFile = new File(getExternalCacheDir(), "HttpCache");
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 50); //50Mb
        CookieJarImpl cookieJar = new CookieJarImpl(new PersistentCookieStore(getApplicationContext()));
        CacheInterceptor cacheInterceptor = new CacheInterceptor();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .addInterceptor(new LoggerInterceptor(DebugUtils.TAG))
                .addNetworkInterceptor(cacheInterceptor)
                .addInterceptor(cacheInterceptor)
                .cache(cache)
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit
                        .MILLISECONDS)
                //其他配置
                .build();

        OkHttpUtils.initClient(okHttpClient);
    }


//	private void writeMILog(){
//		//打开Log
//		LoggerInterface newLogger = new LoggerInterface() {
//
//			@Override
//			public void setTag(String tag) {
//				// ignore
//			}
//
//			@Override
//			public void log(String content, Throwable t) {
//				Log.d("huang", content, t);
//			}
//
//			@Override
//			public void log(String content) {
//				Log.d("huang", content);
//			}
//		};
//		Logger.setLogger(this, newLogger);
//	}

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


    public boolean isIsLogin() {
        return mIsLogin;
    }

    public void setIsLogin(boolean mIsLogin) {

        this.mIsLogin = mIsLogin;
    }

    public String getUid() {

        if ((uid == null || uid.equals("")) && userInfo != null) {
            uid = userInfo.getUid();
        }
        return uid;
    }

    public void setUserInfo(UserInfo userInfo) {
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


    /**
     * 通过token设置cookie
     *
     * @param cookieStore
     *//*
    public void setCookieFromToken(CookieStore cookieStore) {

		mHttpClient.setCookieStore(cookieStore);
		setClientCookieFromHttpClient();

	}

	*//**
     * httpClient的cookie传给Client
     *//*
	public void setClientCookieFromHttpClient() {
		
		cookieStore = mHttpClient.getCookieStore();
		cookies = cookieStore.getCookies();

	}

	public void removeClientCookieFromHttpClient() {
		mHttpClient.setCookieStore(null);
	}*/


}
