package com.xiaoningmeng.fragment;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mobads.AdView;
import com.baidu.mobads.AdViewListener;
import com.xiaoningmeng.ClassificationActivity;
import com.xiaoningmeng.MoreActivity;
import com.xiaoningmeng.R;
import com.xiaoningmeng.adapter.DiscoverStoryAdapter;
import com.xiaoningmeng.adapter.RecommendStoryAdapter;
import com.xiaoningmeng.base.BaseFragment;
import com.xiaoningmeng.base.BaseFragmentActivity;
import com.xiaoningmeng.bean.AlbumInfo;
import com.xiaoningmeng.bean.MoreAblum;
import com.xiaoningmeng.bean.Tag;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.http.LHttpHandler;
import com.xiaoningmeng.http.LHttpRequest;
import com.xiaoningmeng.view.CollapsibleTextView;
import com.xiaoningmeng.view.FlowLayout;

import android.view.ViewGroup.MarginLayoutParams;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AblumDetailIntroFragment extends BaseFragment implements View.OnClickListener{

	private CollapsibleTextView introTv;
	private RelativeLayout mIntroView;
	private FlowLayout mFlowLayout;
	private BaseFragmentActivity mContext;
	private LinearLayout mRecommendLl1;
	private LinearLayout mRecommendLl2;
	private RecommendStoryAdapter mRecommendAdapter;
	private View mContentView;
	private List<Tag> mTagList;
	private List<AlbumInfo> mRecommendAlbumList;

	private Handler mHandler = new Handler();


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		 mContentView = View.inflate(getActivity(),
				R.layout.fragment_ablum_detail_intro, null);
		mContext = (BaseFragmentActivity) getActivity();
		return mContentView;
	}

	public void setIntro(final String intro, final List<Tag> tagList, final List<AlbumInfo> recommendAlbumList) {
		mTagList = tagList;
		mRecommendAlbumList = recommendAlbumList;
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if(mContentView != null) {
					ViewStub viewStub = (ViewStub) mContentView.findViewById(R.id.vs_intro);
					viewStub.inflate();
					introTv = (CollapsibleTextView) mContentView.findViewById(R.id.wv_album_detail_content);
					mIntroView = (RelativeLayout) mContentView.findViewById(R.id.fl_detail_intro);
					mFlowLayout = (FlowLayout) mContentView.findViewById(R.id.fl_album_mark);
					mRecommendLl1 = (LinearLayout) mContentView.findViewById(R.id.ll_recommend1);
					mRecommendLl2 = (LinearLayout) mContentView.findViewById(R.id.ll_recommend2);
					final FrameLayout adFl = (FrameLayout) mContentView.findViewById(R.id.fl_ad);
					AdView adView = new AdView(mContext, Constant.BAIDU_DETAIL_ID);
					adView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
					adView.setVisibility(View.GONE);
					adView.setListener(new DiscoverStoryAdapter.MyAdListener(adView));
					adFl.addView(adView);
					if (intro != null && !"".equals(intro)) {
						introTv.setDesc(intro, introTv, TextView.BufferType.NORMAL);
					} else {
						showEmptyTip(mIntroView, "暂无简介", getResources().getDimensionPixelOffset(R.dimen.dialog_margin));
					}
					if(tagList != null && tagList.size() != 0) {
						initTag(tagList);
					}
					if(recommendAlbumList != null && recommendAlbumList.size() >0){
						mRecommendAdapter = new RecommendStoryAdapter(mContext,recommendAlbumList,false);
						mRecommendAdapter.getView(0,mRecommendLl1,null);
						mRecommendAdapter.getView(1,mRecommendLl2,null);
					}
				}
			}
		},200);

	}

	public void initTag(List<Tag> tagList) {
		MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, mContext.getResources()
				.getDimensionPixelOffset(R.dimen.search_et_height));
		lp.rightMargin = mContext.getResources().getDimensionPixelOffset(R.dimen.base_margin);
		lp.topMargin = mContext.getResources().getDimensionPixelOffset(R.dimen.base_radius_size);
		int allSize = tagList.size();
		int minWidth = getResources().getDimensionPixelOffset(R.dimen.home_discover_btn);
		int childCount = mFlowLayout.getChildCount();
		if (childCount > allSize) {
			for (int i = childCount - 1; i >= allSize; i--) {
				mFlowLayout.removeViewAt(i);
			}
		}
		for (int i = 0; i < allSize; i++) {
			TextView view;
			if (mFlowLayout.getChildAt(i) != null) {
				view = (TextView) mFlowLayout.getChildAt(i);
			} else {
				view = new TextView(mContext);
				view.setMinWidth(minWidth);
				ColorStateList csl = (ColorStateList) mContext.getResources().getColorStateList(R.color.mark_flow_color_selector);
				view.setTextColor(csl);
				view.setBackgroundResource(R.drawable.mark_item_bg_normal);
				view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
				view.setGravity(Gravity.CENTER);
				mFlowLayout.addView(view, lp);
			}
			Tag tag = tagList.get(i);
			view.setOnClickListener(this);
			view.setText(tag.getName());
			view.setTag(tag);
		}
	}




	@Override
	public void onClick(View v) {
		if(v.getTag() != null){
			Tag tag = (Tag)v.getTag();
			Intent i = new Intent(mContext, ClassificationActivity.class);
			i.putExtra("classification",tag);
			startActivityForNew(i);
		}

	}
}
