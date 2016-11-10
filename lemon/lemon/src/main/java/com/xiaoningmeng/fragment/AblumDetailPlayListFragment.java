package com.xiaoningmeng.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.umeng.analytics.MobclickAgent;
import com.xiaoningmeng.R;
import com.xiaoningmeng.adapter.AblumPlayListAdapter;
import com.xiaoningmeng.base.BaseFragment;
import com.xiaoningmeng.bean.AlbumInfo;
import com.xiaoningmeng.bean.AudioDownLoad;
import com.xiaoningmeng.bean.PlayingStory;
import com.xiaoningmeng.bean.Story;
import com.xiaoningmeng.download.DownLoadClientImpl;
import com.xiaoningmeng.manager.EmptyHelper;
import com.xiaoningmeng.player.PlayerManager;
import com.xiaoningmeng.player.PlayerManager.AlbumSource;
import com.xiaoningmeng.player.PlayerManager.PlayState;

import java.util.ArrayList;
import java.util.List;

public class AblumDetailPlayListFragment extends BaseFragment {

    private RecyclerView mRecyclerView;
    private AblumPlayListAdapter mAdapter;
    private List<Story> stories;
    private AlbumInfo albumInfo;
    private String playStoryId;
    private int current;
    private EmptyHelper mEmptyHelper;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = View.inflate(getActivity(),
                R.layout.fragment_ablum_detail_playlist, null);
        mRecyclerView = (RecyclerView) contentView
                .findViewById(R.id.id_stickynavlayout_innerscrollview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        stories = new ArrayList<>();
        initAdapter();
        return contentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (stories.size() > 0) {
            loadingData(albumInfo, stories, playStoryId, current);
        }
    }


    public void setStoryList(final AlbumInfo albumInfo, List<Story> storys, String playStoryId, int current) {
        loadingData(albumInfo, storys, playStoryId, current);
    }


    private void loadingData(final AlbumInfo albumInfo, List<Story> storys, String playStoryId, int current) {
        this.stories = storys;
        this.albumInfo = albumInfo;
        this.playStoryId = playStoryId;
        this.current = current;
        if (mRecyclerView != null) {
            this.mAdapter.setNewData(this.stories);
        }
    }

    public void initAdapter() {

        mAdapter = new AblumPlayListAdapter(stories);
        mAdapter.setPlayStoryId(playStoryId);
        mAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        mEmptyHelper = new EmptyHelper(getContext(), mRecyclerView, mAdapter);
        mEmptyHelper.setEmptyView(EmptyHelper.LOADING, false, getString(R.string.loading_tip));
        View footerView = this.getListEndView();
        mAdapter.addFooterView(footerView, 0);
        mRecyclerView.setAdapter(mAdapter);
        ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        mRecyclerView.addOnItemTouchListener(new OnItemChildClickListener() {
            @Override
            public void SimpleOnItemChildClick(BaseQuickAdapter adapter, View view, int position) {

            }

            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

                switch (view.getId()) {
                    case R.id.fl_download:
                        Story story = stories.get(position);
                        int downloadStatus = DownLoadClientImpl.getInstance().getDownloadStatus(story.getMediapath());
                        if (downloadStatus == -1) {
                            AudioDownLoad downLoad = new AudioDownLoad(story, position);
                            DownLoadClientImpl.getInstance().download(downLoad, albumInfo);
                        } else if (downloadStatus == 0) {
//                            new TipDialog.Builder(getContext()).setAutoDismiss(true)
//                                    .setTransparent(true).setTipText("嗯哈，正在下载中呢").create().show();
                        } else {
//                            new TipDialog.Builder(getContext()).setAutoDismiss(true)
//                                    .setTransparent(true).setTipText("嗯哈，你已经下载过啦").create().show();
                        }
                        MobclickAgent.onEvent(getContext(), "event_download");
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                super.onItemClick(adapter, view, position);
                if (stories.size() > position) {
                    Story story = stories.get(position);
                    PlayingStory playingStory = PlayerManager.getInstance().getPlayingStory();
                    if (story.getAlbum_id() != null && story.getAlbum_id().equals(albumInfo.getAlbumid())) {
                        if (story.getMediapath().equals(playingStory.mediapath)) {
                            if (playingStory.playState == PlayState.RESUME || playingStory.playState == PlayState.START || playingStory.playState == PlayState.PLAY) {
                                PlayerManager.getInstance().pausePlay();
                            } else {
                                PlayerManager.getInstance().resumePlay();
                            }
                        } else {
                            PlayerManager.getInstance().playStory(albumInfo, stories, position, AlbumSource.ALBUM_DETAIL);
                        }
                    } else if (story.getId() != null && story.getId().equals(AblumDetailPlayListFragment.this.playStoryId)) {
                        PlayerManager.getInstance().playStory(albumInfo, stories, position, AlbumSource.ALBUM_DETAIL);
                    } else {
                        PlayerManager.getInstance().playStory(albumInfo, stories, position, AblumDetailPlayListFragment.this.current, AlbumSource.ALBUM_DETAIL);
                    }
                }
            }
        });
    }


    public BaseQuickAdapter getAdapter() {
        return mAdapter;
    }

    public void notifyDownloadView(AudioDownLoad t) {
        if (stories != null) {
            for (int i = 0; i < stories.size(); i++) {
                Story s = stories.get(i);
                if (t.getStoryId().equals(s.getId())) {
                    mAdapter.notifyItemChanged(i);
                    break;
                }
            }
        }
    }

    public void onFailure() {

        if (this.isAdded()) {
            mEmptyHelper.setEmptyView(EmptyHelper.FAILURE, false, getString(R.string.failure_tip)/*, new View.OnClickListener() {
            @Override
			public void onClick(View view) {
				mEmptyHelper.setEmptyView(EmptyHelper.LOADING,false,getString(R.string.loading_tip));
				((AlbumDetailActivity)getActivity()).requestAlbumDetailData();
			}
		}*/);
        }
    }
}