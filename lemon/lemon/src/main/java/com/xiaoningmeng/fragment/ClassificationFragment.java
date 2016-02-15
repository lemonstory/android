package com.xiaoningmeng.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.baoyz.swipemenu.xlistview.XListView;
import com.bigkoo.convenientbanner.CBPageAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiaoningmeng.AblumDetailActivity;
import com.xiaoningmeng.ClassificationActivity;
import com.xiaoningmeng.MoreActivity;
import com.xiaoningmeng.R;
import com.xiaoningmeng.WebViewActivity;
import com.xiaoningmeng.adapter.RecommendStoryAdapter;
import com.xiaoningmeng.base.BaseFragment;
import com.xiaoningmeng.base.BaseFragmentActivity;
import com.xiaoningmeng.bean.AlbumInfo;
import com.xiaoningmeng.bean.FocusPic;
import com.xiaoningmeng.bean.Special;
import com.xiaoningmeng.bean.Tag;
import com.xiaoningmeng.bean.TagAlbum;
import com.xiaoningmeng.bean.TagDetail;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.http.LClient;
import com.xiaoningmeng.http.LHttpHandler;
import com.xiaoningmeng.http.LHttpRequest;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

public class ClassificationFragment extends BaseFragment implements XListView.IXListViewListener {

	private XListView mListView;
	private RecommendStoryAdapter mAdapter;
	private List<AlbumInfo> mAlbumInfos;
	private List<TagAlbum> mTagAlbums;
	private ClassificationActivity.TagParam mTagParam;
	private boolean isAttach;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View contentView = View.inflate(getActivity(),
				R.layout.fragment_discover, null);
		if(!isAttach) {
			mTagParam = getArguments().getParcelable(ClassificationActivity.Fragment_Tag);
		}
		isAttach = false;
		mListView = (XListView) contentView.findViewById(R.id.lv_home_discover);
		mListView.setPullLoadEnable(false);
		mListView.setPullRefreshEnable(true);
		mAlbumInfos = new ArrayList<>();
		mTagAlbums = new ArrayList<>();
		mAdapter = new RecommendStoryAdapter(getActivity(),mAlbumInfos,true);
		showLoadingTip();
		requestData(Constant.FRIST,Constant.FRIST_ID,false);
		mListView.setAdapter(mAdapter);
		mListView.setXListViewListener(this);
		return contentView;
	}


	public void requestData(final String direction, String relationId,final boolean isReafresh){

		LHttpRequest.getInstance().getTagAblumListReq(getActivity(), mTagParam.tag, 0,direction, relationId,
				mTagParam.special,  Constant.GRID_REQ_LEN, new LHttpHandler<TagDetail>(getActivity()) {
			@Override
			public void onGetDataSuccess(TagDetail data) {
				hideLoadingTip();
				if(isReafresh){
					mAlbumInfos.clear();
					mTagAlbums.clear();
				}
				if(data != null && data.getTagalbumlist() != null){
					List<TagAlbum> tagAlbumList = data.getTagalbumlist();
					for(TagAlbum tagAlbum : tagAlbumList){
						mAlbumInfos.add(tagAlbum.getAlbuminfo());
					}
					mTagAlbums.addAll(tagAlbumList);
					if(Constant.DOWN.equals(direction) && tagAlbumList.size() == 0){
						mListView.setFootViewNoMore(true);
					}
					if(Constant.FRIST == direction && tagAlbumList.size() < Constant.GRID_REQ_LEN){
						mListView.setPullLoadEnable(false);
					}else{
						mListView.setPullLoadEnable(true);
					}
					mAdapter.notifyDataSetChanged();
				}
			}
			@Override
			public void onFailure(int statusCode, Header[] headers,
								  String responseString, Throwable throwable) {
				if(mAlbumInfos.size() == 0) {
					mListView.postDelayed(new Runnable() {

						@Override
						public void run() {
							hideLoadingTip();
							showEmptyTip();
						}
					}, 500);
				}

			}
			@Override
			public void onFinish() {
				onLoad();
				super.onFinish();
			}
		});
	}


	public void refreshData(ClassificationActivity.TagParam tagParam){
		isAttach = true;
		mTagParam = tagParam;
	}


	@Override
	public void onRefresh() {
		if(mTagAlbums.size() > 0){
			String relationId = mTagAlbums.get(0).getId();
			if(relationId != null) {
				requestData(Constant.UP, relationId,false);
			}
		}else{
			requestData(Constant.FRIST,Constant.FRIST_ID,false);
		}

	}

	@Override
	public void onLoadMore() {
		int size = mTagAlbums.size();
		if(size > 0){
			String  relationId = mTagAlbums.get(size-1).getId();
			if(relationId != null) {
				requestData(Constant.DOWN, relationId,false);
			}
		}else{
			requestData(Constant.FRIST,Constant.FRIST_ID,false);
		}
	}

	private void onLoad() {

		mListView.stopRefresh();
		mListView.stopLoadMore();
	}

	
	View loadingView;
	public void showLoadingTip() {
		if(mListView.getHeaderViewsCount() == 1){
			if(loadingView == null){
				loadingView = View.inflate(getActivity(),R.layout.fragment_loading, null);
				loadingView.setPadding(0, getResources().getDimensionPixelOffset(R.dimen.home_banner_height), 0, 0);
			}
			mListView.addHeaderView(loadingView,null,false);
		}
	}
	
	public void hideLoadingTip() {
		
		if(loadingView != null && mListView.getHeaderViewsCount() > 1){
			mListView.removeHeaderView(loadingView);
		}
	}
	
	
	TextView emptyView;
	public void showEmptyTip() {

		if(mListView.getHeaderViewsCount() == 1){
			if(emptyView == null && getActivity() != null){
				LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
			     if (inflater != null) { 
			    	 emptyView = (TextView)inflater.inflate(R.layout.fragment_empty, null);
			     }
			}
			if(emptyView != null){
				emptyView.setText("请连接网络后点击屏幕重试");
				emptyView.setClickable(true);
				mListView.addHeaderView(emptyView,null,false);
				emptyView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					hideEmptyTip();
					requestData(Constant.FRIST,Constant.FRIST_ID,false);
				}
			});
			}
		}
	}
	
	public void hideEmptyTip() {
		if(emptyView != null && mListView.getHeaderViewsCount() > 1){
			emptyView.setClickable(false);
			mListView.removeHeaderView(emptyView);
		}
	}

}
