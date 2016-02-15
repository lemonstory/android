package com.xiaoningmeng.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baoyz.swipemenu.xlistview.XListView;
import com.xiaoningmeng.AblumDetailActivity;
import com.xiaoningmeng.R;
import com.xiaoningmeng.adapter.AblumPlayListAdapter;
import com.xiaoningmeng.base.BaseFragment;
import com.xiaoningmeng.bean.AlbumInfo;
import com.xiaoningmeng.bean.AudioDownLoad;
import com.xiaoningmeng.bean.PlayingStory;
import com.xiaoningmeng.bean.Story;
import com.xiaoningmeng.player.PlayerManager;
import com.xiaoningmeng.player.PlayerManager.AlbumSource;
import com.xiaoningmeng.player.PlayerManager.PlayState;

import java.util.List;

public class AblumDetailPlayListFragment extends BaseFragment {
	private XListView mListView;
	private AblumPlayListAdapter mAdapter;
	private List<Story> stories;
	private ViewGroup loadingView;
	private AlbumInfo albumInfo;
	private String playStoryId;
	private int current;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View contentView = View.inflate(getActivity(),
				R.layout.fragment_ablum_detail_playlist, null);
		mListView = (XListView) contentView
				.findViewById(R.id.id_stickynavlayout_innerscrollview);
		loadingView = (ViewGroup)contentView.findViewById(R.id.rl_loading);
		mListView.setPullLoadEnable(false);
		mListView.setPullRefreshEnable(false);
		if(stories != null){
			loadingData(albumInfo, stories,playStoryId,current);
		}
		return contentView;
	}

	public void setStoryList(final AlbumInfo albumInfo, List<Story> storys,String playStoryId,int current) {
		loadingData(albumInfo, storys,playStoryId,current);
	}


	private void loadingData(final AlbumInfo albumInfo, List<Story> storys,String playStoryId,int current) {
		this.stories = storys;
		this.albumInfo = albumInfo;
		this.playStoryId = playStoryId;
		this.current = current;
		if(loadingView == null){
			return;
		}
		loadingView.setVisibility(View.INVISIBLE);
		loadingView.setClickable(false);
		mAdapter = new AblumPlayListAdapter(getActivity(), stories, this.albumInfo,playStoryId);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				final int pos = position - 1;
				if(stories.size() > pos){
					Story story = stories.get(pos);
					PlayingStory playingStory = PlayerManager.getInstance().getPlayingStory();
					if (story.getAlbum_id().equals(albumInfo.getAlbumid())){
						if(story.getMediapath().equals(playingStory.mediapath)) {
							if (playingStory.playState == PlayState.RESUME || playingStory.playState == PlayState.START || playingStory.playState == PlayState.PLAY) {
								PlayerManager.getInstance().pausePlay();
							} else {
								PlayerManager.getInstance().resumePlay();
							}
						}else{
							PlayerManager.getInstance().playStory(albumInfo, stories, pos,AlbumSource.ALBUM_DETAIL);
						}
					}else if(story.getStoryId().equals(AblumDetailPlayListFragment.this.playStoryId)){
						PlayerManager.getInstance().playStory(albumInfo, stories, pos,AlbumSource.ALBUM_DETAIL);
					}else{
						PlayerManager.getInstance().playStory(albumInfo, stories, pos,AblumDetailPlayListFragment.this.current,AlbumSource.ALBUM_DETAIL);
					}
				}
			}
		});
	}



	public void reRequestLoading(){
		if(getView() == null){
			return;
		}
		if(loadingView == null){
			loadingView = (ViewGroup)getView().findViewById(R.id.rl_loading);
		}
		loadingView.setClickable(false);
		loadingView.setVisibility(View.VISIBLE);
		((TextView)loadingView.getChildAt(0)).setText("正在努力加载中");
		loadingView.getChildAt(1).setVisibility(View.VISIBLE);
	}

	public void onFailure(){
		if(getView() == null){
			return;
		}
		if(loadingView == null){
			loadingView = (ViewGroup)getView().findViewById(R.id.rl_loading);
		}
		loadingView.setVisibility(View.VISIBLE);
		((TextView)loadingView.getChildAt(0)).setText("请连接网络后点击屏幕重试");
		loadingView.getChildAt(1).setVisibility(View.INVISIBLE);
		loadingView.setClickable(true);
		loadingView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				reRequestLoading();
				((AblumDetailActivity)getActivity()).requestAlbumDetailData();
			}
		});
	}

	public BaseAdapter getAdapter() {
		return mAdapter;
	}

	public void notifyDownloadView(AudioDownLoad t){
		mAdapter.notifyView(mListView, t);
	}

}