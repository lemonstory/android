package com.xiaoningmeng.fragment;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.baoyz.swipemenu.xlistview.XListView;
import com.baoyz.swipemenu.xlistview.XListView.IXListViewListener;
import com.xiaoningmeng.AblumDetailActivity;
import com.xiaoningmeng.HomeActivity;
import com.xiaoningmeng.R;
import com.xiaoningmeng.SearchActivity;
import com.xiaoningmeng.adapter.SearchAdapter;
import com.xiaoningmeng.base.BaseFragment;
import com.xiaoningmeng.base.BaseFragmentActivity;
import com.xiaoningmeng.bean.AlbumInfo;

public class SearchAlbumChildFragment extends BaseFragment implements IXListViewListener{
	private XListView mListView;
	private SearchAdapter mSearchAdapter;
	private List<AlbumInfo> mAlbumInfos;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View contentView = View.inflate(getActivity(),
				R.layout.fragment_empty_search, null);
		mListView = (XListView) contentView
				.findViewById(R.id.id_stickynavlayout_innerscrollview);;
		mListView.setPullLoadEnable(true);
		mListView.setPullRefreshEnable(false);
		mListView.setXListViewListener(this);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				int pos = position - 1;
				Intent intent = new Intent(getActivity(), AblumDetailActivity.class);
				AlbumInfo albumInfo = mAlbumInfos.get(pos);
				intent.putExtra("albumId", albumInfo.getAlbumid());
				((BaseFragmentActivity)getActivity()).startActivityForNew(intent);
			}
		});
		return contentView;
	}

	public void setAlbumList(List<AlbumInfo> albumInfos) {
		if(albumInfos == null){
			return;
		}
		if(albumInfos.size() <10){
			mListView.setPullLoadEnable(false);
		}
		this.mAlbumInfos = albumInfos;
		mSearchAdapter = new SearchAdapter(getActivity(), mAlbumInfos);
		mListView.setAdapter(mSearchAdapter);
		
	}

	public void addAlbumList(List<AlbumInfo> albumInfos){
		if(mSearchAdapter != null && albumInfos!= null){
			mAlbumInfos.addAll(albumInfos);
			mSearchAdapter.notifyDataSetChanged();
			
		}
	}

	public void stopLoadMore(){
		mListView.stopLoadMore();
	}
	
	@Override
	public void onRefresh() {
		
	}

	@Override
	public void onLoadMore() {
		if(getActivity() instanceof  SearchActivity) {
			((SearchActivity) getActivity()).moreSearch("album");
		}else if(getActivity() instanceof HomeActivity){
			((HomeActivity) getActivity()).mSearchFragment.moreSearch("album");
		}
		
	}

}
