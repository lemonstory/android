package com.xiaoningmeng.base;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.xiaoningmeng.R;
import com.xiaoningmeng.application.ActivityManager;
import com.xiaoningmeng.http.ILoading;
import com.xiaoningmeng.view.dialog.TextDialogLoading;
import com.zhy.http.okhttp.OkHttpUtils;

public class BaseActivity extends AppCompatActivity implements ILoading {

	private ILoading mLoading; // 网络加载状态显示

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityManager.getScreenManager().pushActivity(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		OkHttpUtils.getInstance().cancelTag(this);
		ActivityManager.getScreenManager().popActivity(this);
	}



	public void startOtherActivity(Class<?> clazz) {

		Intent i = new Intent(this, clazz);
		startActivityForNew(i);
	}

	public void startActivityForNew(Intent intent) {


	/*	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Fade fade = new Fade();
			fade.setDuration(300);
			getWindow().setExitTransition(fade);
			startActivity(intent,ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle());
		}else{*/
			startActivity(intent);
			animationForNew();
		/*}*/
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

			Context context = this;
			if (this.isFinishing()) {
				context = this.getParent();
			}
			if (context != null) {
				mLoading = new TextDialogLoading(context);
				mLoading.startLoading();
			}
		}
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

	//隐藏状态栏
	public void setStatusBarHide(){

		if(Build.VERSION.SDK_INT >= 19) {
			View decorView = getWindow().getDecorView();
			decorView.setSystemUiVisibility(
					View.SYSTEM_UI_FLAG_LAYOUT_STABLE
							| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
							| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
							| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
							| View.SYSTEM_UI_FLAG_FULLSCREEN
							| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
		}
	}

	/**
	 * 提供转场动画
	 * @param intent
	 * @param firstSharedView
	 * @param shareName
     */
	public void startShareTransitionActivity(Intent intent,View firstSharedView,String shareName){

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
					firstSharedView,shareName);
			Bundle bundle = activityOptions.toBundle();
			startActivity(intent, bundle);
		}else{
			startActivityForNew(intent);
		}
	}

	protected View getListEndView() {
		LayoutInflater layoutInflater = this.getLayoutInflater();
		View view = layoutInflater.inflate(R.layout.list_end_view, null);
		//view.findViewById(R.id.tv).setVisibility(View.GONE);
		view.setLayoutParams(new DrawerLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getParent(), "click View", Toast.LENGTH_LONG).show();
			}
		});
		return view;
	}

}
