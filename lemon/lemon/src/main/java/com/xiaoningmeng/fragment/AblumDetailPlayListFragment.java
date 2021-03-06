package com.xiaoningmeng.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.orhanobut.logger.Logger;
import com.umeng.analytics.MobclickAgent;
import com.wang.avi.AVLoadingIndicatorView;
import com.xiaoningmeng.R;
import com.xiaoningmeng.adapter.AblumPlayListAdapter;
import com.xiaoningmeng.base.BaseFragment;
import com.xiaoningmeng.bean.AlbumInfo;
import com.xiaoningmeng.bean.AudioDownLoad;
import com.xiaoningmeng.bean.PlayingStory;
import com.xiaoningmeng.bean.Story;
import com.xiaoningmeng.bean.StoryList;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.download.DownLoadClientImpl;
import com.xiaoningmeng.http.JsonResponse;
import com.xiaoningmeng.http.LHttpRequest;
import com.xiaoningmeng.player.PlayerManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.xiaoningmeng.http.LHttpRequest.mRetrofit;

public class AblumDetailPlayListFragment extends BaseFragment implements BaseQuickAdapter.RequestLoadMoreListener {

    private RecyclerView mRecyclerView;
    private AblumPlayListAdapter mAdapter;
    private int storysPage = 1;
    private int storysTotal;
    private List<Story> mStories;
    private List<Story> mCurrentStories;

    private AlbumInfo albumInfo;
    private String playStoryId;
    private int mCurrentPlayTime = 0;
    private int pageSize = 50;
    //单屏显示的数据量
    private int singleScreenItemNum = 6;
    private boolean isErr;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = View.inflate(getActivity(), R.layout.fragment_ablum_detail_playlist, null);
        mRecyclerView = (RecyclerView) contentView.findViewById(R.id.id_stickynavlayout_innerscrollview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mStories = new ArrayList<>();
        initAdapter();
        setLoadingView();
        return contentView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void initAdapter() {

        mAdapter = new AblumPlayListAdapter(mCurrentStories);
        mAdapter.setPlayStoryId(playStoryId);
        isErr = false;
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnLoadMoreListener(this);
        mAdapter.setAutoLoadMoreSize(pageSize);
        ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        mRecyclerView.addOnItemTouchListener(new OnItemChildClickListener() {
            @Override
            public void onSimpleItemChildClick(BaseQuickAdapter adapter, View view, int position) {

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
                        if (playingStory.playState == PlayerManager.PlayState.RESUME || playingStory.playState == PlayerManager.PlayState.START || playingStory.playState == PlayerManager.PlayState.PLAY) {
                            PlayerManager.getInstance().pausePlay();
                        } else {
                            PlayerManager.getInstance().resumePlay();
                        }
                    } else {
                        PlayerManager.getInstance().playStory(albumInfo, mStories, position, AblumDetailPlayListFragment.this.mCurrentPlayTime, PlayerManager.AlbumSource.ALBUM_DETAIL);
                    }
                }
            }
        });
    }

    private void setLoadingView() {

        ViewGroup viewGroup = (ViewGroup) mRecyclerView.getParent();
        View loadingView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.loading_view, viewGroup, false);
        AVLoadingIndicatorView aviImg = (AVLoadingIndicatorView) loadingView.findViewById(R.id.img_avi);
        ViewGroup.MarginLayoutParams aviImgMlp = (ViewGroup.MarginLayoutParams) aviImg.getLayoutParams();
        aviImgMlp.setMargins(0, 80, 20, 0);
        TextView loadingTipTv = (TextView) loadingView.findViewById(R.id.tv_loading_tip);
        ViewGroup.MarginLayoutParams loadingTipTvMlp = (ViewGroup.MarginLayoutParams) loadingTipTv.getLayoutParams();
        loadingTipTvMlp.setMargins(0, 90, 280, 0);
        mAdapter.setEmptyView(loadingView);
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
        if (null != mRecyclerView) {
            this.mAdapter.setNewData(this.mCurrentStories);
            if (this.mStories.size() < pageSize) {
                this.mAdapter.loadMoreEnd(singleScreenItemNum >= this.mStories.size());
            }
        }
        isErr = false;
    }

    private void requestAlbumStorysData(int albumId, int pageSize, int page) {

        if (albumId > 0 && pageSize > 0 && page > 0) {

            LHttpRequest.AlbumStorysRequest albumStorysRequest = mRetrofit.create(LHttpRequest.AlbumStorysRequest.class);
            Call<JsonResponse<StoryList>> call = albumStorysRequest.getResult(albumId, page, pageSize);
            call.enqueue(new Callback<JsonResponse<StoryList>>() {

                @Override
                public void onResponse(Call<JsonResponse<StoryList>> call, Response<JsonResponse<StoryList>> response) {

                    if (response.isSuccessful()) {
                        if (response.body().isSuccessful()) {
                            StoryList data = response.body().getData();
                            storysTotal = Integer.parseInt(data.getTotal());
                            mCurrentStories = data.getItems();
                            if (storysTotal > 0 && mCurrentStories.size() > 0) {

                                mAdapter.addData(mCurrentStories);
                                mStories = mAdapter.getData();
                            }
                        } else {
                            isErr = true;
                            if (null != getActivity() && AblumDetailPlayListFragment.this.isAdded()) {
                                Toast.makeText(getActivity(), response.body().getDesc(), Toast.LENGTH_SHORT).show();
                            }
                            mAdapter.loadMoreFail();
                        }
                    } else {
                        Logger.e(response.toString());
                    }
                }

                @Override
                public void onFailure(Call<JsonResponse<StoryList>> call, Throwable t) {

                    Logger.e(t.toString());
                }
            });
        }
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
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {

                int mAdapterItemsTotal = mAdapter.getData().size();
                if (mAdapterItemsTotal >= storysTotal || mCurrentStories.size() == 0) {
                    //数据全部加载完毕
                    mAdapter.loadMoreEnd();
                } else {
                    storysPage = storysPage + 1;
                    if (!isErr) {
                        requestAlbumStorysData(albumId, pageSize, storysPage);
                        mAdapter.loadMoreComplete();
                    } else {
                        isErr = true;
                        if (null != getActivity() && AblumDetailPlayListFragment.this.isAdded()) {
                            Toast.makeText(getActivity(), R.string.network_err, Toast.LENGTH_LONG).show();
                        }
                        mAdapter.loadMoreFail();
                    }
                }
            }
        }, Constant.DELAY_MILLIS);
    }
}