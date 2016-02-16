package com.xiaoningmeng.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;

import com.baoyz.swipemenu.xlistview.XListView;
import com.baoyz.swipemenu.xlistview.XListView.IXListViewListener;
import com.xiaoningmeng.HomeActivity;
import com.xiaoningmeng.R;
import com.xiaoningmeng.SearchActivity;
import com.xiaoningmeng.adapter.SearchStoryListAdapter;
import com.xiaoningmeng.base.BaseFragment;
import com.xiaoningmeng.bean.PlayingStory;
import com.xiaoningmeng.bean.Story;
import com.xiaoningmeng.player.PlayObserver;
import com.xiaoningmeng.player.PlayerManager;

import java.util.List;

public class SearchStoryChildFragment extends BaseFragment implements IXListViewListener,PlayObserver{
	private XListView mListView;
	private SearchStoryListAdapter mAdapter;
	private List<Story> stories;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View contentView = View.inflate(getActivity(),
				R.layout.fragment_empty_search, null);
		mListView = (XListView) contentView
				.findViewById(R.id.id_stickynavlayout_innerscrollview);
		mListView.setPullLoadEnable(true);
		mListView.setPullRefreshEnable(false);
		mListView.setXListViewListener(this);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				int pos = position - 1;
				PlayerManager.getInstance().playSearchStory(stories, pos);

			}
		});
		PlayerManager.getInstance().register(this);
		return contentView;
	}

	public void setStoryList(List<Story> storys) {
		if(storys== null){
			return;
		}
		stories = storys;
		if(storys.size() <10){
			mListView.setPullLoadEnable(false);
		}
		mAdapter = new SearchStoryListAdapter(getActivity(), stories);
		mListView.setAdapter(mAdapter);
		
	}
	
	public void addStoryList(List<Story> storys){
		if(mAdapter != null && storys!= null){
			stories.addAll(storys);
			mAdapter.notifyDataSetChanged();
			
		}
	}

	public void stopLoadMore(){
		mListView.stopLoadMore();
	}
	public BaseAdapter getAdapter() {
		return mAdapter;
	}

	@Override
	public void onRefresh() {
		
	}

	@Override
	public void onLoadMore() {
		if(getActivity() instanceof  SearchActivity) {
			((SearchActivity) getActivity()).moreSearch("story");
		}else if(getActivity() instanceof HomeActivity){
			//已被社区替换
			//((HomeActivity) getActivity()).mSearchFragment.moreSearch("story");
		}
	}

	@Override
	public void onDestroy() {
		PlayerManager.getInstance().unRegister(this);
		super.onDestroy();
	}

	@Override
	public void notify(PlayingStory music) {
		if(mAdapter != null) {
			switch (music.playState) {
				case START:
				case PAUSE:
				case RESUME:
				case STOP:
					mAdapter.notifyDataSetChanged();
					break;
			}
		}
	}
}
