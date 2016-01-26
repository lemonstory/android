package com.xiaoningmeng.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import com.baoyz.swipemenu.xlistview.XListView;
import com.xiaoningmeng.MoreActivity;
import com.xiaoningmeng.R;
import com.xiaoningmeng.adapter.RecommendStoryAdapter;
import com.xiaoningmeng.base.BaseFragment;
import com.xiaoningmeng.bean.AlbumInfo;
import com.xiaoningmeng.bean.MoreAblum;
import com.xiaoningmeng.http.LHttpHandler;
import com.xiaoningmeng.http.LHttpRequest;
import org.apache.http.Header;
import java.util.ArrayList;
import java.util.List;

public class MoreFragment extends BaseFragment implements XListView.IXListViewListener {

	private XListView mListView;
	private RecommendStoryAdapter mAdapter;
	private List<AlbumInfo> mAlbumInfos;
	private MoreActivity.MoreParam mMoreParams;
	private int p = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View contentView = View.inflate(getActivity(),
				R.layout.fragment_discover, null);
		mMoreParams = getArguments().getParcelable(MoreActivity.MoreParams);
		mListView = (XListView) contentView.findViewById(R.id.lv_home_discover);
		mListView.setPullLoadEnable(false);
		mListView.setPullRefreshEnable(false);
		mAlbumInfos = new ArrayList<>();
		mAdapter = new RecommendStoryAdapter(getActivity(),mAlbumInfos,true);
		showLoadingTip();
		requestData();
		p = 1;
		mListView.setAdapter(mAdapter);
		mListView.setXListViewListener(this);
		return contentView;
	}


	private  void requestData(){

		LHttpRequest.getInstance().getMoreList(getActivity(), mMoreParams.moreType, p, 12, 0, mMoreParams.firstTagId, new LHttpHandler<MoreAblum>(getActivity()) {
			@Override
			public void onGetDataSuccess(MoreAblum data) {
				hideLoadingTip();
				if(data != null){
					List responseAlbumList = null;
					if(MoreActivity.HOT_MORE == mMoreParams.moreType){
						responseAlbumList = data.getHotrecommendlist();
					}else if(MoreActivity.NEW_MORE==mMoreParams.moreType){
						responseAlbumList = data.getNewonlinelist();
					}else if(MoreActivity.SAME_MORE == mMoreParams.moreType){
						responseAlbumList = data.getSameagelist();
					}
					if(responseAlbumList != null){
						if(responseAlbumList.size() == 0){
							mListView.setPullLoadEnable(false);
						}else {
							mListView.setPullLoadEnable(true);
							mAlbumInfos.addAll(responseAlbumList);
						}
						p++;
						mAdapter.notifyDataSetChanged();
					}
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


	@Override
	public void onRefresh() {
	}

	@Override
	public void onLoadMore() {
		requestData();
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
			if(emptyView == null){
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
					requestData();
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
