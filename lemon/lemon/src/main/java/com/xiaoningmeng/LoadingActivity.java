package com.xiaoningmeng;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.xiaoningmeng.auth.UserAuth;
import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.download.DownLoadClientImpl;
import com.xiaoningmeng.download.DownloadNotificationManager;
import com.xiaoningmeng.manager.UploadManager;
import com.xiaoningmeng.player.PlayerManager;

public class LoadingActivity extends BaseActivity {

	private Handler mHandler = new Handler();
	public static final int LOGIN_TIME = 800;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);
		//setTinitColor(Color.parseColor("#f0f0f0"));
		
		UserAuth.getInstance().authUser(this);
		DownLoadClientImpl.getInstance();// 初始化下载器
		PlayerManager.getInstance(); // 初始化音乐播放器
		DownloadNotificationManager.getInstance();//初始化下载通知栏
		UploadManager.getInstance().uploadRecord();
//		int loadCountDown = PreferenceUtil.getInt("load_countdown");
//		if(loadCountDown <3){
//			PreferenceUtil.putInt("load_countdown",loadCountDown+1);
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					Intent i = new Intent(LoadingActivity.this,HomeActivity.class);
					startActivity(i);
					oldFinish();
					overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
				}
			}, LOGIN_TIME);
//		}else{
//			//-- baidu ad start
//			loadAd();
//			//-- baidu ad end
//		}
	}

	private void loadAd() {
//		RelativeLayout adsParent = (RelativeLayout) this
//				.findViewById(R.id.adsRl);
//		SplashAdListener listener=new SplashAdListener() {
//			@Override
//			public void onAdDismissed() {
				jumpWhenCanClick();// 跳转至您的应用主界面
//			}
//
//			@Override
//			public void onAdFailed(String arg0) {
//				jump();
//			}
//
//			@Override
//			public void onAdPresent() {
//			}
//
//			@Override
//			public void onAdClick() {
//				//WebViewActivity.openWebView(this, url);
//			}
//		};
//		new SplashAd(this, adsParent, listener, Constant.BAIDU_AD_LOAD_ID, true);
		
	}
	
	/**
	 * 当设置开屏可点击时，需要等待跳转页面关闭后，再切换至您的主窗口。故此时需要增加waitingOnRestart判断。
	 * 另外，点击开屏还需要在onRestart中调用jumpWhenCanClick接口。
	 */
	public boolean waitingOnRestart=false;
	private void jumpWhenCanClick() {
		if(this.hasWindowFocus()||waitingOnRestart){
			Intent i = new Intent(LoadingActivity.this,/*UserAuth.getInstance().isFirst() ?GuideActivity.class:*/HomeActivity.class);
			startActivity(i);
			oldFinish();
			overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
		}else{
			waitingOnRestart=true;
		}
		
	}
	
	/**
	 * 不可点击的开屏，使用该jump方法，而不是用jumpWhenCanClick
	 */
	private void jump() {
		Intent i = new Intent(LoadingActivity.this,HomeActivity.class);
		startActivity(i);
		oldFinish();
		overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		if(waitingOnRestart){
			jumpWhenCanClick();
		}
	}

	@Override
	public void onBackPressed() {
		oldFinish();
	}
}
