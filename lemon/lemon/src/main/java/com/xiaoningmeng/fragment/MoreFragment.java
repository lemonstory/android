package com.xiaoningmeng.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.xiaoningmeng.AlbumDetailActivity;
import com.xiaoningmeng.MoreActivity;
import com.xiaoningmeng.R;
import com.xiaoningmeng.adapter.AlbumAdapter;
import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.base.LazyFragment;
import com.xiaoningmeng.bean.AlbumInfo;
import com.xiaoningmeng.bean.MoreAblum;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.http.JsonCallback;
import com.xiaoningmeng.http.LHttpRequest;
import com.xiaoningmeng.manager.EmptyHelper;


import java.util.ArrayList;
import java.util.List;

public class MoreFragment extends LazyFragment
		implements SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {

	private RecyclerView mRecyclerView;
	private AlbumAdapter mQuickAdapter;
	private SwipeRefreshLayout mRefreshLayout;
	private List<AlbumInfo> mAlbumInfos;
	private MoreActivity.MoreParam mMoreParams;
	private int p = 0;
	private boolean isPrepared;
	private EmptyHelper mEmptyHelper;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View contentView = View.inflate(getActivity(), R.layout.fragment_tag, null);
		isLoadData = false;
		mMoreParams = getArguments().getParcelable(MoreActivity.MoreParams);
		mRecyclerView = (RecyclerView) contentView.findViewById(R.id.rv_list);
		mRefreshLayout = (SwipeRefreshLayout)contentView.findViewById(R.id.swipeLayout);
		mRecyclerView.setHasFixedSize(true);
		mRefreshLayout.setOnRefreshListener(this);
		mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
		mAlbumInfos = new ArrayList<>();
		initAdapter();
		p = 1;
		isPrepared = true;
		requestData();
		return contentView;
	}

	private void initAdapter() {

		mQuickAdapter = new AlbumAdapter(mAlbumInfos);
		mQuickAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
		mQuickAdapter.setOnLoadMoreListener(this);
		mQuickAdapter.openLoadMore(Constant.GRID_REQ_LEN);
		mRefreshLayout.setRefreshing(true);
		mEmptyHelper = new EmptyHelper(getContext(),mRecyclerView,mQuickAdapter);
		mEmptyHelper.setEmptyView(EmptyHelper.LOADING,getString(R.string.loading_tip));
		mQuickAdapter.isFirstOnly(true);
		mRecyclerView.addOnItemTouchListener(new OnItemChildClickListener() {

			@Override
			public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
				if(position >= 0) {
					AlbumInfo albumInfo = (AlbumInfo) adapter.getItem(position);
					Intent intent = new Intent(getActivity(), AlbumDetailActivity.class);
					intent.putExtra("albumId", albumInfo.getId());
					intent.putExtra("albumInfo", albumInfo);
					((BaseActivity) getActivity()).startShareTransitionActivity(intent, view, "albumImage");
				}
			}
			@Override
			public void SimpleOnItemChildClick(BaseQuickAdapter adapter, View view, int position) {

			}
		});
		mRecyclerView.setAdapter(mQuickAdapter);
	}

	private  void requestData(){
		LHttpRequest.getInstance().getMoreList(getActivity(), mMoreParams.moreType, p,
				Constant.GRID_REQ_LEN, 0, mMoreParams.firstTagId, new JsonCallback<MoreAblum>() {
			@Override
			public void onGetDataSuccess(MoreAblum data) {
				isLoadData = true;
				mRefreshLayout.setEnabled(false);
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
						int size = responseAlbumList.size();
						if(size < Constant.GRID_REQ_LEN){
							mQuickAdapter.loadComplete();
						}
						p++;
						mQuickAdapter.addData(responseAlbumList);
					}
				}

			}
			@Override
			public void onFailure(String responseString) {
				isLoadData = true;

			}
			@Override
			public void onFinish() {
				if(mAlbumInfos.size() == 0){

					mEmptyHelper.setEmptyView(EmptyHelper.FAILURE,getString(R.string.empty_tip));
				}
				super.onFinish();
			}
		});
	}


	@Override
	public void onRefresh() {
			requestData();
	}


	/*public void setEmptyView(boolean isLoading){
		View emptyView;
		if(isLoading) {
			emptyView = mInflater.inflate(R.layout.layout_loading2,(ViewGroup) mRecyclerView.getParent(),false);
		}else{
			emptyView = mInflater.inflate(R.layout.layout_empty,(ViewGroup) mRecyclerView.getParent(),false);
		}
		changedView(emptyView);
	}


	private void changedView(View view) {
		if( mQuickAdapter.getEmptyView() != view) {
			mQuickAdapter.setEmptyView(view);
			mQuickAdapter.notifyItemChanged(0);
		}
	}*/

	@Override
	public void onLoadMoreRequested() {
		requestData();
	}

	@Override
	protected void lazyLoad() {
		if(!isPrepared || !isVisible) {
			return;
		}
		mRefreshLayout.setRefreshing(true);
		requestData();
	}
}
