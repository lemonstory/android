package com.xiaoningmeng.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mobads.AdView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.xiaoningmeng.R;
import com.xiaoningmeng.TagActivity;
import com.xiaoningmeng.adapter.DiscoverStoryAdapter;
import com.xiaoningmeng.adapter.TagAdapter;
import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.base.LazyFragment;
import com.xiaoningmeng.bean.Tag;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.view.CollapsibleTextView;

import java.util.List;

public class AblumDetailIntroFragment extends LazyFragment {

	private CollapsibleTextView introTv;
	private RelativeLayout mIntroView;
	private RecyclerView mRecyclerView;
	private BaseActivity mContext;
	private TagAdapter mTagAdapter;
	private View mContentView;
	private String intro;
	private List<Tag> tagList;
	private boolean isPrepared;
	private boolean flag;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		 mContentView = View.inflate(getActivity(),
				R.layout.fragment_ablum_detail_intro, null);
		mContext = (BaseActivity) getActivity();
		isPrepared = true;
		lazyLoad();
		return mContentView;
	}

	public void setIntro(final String intro, final List<Tag> tagList) {
		this.intro = intro;
		this.tagList = tagList;
		flag = true;
		lazyLoad();
	}

	@Override
	protected void lazyLoad() {
		if(!isPrepared || !isVisible) {
			return;
		}
		if(flag) {
			ViewStub viewStub = (ViewStub) mContentView.findViewById(R.id.vs_intro);
			viewStub.inflate();
			introTv = (CollapsibleTextView) mContentView.findViewById(R.id.wv_album_detail_content);
			mIntroView = (RelativeLayout) mContentView.findViewById(R.id.fl_detail_intro);
			final FrameLayout adFl = (FrameLayout) mContentView.findViewById(R.id.fl_ad);
			AdView adView = new AdView(mContext, Constant.BAIDU_DETAIL_ID);
			adView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			adView.setVisibility(View.GONE);
			adView.setListener(new DiscoverStoryAdapter.MyAdListener(adView));
			adFl.addView(adView);
			//-- baidu ad end
			if (intro != null && !"".equals(intro)) {
				introTv.setDesc(intro, introTv, TextView.BufferType.NORMAL);
			} else {
				showEmptyTip(mIntroView, "暂无简介", getResources().getDimensionPixelOffset(R.dimen.dialog_margin));
			}
			if (tagList != null && tagList.size() != 0) {

				mRecyclerView = (RecyclerView) mContentView.findViewById(R.id.rv_tag_list);
				mRecyclerView.setHasFixedSize(true);
				int spanCount = 3;
				GridLayoutManager manager = new GridLayoutManager(getContext(), spanCount);
				mRecyclerView.setLayoutManager(manager);
				initTag(tagList);
			}
			isLoadData = true;
		}
	}
	public void initTag(List<Tag> tagList) {

		mTagAdapter = new TagAdapter(tagList);
		mRecyclerView.setAdapter(mTagAdapter);
		((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
		mRecyclerView.addOnItemTouchListener(
				new OnItemChildClickListener() {
					@Override
					public void SimpleOnItemChildClick(BaseQuickAdapter adapter, View view, int position) {


					}

					@Override
					public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {


					}

					@Override
					public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

						Tag tag = (Tag) adapter.getData().get(position);
						if (tag != null && tag.getId() != null) {
							Intent i = new Intent(mContext, TagActivity.class);
							i.putExtra("tag",tag);
							startActivityForNew(i);
						}

					}
				}
		);
	}
}
