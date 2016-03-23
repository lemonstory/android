package com.xiaoningmeng.application;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Process;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.alibaba.sdk.android.AlibabaSDK;
import com.alibaba.sdk.android.callback.InitResultCallback;
import com.alibaba.sdk.android.trade.TradeConfigs;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.tencent.bugly.crashreport.CrashReport;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaoningmeng.bean.AppInfo;
import com.xiaoningmeng.bean.UserInfo;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.http.OSSAuth;
import com.xiaoningmeng.http.OkHttpStack;
import com.xiaoningmeng.http.PersistentCookieStore;

import org.apache.http.client.CookieStore;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpParams;
import org.litepal.LitePalApplication;

import java.io.File;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.List;


/**
 * 
 * @author HuangYanbin
 * 
 */
public class MyApplication extends LitePalApplication {

	private static MyApplication mApplication;
	public DefaultHttpClient mHttpClient;
	private RequestQueue mRequestQueue;
	private boolean mIsFirstRequest = true;
	private boolean mIsLogin;
	private String uid = "";
	public UserInfo userInfo;
	private List<Cookie> cookies;
	private CookieStore cookieStore;

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
		mApplication = this;
		if(shouldInit()) {
			MiPushClient.registerPush(this, Constant.MI_APP_ID, Constant.MI_APP_KEY);
			AppInfo.getInstance();
			OSSAuth.getInstance().init(this);
			initImageLoaderConfig(this);
			CrashReport.initCrashReport(this, "900008353", false);
			initRequestQueue();

			//阿里百川
			TradeConfigs.defaultTaokePid = "mm_10003360_0_0";
			AlibabaSDK.asyncInit(this, new InitResultCallback() {
				@Override
				public void onSuccess() {
//					Toast.makeText(MyApplication.this, "TaeSDK 初始化成功", Toast.LENGTH_SHORT)
//							.show();
				}

				@Override
				public void onFailure(int code, String message) {
//					Log.w("mayongge", "初始化异常，code = " + code + ", info = " + message);
				}
			});
		}
		writeMILog();
	}

/*	private void initRequestQueue() {

		if (mRequestQueue == null) {
			DefaultHttpClient mDefaultHttpClient = new DefaultHttpClient();
			final ClientConnectionManager mClientConnectionManager = mDefaultHttpClient
					.getConnectionManager();
			final HttpParams mHttpParams = mDefaultHttpClient.getParams();
			final ThreadSafeClientConnManager mThreadSafeClientConnManager = new ThreadSafeClientConnManager(
					mHttpParams, mClientConnectionManager.getSchemeRegistry());
			mHttpClient = new DefaultHttpClient(mThreadSafeClientConnManager,
					mHttpParams);
			final HttpStack httpStack = new HttpClientStack(mHttpClient);
			this.mRequestQueue = Volley.newRequestQueue(
					this.getApplicationContext(), httpStack);
		}
	}*/

	private void initRequestQueue() {

		if (mRequestQueue == null) {
			DefaultHttpClient mDefaultHttpClient = new DefaultHttpClient();
			final ClientConnectionManager mClientConnectionManager = mDefaultHttpClient
					.getConnectionManager();
			final HttpParams mHttpParams = mDefaultHttpClient.getParams();
			final ThreadSafeClientConnManager mThreadSafeClientConnManager = new ThreadSafeClientConnManager(
					mHttpParams, mClientConnectionManager.getSchemeRegistry());

			mHttpClient = new DefaultHttpClient(mThreadSafeClientConnManager, mHttpParams);

			File httpCacheDirectory = new File(this.getCacheDir(), "responses");
			int cacheSize = 10 * 1024 * 1024; // 10 MiB
			Cache cache = new Cache(httpCacheDirectory, cacheSize);
			OkHttpClient client = new OkHttpClient();
			CookieManager cookieManager = new CookieManager(new PersistentCookieStore(getApplicationContext()), CookiePolicy.ACCEPT_ALL);
			client.setCookieHandler(cookieManager);
			client.setCache(cache);
			final HttpStack httpStack = new OkHttpStack(client);
			this.mRequestQueue = Volley.newRequestQueue(
					this.getApplicationContext(), httpStack);
		}
	}

	
	public void initImageLoaderConfig(Context context) {

		int memoryCacheSize;
		File cacheDir = StorageUtils.getCacheDirectory(getApplicationContext());
		UnlimitedDiskCache discCache = new UnlimitedDiskCache(cacheDir);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
			int memClass = ((ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE))
					.getMemoryClass();
			memoryCacheSize = (memClass / 6) * 1024 * 1024;
		} else {
			memoryCacheSize = 4 * 1024 * 1024;
		}

		DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(android.R.color.transparent)
				.showImageOnFail(android.R.color.transparent)
				.cacheInMemory(true).cacheOnDisk(true).build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPoolSize(5)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.memoryCacheSize(memoryCacheSize)
				.memoryCache(new LRULimitedMemoryCache(memoryCacheSize))
				.denyCacheImageMultipleSizesInMemory().diskCache(discCache)
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.diskCacheSize(50 * 1024 * 1024)
				/*.imageDownloader(new LImageDownaloder(this))*/
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.defaultDisplayImageOptions(displayImageOptions)
				.writeDebugLogs().build();
		ImageLoader.getInstance().init(config);
	}

	private void writeMILog(){
		//打开Log
		LoggerInterface newLogger = new LoggerInterface() {

			@Override
			public void setTag(String tag) {
				// ignore
			}

			@Override
			public void log(String content, Throwable t) {
				Log.d("huang", content, t);
			}

			@Override
			public void log(String content) {
				Log.d("huang", content);
			}
		};
		Logger.setLogger(this, newLogger);
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

	/**
	 * 得到请求队列
	 * 
	 * @return请求队列
	 */
	public RequestQueue getmRequestQueue() {

		return mRequestQueue;
	}

	public boolean isFirstRequest() {

		return mIsFirstRequest;
	}

	public void setIsFirstRequest(boolean isFirstRequest) {

		this.mIsFirstRequest = isFirstRequest;
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

	/**
	 * 通过token设置cookie
	 * 
	 * @param cookieStore
	 */
	public void setCookieFromToken(CookieStore cookieStore) {

		mHttpClient.setCookieStore(cookieStore);
		setClientCookieFromHttpClient();

	}

	/**
	 * httpClient的cookie传给Client
	 */
	public void setClientCookieFromHttpClient() {
		
		cookieStore = mHttpClient.getCookieStore();
		cookies = cookieStore.getCookies();

	}

	public void removeClientCookieFromHttpClient() {
		mHttpClient.setCookieStore(null);
	}

	public List<Cookie> getCookies() {

		return cookies;
	}

}
