package com.xiaoningmeng;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.facebook.common.util.UriUtil;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebStorage.QuotaUpdater;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.xiaoningmeng.application.ActivityManager;
import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.bean.ShareBean;
import com.xiaoningmeng.manager.DownloadApkManager;
import com.xiaoningmeng.utils.NetUtils;
import com.xiaoningmeng.view.ShareDialog;

//import com.r0adkll.slidr.Slidr;

public class WebViewActivity extends BaseActivity {

    private WebView webView;
    private String webUrl;
    private ProgressBar progressBar;
    private ImageView mShareImg;
    private String mPageTitle = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        setRightHeadIcon(R.drawable.btn_player_share_normal);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        webView = (WebView) findViewById(R.id.webView);
        progressBar = (ProgressBar) findViewById(R.id.myProgressBar);
        settingWebView();
        mShareImg = (ImageView) this.findViewById(R.id.img_head_right);
        mShareImg.setOnClickListener(shareImgOnClick);
        webUrl = getIntent().getStringExtra("web_url");
        if (null == webUrl) {
            webUrl = String.valueOf(getIntent().getData());
        }
        reRequestLoading();
        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().sync();
        //synCookies(WebViewActivity.this, url);
        webView.loadUrl(webUrl/*, headers*/);

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

        //http://blog.csdn.net/zhyh1986/article/details/42169159
        webView.setWebViewClient(new WebViewClient() {
            /**
             * 点击Url是否重载 返回true表示在webView加载
             */
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                Uri uri = Uri.parse(url);
                String filename = uri.getLastPathSegment();
                if (UriUtil.isNetworkUri(uri)){

                    if(null != filename && filename.endsWith(".apk")) {
                        DownloadApkManager.getInstance().showDownloadDialog(WebViewActivity.this, url);
                    }
                    return false;
                }

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
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
            @Override
            public void onProgressChanged(WebView view, int newProgress) {

                if (newProgress == 100) {
                    reqeuestSuccess();
                    progressBar.setVisibility(View.GONE);
                } else {
                    if (View.INVISIBLE == progressBar.getVisibility()) {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                    progressBar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }

            /**
             * 设置页面标题
             */
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (!TextUtils.isEmpty(title)) {
                    mPageTitle = title;
                    WebViewActivity.this.setTitleName(mPageTitle);
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

    private void reRequestLoading() {

    }

    private void reqeuestSuccess() {

    }

    private void onFailure() {

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack()) {
                webView.goBack();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void finish() {

        if (webView.canGoBack()) {

            webView.goBack();

        } else {

            if (ActivityManager.getScreenManager().getActivity(HomeActivity.class) == null) {
                startActivityForNew(new Intent(this, HomeActivity.class));
            }
            super.finish();
        }
    }


    private View.OnClickListener shareImgOnClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            ShareBean shareBean = new ShareBean(mPageTitle, "", "", "", webUrl);
            new ShareDialog().show(WebViewActivity.this, shareBean);
        }
    };
}