package com.xiaoningmeng;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.umeng.analytics.MobclickAgent;
import com.xiaoningmeng.adapter.AuthorAlbumsAdapter;
import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.bean.AlbumInfo;
import com.xiaoningmeng.bean.Author;
import com.xiaoningmeng.bean.AuthorAlbums;
import com.xiaoningmeng.bean.PlayingStory;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.http.JsonResponse;
import com.xiaoningmeng.http.LHttpRequest;
import com.xiaoningmeng.manager.EmptyHelper;
import com.xiaoningmeng.manager.PlayWaveManager;
import com.xiaoningmeng.player.PlayObserver;
import com.xiaoningmeng.player.PlayerManager;
import com.xiaoningmeng.utils.DebugUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.xiaoningmeng.http.LHttpRequest.mRetrofit;

public class AuthorAlbumsActivity extends BaseActivity implements BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener, PlayObserver {

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private EmptyHelper mEmptyHelper;
    private ImageView mWaveImg;
    private AuthorAlbumsAdapter mAdapter;
    private List<AlbumInfo> mCurrentAlbums;
    private int mTotalCounter;
    private int mCurrentCounter;

    private int pageSize = 50;
    //单屏显示的数据量
    private int singleScreenItemNum = 3;
    private int mAuthorId = 0;
    private Author mAuthorObj = null;
    private int mStartAlbumId = 0;
    private boolean isErr;
    private View notLoadingView;
    private View mHeaderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_albums);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.id_stickynavlayout_innerscrollview);
        mWaveImg = (ImageView) findViewById(R.id.img_head_right);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        PlayerManager.getInstance().register(this);
        setRightHeadIcon(R.drawable.ic_player_flag_wave_01);
        Intent intent = this.getIntent();
        parseIntent(intent);
        initAdapter();
        mHeaderView = getHeaderView();
        mAdapter.addHeaderView(mHeaderView);
        this.setHeaderViewData(mAuthorObj);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        parseIntent(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();
        PlayWaveManager.getInstance().loadWaveAnim(this, mWaveImg);
        MobclickAgent.onEvent(this, "event_author_albums");
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

    private void parseIntent(Intent intent) {

        String authorUidStr = intent.getStringExtra("author_uid");
        if (null != authorUidStr) {
            this.mAuthorId = Integer.parseInt(authorUidStr);
        }
        this.mAuthorObj = intent.getParcelableExtra("author");
    }

    public void initAdapter() {

        mAdapter = new AuthorAlbumsAdapter(mCurrentAlbums);
        mEmptyHelper = new EmptyHelper(this, mRecyclerView, mAdapter);
        mEmptyHelper.setEmptyView(EmptyHelper.LOADING, true, getString(R.string.loading_tip));
        mAdapter.setOnLoadMoreListener(this);
        //mAdapter.openLoadMore(pageSize);
        isErr = false;
        mRecyclerView.setAdapter(mAdapter);
        AuthorAlbumsActivity.this.requestAuthorAlbumsData(mAuthorId, mStartAlbumId, pageSize, true);

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
                        AlbumInfo albumInfo = mAdapter.getItem(position);
                        startAlbumInfoActivity(view, albumInfo);
                    }
                }
        );
    }


    private void requestAuthorAlbumsData(int authorId, int startAlbumId, final int pageSize, final Boolean isRefreshing) {


        LHttpRequest.GetAuthorAlbumsRequest getAuthorAlbumsRequest = mRetrofit.create(LHttpRequest.GetAuthorAlbumsRequest.class);
        Call<JsonResponse<AuthorAlbums>> call = getAuthorAlbumsRequest.getResult(authorId, startAlbumId, pageSize);
        call.enqueue(new Callback<JsonResponse<AuthorAlbums>>() {

            @Override
            public void onResponse(Call<JsonResponse<AuthorAlbums>> call, Response<JsonResponse<AuthorAlbums>> response) {

                if (response.isSuccessful() && response.body().isSuccessful()) {

                    AuthorAlbums data = response.body().getData();
                    if (data != null) {

                        mAuthorObj = data.getInfo();
                        setHeaderViewData(mAuthorObj);
                        AuthorAlbumsActivity.this.setTitleName(data.getInfo().getNickname());
                        if (data.getTotal() > 0) {
                            mTotalCounter = data.getTotal();
                        }
                        if (data.getItems() != null && data.getItems().size() > 0) {

                            mCurrentCounter = data.getItems().size();
                            mCurrentAlbums = data.getItems();
                            AlbumInfo lastAlbumItem = data.getItems().get(data.getItems().size() - 1);
                            mStartAlbumId = Integer.parseInt(lastAlbumItem.getId());

                            if (isRefreshing) {

                                mAdapter.setNewData(mCurrentAlbums);
                            } else {

                                mAdapter.addData(mCurrentAlbums);
                            }

                            //数量不足page_size 显示加载完成view
                            if (mCurrentCounter < pageSize && mTotalCounter > singleScreenItemNum) {
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
                    Toast.makeText(AuthorAlbumsActivity.this, R.string.network_err, Toast.LENGTH_LONG).show();
                    if (mCurrentCounter > 0) {
                        mAdapter.loadMoreFail();
                    } else {
                        mEmptyHelper.setEmptyView(EmptyHelper.EMPTY, true, getString(R.string.empty_tip));
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonResponse<AuthorAlbums>> call, Throwable t) {

                DebugUtils.e(t.toString());
                isErr = true;
                Toast.makeText(AuthorAlbumsActivity.this, R.string.network_err, Toast.LENGTH_LONG).show();
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
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                AuthorAlbumsActivity.this.requestAuthorAlbumsData(mAuthorId, mStartAlbumId, pageSize, false);
                            }
                        }, Constant.DELAY_MILLIS);
                    } else {
                        isErr = true;
                        Toast.makeText(AuthorAlbumsActivity.this, R.string.network_err, Toast.LENGTH_LONG).show();
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
                mStartAlbumId = 0;
                AuthorAlbumsActivity.this.requestAuthorAlbumsData(mAuthorId, mStartAlbumId, pageSize, true);
                mAdapter.removeAllFooterView();
                mSwipeRefreshLayout.setRefreshing(false);
                isErr = false;
            }
        }, Constant.DELAY_MILLIS);
    }

    public void startAlbumInfoActivity(View view, AlbumInfo albumInfo) {

        Intent intent = new Intent(this, AlbumDetailActivity.class);
        intent.putExtra("albumId", albumInfo.getId());
        intent.putExtra("albumInfo", albumInfo);

        ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
                ((BaseActivity) this),
                new Pair<View, String>(view.findViewById(R.id.img_album_cover), "albumImage")
        );
        Bundle bundle = activityOptions.toBundle();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

            startActivity(intent, bundle);
        } else {
            startActivity(intent);
        }
    }

    private View getHeaderView() {

        View view = getLayoutInflater().inflate(R.layout.header_author_albums, null);
        view.setLayoutParams(new DrawerLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return view;
    }

    private void setHeaderViewData(final Author author) {

        if (null != mHeaderView) {

            SimpleDraweeView coverImg = (SimpleDraweeView) mHeaderView.findViewById(R.id.img_avatar);
            String albumCover = author.getAvatar();
            if (URLUtil.isValidUrl(albumCover)) {
                Uri coverImgUri = Uri.parse(albumCover);
                coverImg.setImageURI(coverImgUri);
            }

            TextView authorTvName = (TextView) mHeaderView.findViewById(R.id.tv_author_name);
            authorTvName.setText(author.getNickname());

            TextView authorTvIntro = (TextView) mHeaderView.findViewById(R.id.tv_author_intro);
            String intro = "";
            if (null != author.getIntro()) {
                intro = author.getIntro().trim();
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                authorTvIntro.setText(Html.fromHtml(intro, Html.FROM_HTML_MODE_LEGACY));
            } else {
                authorTvIntro.setText(Html.fromHtml(intro));
            }
            final String authorWikiUrl = author.getWiki_url();
            if (URLUtil.isValidUrl(authorWikiUrl)) {

                mHeaderView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        DebugUtils.d("authorWikiUrl = " + authorWikiUrl);
                        Uri authorWikiUri = Uri.parse(authorWikiUrl);
                        Intent adIntent = new Intent();
                        adIntent.setData(authorWikiUri);
                        adIntent.addCategory(Constant.DEFAULT_INTENT_CATEGORY);
                        AuthorAlbumsActivity.this.startActivity(adIntent);
                    }
                });
            }
        }
    }

    @Override
    public void notify(PlayingStory music) {
        PlayWaveManager.getInstance().notify(music);
    }
}