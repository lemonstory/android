package com.xiaoningmeng.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.umeng.analytics.MobclickAgent;
import com.wang.avi.AVLoadingIndicatorView;
import com.xiaoningmeng.AlbumDetailActivity;
import com.xiaoningmeng.DownloadActivity;
import com.xiaoningmeng.FavActivity;
import com.xiaoningmeng.LoginActivity;
import com.xiaoningmeng.R;
import com.xiaoningmeng.adapter.MineHistoryAdapter;
import com.xiaoningmeng.auth.UserAuth;
import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.base.BaseFragment;
import com.xiaoningmeng.bean.AlbumInfo;
import com.xiaoningmeng.bean.ListenerAlbum;
import com.xiaoningmeng.bean.Mine;
import com.xiaoningmeng.bean.UserInfo;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.db.HistoryDao;
import com.xiaoningmeng.download.DownLoadClientImpl;
import com.xiaoningmeng.download.OnDownloadCountChangedListener;
import com.xiaoningmeng.event.FavEvent;
import com.xiaoningmeng.event.HistoryEvent;
import com.xiaoningmeng.event.LoginEvent;
import com.xiaoningmeng.http.JsonResponse;
import com.xiaoningmeng.http.LHttpRequest;
import com.xiaoningmeng.utils.DebugUtils;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.xiaoningmeng.http.LHttpRequest.mRetrofit;

public class MineFragment extends BaseFragment implements OnClickListener, OnDownloadCountChangedListener, SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private MineHistoryAdapter mAdapter;
    private BaseActivity mContext;
    private TextView mDownloadCountTv;
    private TextView mFavCountTv;
    private List<ListenerAlbum> mAlbumList;
    private List<ListenerAlbum> mDBHistoryAlbum;
    private List<HistoryEvent> mHistoryEvents = new ArrayList<>();
    private UserInfo mLoginUserInfo;
    private View errorView;
    private View emptyView;
    private View loadingView;
    private boolean isErr;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View contentView = View.inflate(getActivity(), R.layout.fragment_mine, null);
        mContext = (BaseActivity) getActivity();
        DownLoadClientImpl.getInstance().setOnDownloadCountChangedListener(this);
        mDBHistoryAlbum = HistoryDao.getInstance().getHistoryAlbums();
        mLoginUserInfo = UserAuth.getInstance().getLoginUserInfo(getActivity());
        initView(contentView);
        loadingView = getLoadingView();
        mAdapter.setEmptyView(loadingView);
        requestHistoryAlbumsData(Constant.FRIST, Constant.FRIST_ID);
        EventBus.getDefault().register(this);
        return contentView;
    }

    public void onResume() {

        super.onResume();
        if (mHistoryEvents.size() > 0) {
            for (HistoryEvent historyEvent : mHistoryEvents) {
                if (historyEvent.listenerAlbum != null) addCurrentListenAlbum(historyEvent);
                mAdapter.setNewData(mAlbumList);
            }
            mHistoryEvents.clear();
        }
        MobclickAgent.onEvent(mContext, "event_show_mystory");
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initView(View contentView) {

        isErr = false;
        mRecyclerView = (RecyclerView) contentView.findViewById(R.id.rv_main);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        mAdapter = new MineHistoryAdapter(R.layout.item_mine_history, mAlbumList);
        mRecyclerView.setAdapter(mAdapter);

        //列表为空处理
        emptyView = getEmptyView(getString(R.string.empty_history_albums), null);
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int size = mAlbumList.size();
                        if (size > 0) {
                            requestHistoryAlbumsData(Constant.DOWN, mAlbumList.get(size - 1).getAlbumid());
                        }
                    }
                }, Constant.DELAY_MILLIS);
            }
        }, mRecyclerView);

        //下载,收藏处理
        setHeaderView();

        mAlbumList = new ArrayList<>();
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                ListenerAlbum listenerAlbum = mAlbumList.get(position);
                AlbumInfo albumInfo = listenerAlbum.getAlbuminfo();
                Intent intent = new Intent(mContext, AlbumDetailActivity.class);
                intent.putExtra("albumId", albumInfo.getId());
                intent.putExtra("albumInfo", albumInfo);
                intent.putExtra("playstoryid", listenerAlbum.getPlaystoryid());
                intent.putExtra("playtimes", listenerAlbum.getPlaytimes());
                mContext.startShareTransitionActivity(intent, view.findViewById(R.id.img_mine_item_cover), "albumImage");
            }
        });

        //下拉刷新
        mSwipeRefreshLayout = (SwipeRefreshLayout) contentView.findViewById(R.id.swipeLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        //错误页
        errorView = getErrorView();
    }

    public void setHeaderView() {

        View headView = View.inflate(getActivity(), R.layout.fragment_mine_head, null);
        mAdapter.addHeaderView(headView);
        mAdapter.setHeaderFooterEmpty(true, false);
        //下载设置
        headView.findViewById(R.id.rl_mine_download).setOnClickListener(this);
        mDownloadCountTv = (TextView) headView.findViewById(R.id.tv_mine_download_count);
        mFavCountTv = (TextView) headView.findViewById(R.id.tv_mine_fav_count);
        if (null != mLoginUserInfo) {
            if (mLoginUserInfo.getFavcount().matches("-?\\d+")) {
                if (Integer.parseInt(mLoginUserInfo.getFavcount()) > 0) {
                    mFavCountTv.setText(mLoginUserInfo.getFavcount() + "");
                }
            }
        }
        int downloadSize = DownLoadClientImpl.getInstance().getDownloadCount();
        if (downloadSize > 0) {
            mDownloadCountTv.setText(downloadSize + "");
        }

        //收藏设置
        headView.findViewById(R.id.rl_mine_fav).setOnClickListener(this);
    }

    private View getLoadingView() {

        ViewGroup viewGroup = (ViewGroup) mRecyclerView.getParent();
        View loadingView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.loading_view, viewGroup, false);
        AVLoadingIndicatorView aviImg = (AVLoadingIndicatorView) loadingView.findViewById(R.id.img_avi);
        ViewGroup.MarginLayoutParams aviImgMlp = (ViewGroup.MarginLayoutParams) aviImg.getLayoutParams();
        aviImgMlp.setMargins(0, 80, 20, 0);
        TextView loadingTipTv = (TextView) loadingView.findViewById(R.id.tv_loading_tip);
        ViewGroup.MarginLayoutParams loadingTipTvMlp = (ViewGroup.MarginLayoutParams) loadingTipTv.getLayoutParams();
        loadingTipTvMlp.setMargins(0, 90, 280, 0);
        return loadingView;
    }

    public View getEmptyView(String tip, OnClickListener onClickListener) {

        TextView emptyView;
        emptyView = (TextView) View.inflate(getActivity(), R.layout.fragment_empty, null);
        emptyView.setText(tip);
        emptyView.setPadding(0, getResources().getDimensionPixelOffset(R.dimen.home_discover_item_img_height), 0, 0);
        return emptyView;
    }

    public View getErrorView() {

        ViewGroup viewGroup = (ViewGroup) mRecyclerView.getParent();
        View errorView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.error_view, viewGroup, false);
        errorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRefresh();
            }
        });
        return errorView;
    }

    private void requestHistoryAlbumsData(final String direction, String startId) {

        if (UserAuth.getInstance().isLogin(this.getActivity())) {

            LHttpRequest.MyStoryRequest myStoryRequest = mRetrofit.create(LHttpRequest.MyStoryRequest.class);
            Call<JsonResponse<Mine>> call = myStoryRequest.getResult(direction, startId, Constant.MAX_REQ_LEN);
            call.enqueue(new Callback<JsonResponse<Mine>>() {

                @Override
                public void onResponse(Call<JsonResponse<Mine>> call, Response<JsonResponse<Mine>> response) {

                    if (response.isSuccessful() && response.body().isSuccessful()) {

                        Mine data = response.body().getData();
                        List<ListenerAlbum> albums = data.getListenalbumlist();
                        if (direction == Constant.FRIST) {

                            mAlbumList.clear();
                            addHistoryAlbums(mDBHistoryAlbum);
                            if (albums.size() == Constant.MAX_REQ_LEN) {
                                mAdapter.loadMoreComplete();
                            } else if (albums.size() < Constant.MAX_REQ_LEN) {
                                mAdapter.loadMoreEnd();
                            }
                        } else {
                            if (albums.size() < Constant.MAX_REQ_LEN) {
                                mAdapter.loadMoreEnd();
                            }
                        }
                        addHistoryAlbums(albums);
                        mAdapter.setNewData(mAlbumList);
                    } else {

                        DebugUtils.e(response.toString());
                        addHistoryAlbums(mDBHistoryAlbum);
                        isErr = true;
                        mAdapter.loadMoreFail();
                    }
                }

                @Override
                public void onFailure(Call<JsonResponse<Mine>> call, Throwable t) {

                    addHistoryAlbums(mDBHistoryAlbum);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.setEmptyView(errorView);
                            isErr = false;
                        }
                    }, Constant.DELAY_MILLIS);
                    mAdapter.loadMoreFail();
                }
            });

        } else {

            List<ListenerAlbum> dbHistoryAlbums =
                    HistoryDao.getInstance().getUnloginHistoryAlbums(mAlbumList.size(), Constant.MAX_REQ_LEN);
            if (dbHistoryAlbums.size() > 0) {
                addHistoryAlbums(dbHistoryAlbums);
            }
            mAdapter.setNewData(mAlbumList);
        }

        //用户没有播放记录
        if (!isErr && 0 == mAlbumList.size()) {
            mAdapter.setEmptyView(emptyView);
        }
    }

    private void addHistoryAlbums(List<ListenerAlbum> historyAlbums) {

        if (historyAlbums != null && historyAlbums.size() > 0) {
            mAlbumList.addAll(historyAlbums);
        }
    }

    private void addCurrentListenAlbum(HistoryEvent historyEvent) {

        int albumSize = mAlbumList.size();
        for (int i = 0; i < albumSize; i++) {

            ListenerAlbum album = mAlbumList.get(i);
            if (null != album
                    && null != album.getAlbumid()
                    && null != historyEvent
                    && null != historyEvent.albumId) {
                if (album.getAlbumid().equals(historyEvent.albumId)) {
                    mAlbumList.remove(album);
                    break;
                }
            }
        }
        mAlbumList.add(0, historyEvent.listenerAlbum);
    }

    @Override
    public void onRefresh() {

        mAdapter.setEmptyView(loadingView);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (isErr) {
                    mAdapter.setEmptyView(errorView);
                    isErr = false;
                    mSwipeRefreshLayout.setRefreshing(false);
                } else {
                    if (mAlbumList.size() > 0) {
                        requestHistoryAlbumsData(Constant.UP, mAlbumList.get(0).getAlbumid());
                    } else {
                        requestHistoryAlbumsData(Constant.FRIST, Constant.FRIST_ID);
                    }
                    mSwipeRefreshLayout.setRefreshing(false);
                    isErr = false;
                }
            }
        }, Constant.DELAY_MILLIS);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.rl_mine_download:
                ((BaseActivity) getActivity()).startActivityForNew(
                        new Intent(getActivity(), DownloadActivity.class));
                break;
            case R.id.rl_mine_fav:
                if (UserAuth.getInstance().isLogin(mContext)) {
                    ((BaseActivity) getActivity()).startActivityForNew(
                            new Intent(getActivity(), FavActivity.class));
                } else {
                    ((BaseActivity) getActivity()).startActivityForNew(
                            new Intent(getActivity(), LoginActivity.class));
                }
                break;
            default:
                break;
        }
    }

    public void onEventMainThread(HistoryEvent historyEvent) {

        mHistoryEvents.add(historyEvent);
    }

    public void onEventMainThread(LoginEvent loginEvent) {

        mDBHistoryAlbum = HistoryDao.getInstance().getHistoryAlbums();
        mAlbumList.clear();
        hideEmptyTip();
        requestHistoryAlbumsData(Constant.FRIST, Constant.FRIST_ID);
    }

    public void onEventMainThread(FavEvent favEvent) {

        String favCount = mFavCountTv.getText().toString();
        int fav = 0;
        if (!"".equals(favCount)) {
            fav = Integer.parseInt(favCount);
        }
        fav = favEvent.fav == 1 ? fav + 1 : fav - 1;
        mFavCountTv.setText(fav + "");
    }

    @Override
    public void notifyDownloadCountChanged(final int downloadCount) {
        if (mDownloadCountTv != null) {
            mDownloadCountTv.setText((downloadCount >= 0 ? downloadCount : 0) + "");
        }
    }
}