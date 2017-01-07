package com.xiaoningmeng;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter.RequestLoadMoreListener;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.umeng.analytics.MobclickAgent;
import com.xiaoningmeng.adapter.AuthorAdapter;
import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.bean.Author;
import com.xiaoningmeng.bean.AuthorList;
import com.xiaoningmeng.bean.PlayingStory;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.http.JsonResponse;
import com.xiaoningmeng.http.LHttpRequest;
import com.xiaoningmeng.manager.EmptyHelper;
import com.xiaoningmeng.manager.PlayWaveManager;
import com.xiaoningmeng.player.PlayObserver;
import com.xiaoningmeng.player.PlayerManager;
import com.xiaoningmeng.utils.DebugUtils;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.xiaoningmeng.http.LHttpRequest.mRetrofit;

public class AuthorsActivity extends BaseActivity implements RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener, PlayObserver {

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private EmptyHelper mEmptyHelper;
    private ImageView mWaveImg;
    private AuthorAdapter mAdapter;
    private List<Author> mCurrentAuthors;
    private int mTotalCounter = 0;
    private int mCurrentCounter = 0;

    private View notLoadingView;
    private int mPage = 1;
    private int pageSize = 50;
    private boolean isErr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authors);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.id_stickynavlayout_innerscrollview);
        mWaveImg = (ImageView) findViewById(R.id.img_head_right);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        PlayerManager.getInstance().register(this);
        setRightHeadIcon(R.drawable.ic_player_flag_wave_01);
        setTitleName("作者");
        initAdapter();
    }

    @Override
    protected void onResume() {

        super.onResume();
        PlayWaveManager.getInstance().loadWaveAnim(this, mWaveImg);
        MobclickAgent.onEvent(this, "event_authors");
    }

    @Override
    protected void onPause() {
        super.onPause();
        PlayWaveManager.getInstance().mContext = null;
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        PlayerManager.getInstance().unRegister(this);
    }

    public void initAdapter() {

        mAdapter = new AuthorAdapter(R.layout.item_author_horizontal, mCurrentAuthors);
        mEmptyHelper = new EmptyHelper(this, mRecyclerView, mAdapter);
        mEmptyHelper.setEmptyView(EmptyHelper.LOADING, true, getString(R.string.loading_tip));
        mAdapter.setOnLoadMoreListener(this);
        //mAdapter.openLoadMore(pageSize);
        isErr = false;
        mRecyclerView.setAdapter(mAdapter);
        AuthorsActivity.this.requestAuthorsData(mPage, pageSize, true);

        mRecyclerView.addOnItemTouchListener(
                new OnItemChildClickListener() {
                    @Override
                    public void onSimpleItemChildClick(BaseQuickAdapter adapter, View view, int position) {

                    }

                    @Override
                    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

                        super.onItemChildClick(adapter, view, position);
                    }

                    @Override
                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                        super.onItemClick(adapter, view, position);
                        Author authorItem = mAdapter.getItem(position);
                        Intent intent = new Intent(AuthorsActivity.this, AuthorAlbumsActivity.class);
                        intent.putExtra("author_uid", authorItem.getUid());
                        intent.putExtra("author", authorItem);
                        startActivity(intent);

                        //计数统计
                        HashMap<String, String> authorMap = new HashMap<String, String>();
                        authorMap.put("uid", authorItem.getUid());
                        authorMap.put("nickname", authorItem.getNickname());
                        MobclickAgent.onEvent(AuthorsActivity.this, "event_click_author", authorMap);
                    }
                }
        );
    }

    private void requestAuthorsData(int page, final int pageSize, final Boolean isRefreshing) {

        LHttpRequest.GetAuthorsRequest getAuthorsRequest = mRetrofit.create(LHttpRequest.GetAuthorsRequest.class);
        Call<JsonResponse<AuthorList>> call = getAuthorsRequest.getResult(page, pageSize);
        call.enqueue(new Callback<JsonResponse<AuthorList>>() {

            @Override
            public void onResponse(Call<JsonResponse<AuthorList>> call, Response<JsonResponse<AuthorList>> response) {

                if (response.isSuccessful() && response.body().isSuccessful()) {

                    AuthorList data = response.body().getData();
                    if (data != null) {

                        if (data.getTotal() > 0) {
                            mTotalCounter = data.getTotal();
                        }
                        if (data.getItems() != null) {

                            if (data.getItems().size() > 0) {
                                mCurrentCounter = data.getItems().size();
                                mCurrentAuthors = data.getItems();

                                if (isRefreshing) {
                                    mAdapter.setNewData(mCurrentAuthors);
                                } else {
                                    mAdapter.addData(mCurrentAuthors);
                                }
                            }

                            //数量不足page_size 显示加载完成view
                            if (mCurrentCounter < pageSize) {
                                if (notLoadingView == null) {
                                    notLoadingView = getLayoutInflater().inflate(R.layout.list_footer_view, (ViewGroup) mRecyclerView.getParent(), false);
                                }
                                mAdapter.addFooterView(notLoadingView);
                            }

                        } else {
                            mEmptyHelper.setEmptyView(EmptyHelper.EMPTY, true, getString(R.string.empty_tip));
                        }
                    }

                } else {
                    DebugUtils.e(response.toString());
                    isErr = true;
                    Toast.makeText(AuthorsActivity.this, R.string.network_err, Toast.LENGTH_LONG).show();
                    if (mCurrentCounter > 0) {
                        mAdapter.loadMoreFail();
                    } else {
                        mEmptyHelper.setEmptyView(EmptyHelper.EMPTY, true, getString(R.string.empty_tip));
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonResponse<AuthorList>> call, Throwable t) {
                DebugUtils.e(t.toString());
                isErr = true;
                Toast.makeText(AuthorsActivity.this, R.string.network_err, Toast.LENGTH_LONG).show();
                if (mCurrentCounter > 0) {
                    mAdapter.loadMoreFail();
                } else {
                    mEmptyHelper.setEmptyView(EmptyHelper.EMPTY, true, getString(R.string.empty_tip));
                }
            }
        });
    }


    @Override
    public void onLoadMoreRequested() {

        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {

                if (mCurrentCounter >= mTotalCounter) {
                    mAdapter.loadMoreComplete();
                    if (notLoadingView == null) {
                        notLoadingView = getLayoutInflater().inflate(R.layout.list_footer_view, (ViewGroup) mRecyclerView.getParent(), false);
                    }
                    mAdapter.addFooterView(notLoadingView);
                } else {
                    if (!isErr) {
                        mPage = mPage + 1;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                AuthorsActivity.this.requestAuthorsData(mPage, pageSize, false);
                            }
                        }, Constant.DELAY_MILLIS);
                    } else {
                        isErr = true;
                        Toast.makeText(AuthorsActivity.this, R.string.network_err, Toast.LENGTH_LONG).show();
                        mAdapter.loadMoreFail();
                    }
                }
            }
        });
    }

    @Override
    public void onRefresh() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mPage = 1;
                AuthorsActivity.this.requestAuthorsData(mPage, pageSize, true);
                mAdapter.removeAllFooterView();
                mSwipeRefreshLayout.setRefreshing(false);
                isErr = false;
            }
        }, Constant.DELAY_MILLIS);
    }

    @Override
    public void notify(PlayingStory music) {
        PlayWaveManager.getInstance().notify(music);
    }
}
