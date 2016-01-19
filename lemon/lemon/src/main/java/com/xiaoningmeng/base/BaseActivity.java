package com.xiaoningmeng.base;

import com.umeng.analytics.MobclickAgent;
import com.xiaoningmeng.R;
import com.xiaoningmeng.application.ActivityManager;
import com.xiaoningmeng.application.MyApplication;
import com.xiaoningmeng.http.ILoading;
import com.xiaoningmeng.manager.SystemBarTintManager;
import com.xiaoningmeng.view.dialog.TextDialogLoading;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class BaseActivity extends Activity implements ILoading {

	private ILoading mLoading; // 网络加载状态显示

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityManager.getScreenManager().pushActivity(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 取消所有从该activity中发出的请求，释放线程资源
		if (MyApplication.getInstance() != null) {
			MyApplication.getInstance().getmRequestQueue().cancelAll(this);
		}
		ActivityManager.getScreenManager().popActivity(this);
	}



	public void startOtherActivity(Class<?> clazz) {

		Intent i = new Intent(this, clazz);
		startActivityForNew(i);
	}

	public void startActivityForNew(Intent intent) {
		startActivity(intent);
		animationForNew();
	}

	public void animationForNew() {
		overridePendingTransition(R.anim.main_translatex100to0,
				R.anim.main_translatex0tof100);
	}

	public void animationForOld() {
		overridePendingTransition(R.anim.main_translatexf100to0,
				R.anim.main_translatex0to100);
	}

	public void animationForBottom() {
		overridePendingTransition(R.anim.main_translatey100to0,
				R.anim.main_translatey0tof100);
	}

	public void animationForOTop() {
		overridePendingTransition(R.anim.main_translateyf100to0,
				R.anim.main_translatey0to100);
	}

	@Override
	public void finish() {
		super.finish();
		animationForOld();

	}

	public void oldFinish() {
		super.finish();
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {

		super.startActivityForResult(intent, requestCode);
		animationForNew();
	}

	protected void setLoading(ILoading iLoading) {

		this.mLoading = iLoading;
	}
	
	public void setLoadingTip(String tip) {

		if (mLoading == null) {
			mLoading = new TextDialogLoading(this);
		}
		((TextDialogLoading)mLoading).setLoadingTip(tip);
	}

	@Override
	public void startLoading() {

		if (mLoading == null) {
			mLoading = new TextDialogLoading(this);
		}
		mLoading.startLoading();
	}

	@Override
	public void stopLoading() {

		if (mLoading != null) {
			mLoading.stopLoading();
		}
	}

	@Override
	protected void onResume() {
		MobclickAgent.onResume(this);
		super.onResume();
	}

	@Override
	protected void onPause() {
		MobclickAgent.onPause(this);
		super.onPause();
	}

	protected void setTitleName(String title) {

		((TextView) findViewById(R.id.tv_head_title)).setText(title);
	}

	protected void setRightHeadIcon(int drawableId) {

		ImageView rightIcon = (ImageView) findViewById(R.id.img_head_right);
		rightIcon.setImageResource(drawableId);
		rightIcon.setVisibility(View.VISIBLE);
		View textView = findViewById(R.id.tv_head_right);
		if(textView != null)
			textView.setVisibility(View.INVISIBLE);
	}

	protected void setRightHeadText(String text) {

		TextView rightTv = (TextView) findViewById(R.id.tv_head_right);
		rightTv.setText(text);
		rightTv.setVisibility(View.VISIBLE);
		findViewById(R.id.img_head_right).setVisibility(View.INVISIBLE);
	}

	public void back(View view) {
		finish();
	}
	
	@Override
	public void setContentView(int layoutResID) {
		
		super.setContentView(layoutResID);
		setSystemBar();
	}
	
	@Override
	public void setContentView(View view) {
		
		super.setContentView(view);
		setSystemBar();
	}

	public void setTinitColor(int color){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			SystemBarTintManager tintManager = new SystemBarTintManager(this);
			tintManager.setStatusBarTintEnabled(true);
			tintManager.setNavigationBarTintEnabled(false);
			tintManager.setTintColor(color);
		}
	}
	
	public void setSystemBar() {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			SystemBarTintManager tintManager = new SystemBarTintManager(this);
			tintManager.setStatusBarTintEnabled(true);
			tintManager.setNavigationBarTintEnabled(false);
			tintManager.setTintColor(getResources().getColor(R.color.action_bar_bg));
		}
	}
}
