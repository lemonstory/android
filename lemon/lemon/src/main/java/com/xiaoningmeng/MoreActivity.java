package com.xiaoningmeng;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.xiaoningmeng.fragment.MoreFragment;
import com.xiaoningmeng.player.PlayerManager;

import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.bean.MoreAblum;
import com.xiaoningmeng.bean.PlayingStory;
import com.xiaoningmeng.bean.Tag;
import com.xiaoningmeng.http.JsonCallback;
import com.xiaoningmeng.http.LHttpRequest;
import com.xiaoningmeng.manager.PlayWaveManager;
import com.xiaoningmeng.player.PlayObserver;
import com.xiaoningmeng.view.dialog.DrawableDialogLoading;

import java.util.ArrayList;
import java.util.List;


public class MoreActivity extends BaseActivity implements View.OnClickListener,PlayObserver {

	public static final int SAME_MORE = 1;
	public static final int NEW_MORE = 2;
	public static final int PRIVATE_MORE = 3;
	public static final int HOT_MORE =0;
	public static final String MORE_TYPE = "more_type";
	private PagerSlidingTabStrip mIndicator;
	private ViewPager mViewPager;
	private ImageView mCoverImg;
	public static final String MoreParams = "MoreParams";
	private List<MoreParam> mMoreParams;
	public static final String[] MoreArray={"热门推荐","同龄在听","最新上架","私人定制"};
	private MoreFragment[] mMoreFragments;
	private int mSelectType;
	private TextView mTitleNameTv;
	//private int p =1;


	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		Fresco.initialize(this);
		setContentView(R.layout.activity_more);
		mSelectType = getIntent().getIntExtra(MORE_TYPE, HOT_MORE);
		mIndicator = (PagerSlidingTabStrip) findViewById(R.id.tab_indicator);
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mViewPager.setOffscreenPageLimit(1);
		mTitleNameTv = (TextView) findViewById(R.id.tv_head_title);
		mCoverImg = (ImageView) findViewById(R.id.img_head_right);
		mTitleNameTv.setText(MoreArray[mSelectType]);
		mMoreParams = new ArrayList<>();
		setLoading(new DrawableDialogLoading(this));
		PlayerManager.getInstance().register(this);
		requestData();
	}


	private void requestData(){
		LHttpRequest.getInstance().getMoreList(this, mSelectType,1, 0, 1, null, new JsonCallback<MoreAblum>(this) {
			@Override
			public void onGetDataSuccess(MoreAblum data) {
				if(data != null){
					List<Tag> firstTagList = data.getFirsttaglist();
					mMoreParams.add(new MoreParam(mSelectType,"全部",null));
					if(firstTagList != null){
						for(Tag tag :firstTagList){
							mMoreParams.add(new MoreParam(mSelectType,tag.getName(),tag.getId()));
						}
					}
					mMoreFragments = new MoreFragment[mMoreParams.size()];
					mViewPager.setAdapter(new TabFragmentPagerAdapter(getSupportFragmentManager()));
					mIndicator.setViewPager(mViewPager);
				}
			}
		});

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
			case R.id.img_head_search:
				startActivityForNew(new Intent(this,SearchActivity.class));
				break;
		}
	}

	public  class TabFragmentPagerAdapter extends FragmentPagerAdapter {



		public TabFragmentPagerAdapter(FragmentManager fm) {
			super(fm);

		}

		@Override
		public Fragment getItem(int arg0) {

			if(mMoreFragments[arg0] == null){
				mMoreFragments[arg0] = new MoreFragment();
			}
			Bundle bundle = new Bundle();
			bundle.putParcelable(MoreParams, mMoreParams.get(arg0));
			mMoreFragments[arg0].setArguments(bundle);
			return mMoreFragments[arg0];
		}

		@Override
		public int getCount() {

			return mMoreFragments.length;
		}
		@Override
		public int getItemPosition(Object object) {

			return PagerAdapter.POSITION_NONE;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return mMoreParams.get(position).name;
		}
	}


	@Override
	protected void onResume() {
		PlayWaveManager.getInstance().loadWaveAnim(this, mCoverImg);
		super.onResume();

	}

	@Override
	public void notify(PlayingStory music) {
		PlayWaveManager.getInstance().notify(music);

	}


	@Override
	public void onDestroy() {
		PlayerManager.getInstance().unRegister(this);
		super.onDestroy();
	}

	public static class MoreParam implements Parcelable {
		public String name;
		public int moreType;
		public String firstTagId;

		public MoreParam(int moreType,String name,  String firstTagId) {
			this.name = name;
			this.moreType = moreType;
			this.firstTagId = firstTagId;
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeString(this.name);
			dest.writeInt(this.moreType);
			dest.writeString(this.firstTagId);
		}

		protected MoreParam(Parcel in) {
			this.name = in.readString();
			this.moreType = in.readInt();
			this.firstTagId = in.readString();
		}

		public static final Parcelable.Creator<MoreParam> CREATOR = new Parcelable.Creator<MoreParam>() {
			public MoreParam createFromParcel(Parcel source) {
				return new MoreParam(source);
			}

			public MoreParam[] newArray(int size) {
				return new MoreParam[size];
			}
		};
	}


}
