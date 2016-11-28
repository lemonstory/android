package com.xiaoningmeng.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.umeng.analytics.MobclickAgent;
import com.xiaoningmeng.R;
import com.xiaoningmeng.adapter.AblumPlayListAdapter;
import com.xiaoningmeng.application.MyApplication;
import com.xiaoningmeng.base.BaseFragment;
import com.xiaoningmeng.bean.AlbumInfo;
import com.xiaoningmeng.bean.AudioDownLoad;
import com.xiaoningmeng.bean.PlayingStory;
import com.xiaoningmeng.bean.Story;
import com.xiaoningmeng.bean.StoryList;
import com.xiaoningmeng.download.DownLoadClientImpl;
import com.xiaoningmeng.http.JsonCallback;
import com.xiaoningmeng.http.LHttpRequest;
import com.xiaoningmeng.manager.EmptyHelper;
import com.xiaoningmeng.player.PlayerManager;
import com.xiaoningmeng.player.PlayerManager.AlbumSource;
import com.xiaoningmeng.player.PlayerManager.PlayState;

import java.util.ArrayList;
import java.util.List;

public class AblumDetailPlayListFragment extends BaseFragment implements BaseQuickAdapter.RequestLoadMoreListener {

    private RecyclerView mRecyclerView;
    private AblumPlayListAdapter mAdapter;
    private int storysPage;
    private int storysTotal;
    private List<Story> mStories;
    private List<Story> mCurrentStories;

    private AlbumInfo albumInfo;
    private String playStoryId;
    private int mCurrentPlayTime = 0;
    private int pageSize = 50;
    private int delayMillis = 1000;
    private EmptyHelper mEmptyHelper;
    private boolean isErr;
    private View notLoadingView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = View.inflate(getActivity(),
                R.layout.fragment_ablum_detail_playlist, null);
        mRecyclerView = (RecyclerView) contentView
                .findViewById(R.id.id_stickynavlayout_innerscrollview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mStories = new ArrayList<>();
        initAdapter();
        return contentView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void initAdapter() {

        mAdapter = new AblumPlayListAdapter(mCurrentStories);
        mAdapter.setPlayStoryId(playStoryId);
        mAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        mEmptyHelper = new EmptyHelper(getContext(), mRecyclerView, mAdapter);
        mEmptyHelper.setEmptyView(EmptyHelper.LOADING, false, getString(R.string.loading_tip));
        notLoadingView = getActivity().getLayoutInflater().inflate(R.layout.list_footer_view, (ViewGroup) mRecyclerView.getParent(), false);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnLoadMoreListener(this);
        mAdapter.openLoadMore(pageSize);
        ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        mRecyclerView.addOnItemTouchListener(new OnItemChildClickListener() {
            @Override
            public void SimpleOnItemChildClick(BaseQuickAdapter adapter, View view, int position) {

            }

            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

                switch (view.getId()) {
                    case R.id.fl_download:
                        Story story = mStories.get(position);
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
                if (mStories.size() > position) {
                    Story story = mStories.get(position);
                    PlayingStory playingStory = PlayerManager.getInstance().getPlayingStory();
                    AblumDetailPlayListFragment.this.playStoryId = story.getId();
                    mAdapter.setPlayStoryId(story.getId());
                        if (story.getMediapath().equals(playingStory.mediapath)) {
                            if (playingStory.playState == PlayState.RESUME || playingStory.playState == PlayState.START || playingStory.playState == PlayState.PLAY) {
                                PlayerManager.getInstance().pausePlay();
                            } else {
                                PlayerManager.getInstance().resumePlay();
                            }
                        } else {
                            PlayerManager.getInstance().playStory(albumInfo, mStories, position,AblumDetailPlayListFragment.this.mCurrentPlayTime, AlbumSource.ALBUM_DETAIL);
                        }
                }
            }
        });
    }


    public BaseQuickAdapter getAdapter() {
        return mAdapter;
    }

    public void setStoryList(final AlbumInfo albumInfo, List<Story> storys, int storysPage, String playStoryId, int currentPlayTime) {

        this.storysPage = storysPage;
        refreshAlbumStorysData(albumInfo, storys, storysPage, playStoryId, currentPlayTime);
    }


    private void refreshAlbumStorysData(final AlbumInfo albumInfo, List<Story> storys, int storysPage, String playStoryId, int currentPlayTime) {

        this.storysPage = storysPage;
        this.mCurrentStories = storys;
        this.mStories = this.mCurrentStories;

        if (albumInfo != null) {
            this.albumInfo = albumInfo;
            this.storysTotal = albumInfo.getStory_num();
        }
        this.playStoryId = playStoryId;
        this.mCurrentPlayTime = currentPlayTime;
        if (mRecyclerView != null) {
            this.mAdapter.setNewData(this.mCurrentStories);
        }
        isErr = false;

        if(this.mStories.size() < pageSize) {
            mAdapter.addFooterView(notLoadingView);
        }
    }

    private void requestAlbumStorysData(int albumId, int pageSize, int page) {

        if (albumId > 0 && pageSize > 0 && page > 0) {

            LHttpRequest.getInstance().albumStorysReq(getActivity(), albumId, pageSize, page,
                    MyApplication.getInstance().getUid(),
                    new JsonCallback<StoryList>() {

                        @Override
                        public void onGetDataSuccess(StoryList data) {

                            storysTotal = Integer.parseInt(data.getTotal());
                            if (storysTotal > 0 && data.getItems().size() > 0) {

                                mCurrentStories = data.getItems();
                                mAdapter.addData(mCurrentStories);
                                mStories = mAdapter.getData();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, String failureResponse) {
                            AblumDetailPlayListFragment.this.onFailure(statusCode,failureResponse);
                        }

                        @Override
                        public void onFinish() {
                            super.onFinish();
                        }
                    });
        }

    }

    public void onFailure(int statusCode, String failureResponse) {

        isErr = true;
        if(null != getActivity() && AblumDetailPlayListFragment.this.isAdded()) {
            Toast.makeText(getActivity(), R.string.network_err, Toast.LENGTH_LONG).show();
        }
        mAdapter.showLoadMoreFailedView();
    }


    public void notifyDownloadView(AudioDownLoad t) {
        if (mStories != null) {
            for (int i = 0; i < mStories.size(); i++) {
                Story s = mStories.get(i);
                if (t.getStoryId().equals(s.getId())) {
                    mAdapter.notifyItemChanged(i);
                    break;
                }
            }
        }
    }

    @Override
    public void onLoadMoreRequested() {

        final int albumId = Integer.parseInt(this.albumInfo.getId());
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {

                if (mAdapter.getData().size() >= storysTotal) {
                    mAdapter.loadComplete();
                    mAdapter.addFooterView(notLoadingView);
                } else {
                    storysPage = storysPage + 1;
                    if (!isErr) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                requestAlbumStorysData(albumId, pageSize, storysPage);
                            }
                        }, delayMillis);
                    } else {
                        isErr = true;
                        Toast.makeText(getActivity(), R.string.network_err, Toast.LENGTH_LONG).show();
                        mAdapter.showLoadMoreFailedView();
                    }
                }
            }
        });

    }
}