package com.xiaoningmeng;

import java.util.HashMap;
import java.util.List;

import org.apache.http.cookie.Cookie;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebStorage.QuotaUpdater;
import android.widget.TextView;

//import com.r0adkll.slidr.Slidr;
import com.xiaoningmeng.application.ActivityManager;
import com.xiaoningmeng.application.MyApplication;
import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.bean.AppInfo;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.utils.NetUtils;

public class WebViewActivity extends BaseActivity {

	private WebView webView;
	//private HashMap<String, String> headers;
	private ViewGroup loadingView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);
		webView = (WebView) findViewById(R.id.webView);
		settingWebView();
		String webUrl = getIntent().getStringExtra("web_url");
		reRequestLoading();
		webView.loadUrl(webUrl/*, headers*/);
		/*webView.setOnKeyListener(new View.OnKeyListener(){

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					if(keyCode == KeyEvent.KEYCODE_BACK &&webView.canGoBack()){
						webView.goBack();
						return true;
					}
				}
				return false;
			}
		});*/
	}

	// 调用此方法打开webView
	public static void openWebView(Context context, String url) {

		Intent i = new Intent(context, WebViewActivity.class);
		i.putExtra("web_url", url);
		context.startActivity(i);
	}

	// 调用此方法打开webView
	public static void openNotifyWebView(Context context, String url) {

		Intent i = new Intent(context, WebViewActivity.class);
		i.putExtra("web_url", url);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);
	}

	private void settingWebView() {
		WebSettings webseting = webView.getSettings();
		webseting.setJavaScriptEnabled(true);
		webseting.setAllowFileAccess(true);
		webseting.setAppCacheEnabled(true);
		webseting.setDomStorageEnabled(true);
		// 增加缓存文件路径
		webseting.setAppCacheMaxSize(1024 * 1024 * 8);// 设置缓冲大小 8M
		String appCacheDir = this.getApplicationContext()
				.getDir("cache", Context.MODE_PRIVATE).getPath();
		webseting.setAppCachePath(appCacheDir);
		//headers = new HashMap<>();
		//headers.put("FROM", "mobile");
		//headers.put("userAgent",AppInfo.getInstance().getUAStr());
		// 如果有网则先从网络上获取缓存
		if (NetUtils.isNetworkConnected(this)) {
			webseting.setCacheMode(WebSettings.LOAD_DEFAULT);
		} else {
			// 没有网则从本地读取缓存
			webseting.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		}

		webView.setWebViewClient(new WebViewClient() {
			/**
			 * 点击Url是否重载 返回true表示在webView加载
			 */
			public boolean shouldOverrideUrlLoading(WebView view, String url) {

				//synCookies(WebViewActivity.this, url);
				view.loadUrl(url/*, headers*/);
				return true;
			}

			/**
			 * 返回错误 加载本地数据
			 */
			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				onFailure();
			}
		});

		webView.setWebChromeClient(new WebChromeClient() {

			/**
			 * 监听进度变化
			 */
			public void onProgressChanged(WebView view, int progress) {

				if (progress == 100) {
					reqeuestSuccess();
				}

			}

			/**
			 * 设置页面标题
			 */
			@Override
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);
				if (!TextUtils.isEmpty(title)) {
					WebViewActivity.this.setTitleName(title);
				}
			}

			/**
			 * 增大缓存
			 */
			@Override
			@Deprecated
			public void onReachedMaxAppCacheSize(long requiredStorage,
					long quota, QuotaUpdater quotaUpdater) {
				quotaUpdater.updateQuota(requiredStorage * 2);
			}
		});
	}

	/*public static void synCookies(Context context, String url) {
		if (MyApplication.getInstance().isIsLogin()) {
			CookieManager cookieManager = CookieManager.getInstance();
			cookieManager.setAcceptCookie(true);
			try {
				List<Cookie> cookies = MyApplication.getInstance().getCookies();
				for (int i = 0; i < cookies.size(); i++) { //
					cookieManager.setCookie(url, cookies.get(i).getName() + "="
							+ cookies.get(i).getValue()
							+ "; Domain=.xiaoningmeng.com; ");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			CookieManager cookieManager = CookieManager.getInstance();
			cookieManager.removeAllCookie();
		}

	}*/

	private void reRequestLoading(){

		if(loadingView == null){
			loadingView = (ViewGroup)findViewById(R.id.rl_loading);
		}
		loadingView.setClickable(false);
		loadingView.setVisibility(View.VISIBLE);
		((TextView)loadingView.getChildAt(0)).setText("正在努力加载中");
		loadingView.getChildAt(1).setVisibility(View.VISIBLE);
	}

	private void reqeuestSuccess(){

		loadingView.setVisibility(View.INVISIBLE);
		loadingView.setClickable(false);
	}

	private void onFailure(){

		if(loadingView == null){
			loadingView = (ViewGroup)findViewById(R.id.rl_loading);
		}
		loadingView.setVisibility(View.VISIBLE);
		((TextView)loadingView.getChildAt(0)).setText("请连接网络后点击屏幕重试");
		loadingView.getChildAt(1).setVisibility(View.INVISIBLE);
		loadingView.setClickable(true);
		loadingView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				reRequestLoading();
				webView.loadUrl(Constant.Cricle_URL/*, headers*/);
			}
		});
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
			webView.goBack(); // goBack()表示返回WebView的上一页面
			return true;
		}
		return super.onKeyDown(keyCode,event);
	}


	@Override
	public void finish() {
		if(ActivityManager.getScreenManager().getActivity(HomeActivity.class) == null){
			startActivityForNew(new Intent(this,HomeActivity.class));
		}
		super.finish();
	}
}
