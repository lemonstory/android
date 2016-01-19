package com.xiaoningmeng.base;

import com.xiaoningmeng.R;
import com.xiaoningmeng.application.ActivityManager;
import com.xiaoningmeng.http.ILoading;
import com.xiaoningmeng.manager.SystemBarTintManager;
import com.xiaoningmeng.view.dialog.TextDialogLoading;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class BaseFragmentActivity extends FragmentActivity implements ILoading {

	private ILoading mLoading; // 网络加载状态显示
	protected boolean isLandscape;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		ActivityManager.getScreenManager().pushActivity(this);
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			isLandscape = true;
		} else {
			isLandscape = false;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ActivityManager.getScreenManager().popActivity(this);
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

	public void removeFragment(Fragment fragment) {
		getSupportFragmentManager().beginTransaction().remove(fragment)
				.commit();
	}

	@Override
	public void finish() {
		super.finish();
		animationForOld();
	}

	public void finish2() {
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

	public void startOtherActivity(Class<?> clazz) {

		Intent i = new Intent(this, clazz);
		startActivityForNew(i);
	}

	public void oldFinish() {
		super.finish();
	}

	protected void setTitleName(String title) {

		((TextView) findViewById(R.id.tv_head_title)).setText(title);
	}

	protected void setRightHeadIcon(int drawableId) {

		ImageView rightIcon = (ImageView) findViewById(R.id.img_head_right);
		rightIcon.setImageResource(drawableId);
		rightIcon.setVisibility(View.VISIBLE);
		findViewById(R.id.tv_head_right).setVisibility(View.INVISIBLE);
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
	
	@Override
	public void setContentView(View view) {
		
		super.setContentView(view);
		setSystemBar();
	}
}
