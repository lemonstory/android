package com.xiaoningmeng.fragment;

import java.util.HashMap;
import java.util.List;
import org.apache.http.cookie.Cookie;

import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.xiaoningmeng.AblumDetailActivity;
import com.xiaoningmeng.R;
import com.xiaoningmeng.WebViewActivity;
import com.xiaoningmeng.application.MyApplication;
import com.xiaoningmeng.base.BaseFragment;
import com.xiaoningmeng.base.BaseFragmentActivity;
import com.xiaoningmeng.bean.AppInfo;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.utils.NetUtils;

public class CricleFragment extends BaseFragment{


	private BaseFragmentActivity mContext;
	private WebView webView;
	private HashMap<String, String> headers;
	private ViewGroup loadingView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = (BaseFragmentActivity) getActivity();
		View contentView = View.inflate(mContext,R.layout.fragment_circle, null);
		webView = (WebView) contentView.findViewById(R.id.webView);
		settingWebView();
		reRequestLoading(contentView);
		webView.loadUrl(Constant.Cricle_URL, headers);
		return contentView;
	}


	private void settingWebView() {
		WebSettings webseting = webView.getSettings();
		webseting.setJavaScriptEnabled(true);
		webseting.setAllowFileAccess(true);
		webseting.setAppCacheEnabled(true);
		webseting.setDomStorageEnabled(true);
		// 增加缓存文件路径
		webseting.setAppCacheMaxSize(1024 * 1024 * 8);// 设置缓冲大小 8M
		String appCacheDir = mContext.getApplicationContext()
				.getDir("cache", Context.MODE_PRIVATE).getPath();
		webseting.setAppCachePath(appCacheDir);
		headers = new HashMap<>();
		headers.put("FROM", "mobile");
		headers.put("userAgent", AppInfo.getInstance().getUAStr());
		// 如果有网则先从网络上获取缓存
		if (NetUtils.isNetworkConnected(mContext)) {
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

				synCookies(mContext, url);
				//view.loadUrl(url, headers);
				WebViewActivity.openWebView(mContext,url);
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
				if(progress ==100){
					reqeuestSuccess();
				}
			}

			/**
			 * 设置页面标题
			 */
			@Override
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);
			}

			/**
			 * 增大缓存
			 */
			@Override
			@Deprecated
			public void onReachedMaxAppCacheSize(long requiredStorage,
												 long quota, WebStorage.QuotaUpdater quotaUpdater) {
				quotaUpdater.updateQuota(requiredStorage * 2);
			}
		});
	}

	public static void synCookies(Context context, String url) {
		if (MyApplication.getInstance().isIsLogin()) {
			CookieManager cookieManager = CookieManager.getInstance();
			cookieManager.setAcceptCookie(true);
			try {
				List<Cookie> cookies = MyApplication.getInstance().getCookies();
				for (int i = 0; i < cookies.size(); i++) { //
					cookieManager.setCookie(url, cookies.get(i).getName() + "="
							+ cookies.get(i).getValue()
							+ "; Domain=.xiaoningmeng.net; ");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			CookieManager cookieManager = CookieManager.getInstance();
			cookieManager.removeAllCookie();
		}

	}

	private void reRequestLoading(View contentView){

		if(loadingView == null){
			loadingView = (ViewGroup) (contentView == null?getView():contentView).findViewById(R.id.rl_loading);
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
		if(getView() == null){
			return;
		}
		if(loadingView == null){
			loadingView = (ViewGroup)getView().findViewById(R.id.rl_loading);
		}
		loadingView.setVisibility(View.VISIBLE);
		((TextView)loadingView.getChildAt(0)).setText("请连接网络后点击屏幕重试");
		loadingView.getChildAt(1).setVisibility(View.INVISIBLE);
		loadingView.setClickable(true);
		loadingView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				reRequestLoading(null);
				webView.loadUrl(Constant.Cricle_URL, headers);
			}
		});
	}

}
