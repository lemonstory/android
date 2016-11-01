package com.xiaoningmeng;

import java.lang.reflect.Field;

import com.xiaoningmeng.application.ActivityManager;
import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.fragment.WeclomeFragment;
import com.xiaoningmeng.view.CricleIndicator;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.EdgeEffectCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import org.w3c.dom.Text;

public class GuideActivity extends BaseActivity implements
		OnClickListener {

	private ViewPager mViewPager;
	private CricleIndicator indicator;
	public static final int PAGE_COUNT = 2; // 页数
	private EdgeEffectCompat rightEdge;
	private TextView mRightTv;
	private boolean isBrithEdit;
	public  boolean isGenderEdit;
	private int pageArg0 =0;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(R.style.PickTheme);
		setContentView(R.layout.activity_guide);
		indicator = (CricleIndicator) findViewById(R.id.indicator);
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mRightTv = (TextView) findViewById(R.id.tv_head_right);
		mViewPager.setOnPageChangeListener(listener);
		setTitleName("设置生日");
		try {
			Field rightEdgeField = mViewPager.getClass().getDeclaredField("mRightEdge");
			if (rightEdgeField != null) {
				rightEdgeField.setAccessible(true);
				rightEdge = (EdgeEffectCompat) rightEdgeField.get(mViewPager);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		mViewPager.setAdapter(new TabFragmentPagerAdapter(getSupportFragmentManager()));
	}

	private OnPageChangeListener listener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int arg0) {
			indicator.setCurrentPosition(arg0 % PAGE_COUNT);
			indicator.setVisibility(arg0 == 2 ? View.INVISIBLE : View.VISIBLE);
			setTitleName(arg0 == 0 ? "设置生日" : "设置性别");
			pageArg0  = arg0;
			setRightTvStatus();
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			if (rightEdge != null && !rightEdge.isFinished()) {
				if(ActivityManager.getScreenManager().getActivity(HomeActivity.class)== null){
					startActivityForNew(new Intent(GuideActivity.this,
							HomeActivity.class));
				}
				oldFinish();
			}
		}
	};

	public static class TabFragmentPagerAdapter extends FragmentPagerAdapter {

		private WeclomeFragment[] mDynamicFragments = new WeclomeFragment[PAGE_COUNT];

		public TabFragmentPagerAdapter(FragmentManager fm) {
			super(fm);

		}

		@Override
		public Fragment getItem(int arg0) {

			if (mDynamicFragments[arg0] == null) {
				mDynamicFragments[arg0] = new WeclomeFragment();
			}
			Bundle bundle = new Bundle();
			bundle.putInt("page", arg0);
			mDynamicFragments[arg0].setArguments(bundle);
			return mDynamicFragments[arg0];
		}

		@Override
		public int getCount() {

			return PAGE_COUNT;
		}

		@Override
		public int getItemPosition(Object object) {

			return PagerAdapter.POSITION_NONE;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_head_right:
			if(pageArg0 == 0 && isBrithEdit){
				mViewPager.setCurrentItem(1);
			}else if(pageArg0 == 1 && isGenderEdit){
				startActivityForNew(new Intent(GuideActivity.this,
						HomeActivity.class));
				oldFinish();
			}else{
				startActivityForNew(new Intent(GuideActivity.this,
						HomeActivity.class));
				oldFinish();
			}
			break;
		default:
			break;
		}

	}


	public  void setisBrithEdit(){
		isBrithEdit = true;
		setRightTvStatus();
	}

	public  void setisGenderEdit(){
		isGenderEdit = true;
		setRightTvStatus();
	}


	private void setRightTvStatus(){
		if(pageArg0 == 0 && isBrithEdit){
			mRightTv.setText("下一步");
			mRightTv.setTextColor(getResources().getColor(R.color.orage_color));
		}else if(pageArg0 == 1 && isGenderEdit){
			mRightTv.setText("完成");
			mRightTv.setTextColor(getResources().getColor(R.color.orage_color));
		}else{
			mRightTv.setText("跳过");
			mRightTv.setTextColor(getResources().getColor(R.color.base_black_title_color));
		}

	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus){
			setStatusBarHide();
		}
	}
}
