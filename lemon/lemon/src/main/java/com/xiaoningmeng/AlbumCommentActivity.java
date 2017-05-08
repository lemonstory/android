package com.xiaoningmeng;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.orhanobut.logger.Logger;
import com.xiaoningmeng.adapter.AblumCommentAdapter;
import com.xiaoningmeng.auth.UserAuth;
import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.bean.Comment;
import com.xiaoningmeng.bean.CommentList;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.http.JsonResponse;
import com.xiaoningmeng.http.LHttpRequest;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.xiaoningmeng.http.LHttpRequest.mRetrofit;

public class AlbumCommentActivity extends BaseActivity implements BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private View notDataView;
    private AblumCommentAdapter mAdapter;
    private List<Comment> mCurrentComments = new ArrayList<>();
    private String mAblumId;
    private String mStartCommentId = "0";
    private int pageSize = 20;
    private boolean isErr;
    private int mCurrentCounter = 0;
    private int mTotalCounter = 0;
    private int singleScreenItemNum = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ablum_comment);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mAdapter = new AblumCommentAdapter(mCurrentComments);
        mRecyclerView = (RecyclerView) findViewById(R.id.id_stickynavlayout_innerscrollview);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.setRightHeadIcon(R.drawable.write_comment);
        this.findViewById(R.id.img_head_right).setOnClickListener(writeCommentIconClickListener);
        Intent intent = this.getIntent();
        mAblumId = intent.getStringExtra("albumId");
        initAdapter();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initAdapter() {

        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnLoadMoreListener(this);
        mAdapter.setAutoLoadMoreSize(pageSize);
        isErr = false;
        notDataView = getLayoutInflater().inflate(R.layout.empty_view, (ViewGroup) mRecyclerView.getParent(), false);
        AlbumCommentActivity.this.requestAlbumCommentData(Constant.DOWN, mStartCommentId, false);
        mRecyclerView.addOnItemTouchListener(
                new OnItemChildClickListener() {
                    @Override
                    public void onSimpleItemChildClick(BaseQuickAdapter adapter, View view, int position) {

                        Comment item = (Comment) adapter.getData().get(position);
                        String uid = item.getUid();
                        Intent i = new Intent(AlbumCommentActivity.this, PerasonalActivity.class);
                        i.putExtra("uid", uid);
                        startActivityForNew(i);
                    }

                    @Override
                    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

                        super.onItemChildClick(adapter, view, position);
                    }

                    @Override
                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                        super.onItemClick(adapter, view, position);
                    }
                }
        );
    }

    private View.OnClickListener writeCommentIconClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            writeComment();
        }
    };

    private void requestAlbumCommentData(final String direction, String startId, final Boolean isRefreshing) {

        LHttpRequest.AlbumCommentRequest albumCommentRequest = mRetrofit.create(LHttpRequest.AlbumCommentRequest.class);
        Call<JsonResponse<CommentList>> call = albumCommentRequest.getResult(mAblumId, direction, startId, pageSize);
        call.enqueue(new Callback<JsonResponse<CommentList>>() {

            @Override
            public void onResponse(Call<JsonResponse<CommentList>> call, Response<JsonResponse<CommentList>> response) {

                if (response.isSuccessful() && response.body().isSuccessful()) {

                    CommentList data = response.body().getData();
                    if (data != null) {

                        if (data.getTotal() != null && Integer.parseInt(data.getTotal()) >= 0) {
                            mTotalCounter = Integer.parseInt(data.getTotal());
                            String title = String.format("评论(%s)", mTotalCounter);
                            AlbumCommentActivity.this.setTitleName(title);
                        }

                        if (data.getItems() != null && data.getItems().size() > 0) {
                            mCurrentCounter = data.getItems().size();
                            mCurrentComments = data.getItems();
                            Comment currentCommentItem = data.getItems().get(data.getItems().size() - 1);
                            mStartCommentId = currentCommentItem.getId();
                            if (isRefreshing) {
                                mAdapter.setNewData(mCurrentComments);
                            } else {
                                mAdapter.addData(mCurrentComments);
                            }
                            mCurrentCounter = mAdapter.getData().size();

                            //数量不足page_size 显示加载完成view
                            if (mCurrentCounter == mTotalCounter && mCurrentCounter < pageSize) {
                                mAdapter.loadMoreEnd(singleScreenItemNum >= mTotalCounter);
                            }

                        } else {
                            TextView emptyTip = (TextView) notDataView.findViewById(R.id.tv_empty_tip);
                            emptyTip.setText(getString(R.string.empty_album_comment));
                            mAdapter.setEmptyView(notDataView);
                        }
                    }

                } else {
                    isErr = true;
                    Toast.makeText(AlbumCommentActivity.this, R.string.network_err, Toast.LENGTH_LONG).show();
                    mAdapter.loadMoreFail();
                    Logger.e(response.toString());
                }
            }

            @Override
            public void onFailure(Call<JsonResponse<CommentList>> call, Throwable t) {

                Logger.e(t.toString());
                isErr = true;
                Toast.makeText(AlbumCommentActivity.this, R.string.network_err, Toast.LENGTH_LONG).show();
                mAdapter.loadMoreFail();
            }
        });
    }

    private void writeComment() {

        if (UserAuth.getInstance().isLogin(this)) {
            Intent i = new Intent(this, CommentWriteActivity.class);
            i.putExtra("albumId", mAblumId);
            startActivityForResult(i, 0);
        }
    }

    @Override
    public void onRefresh() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mStartCommentId = "0";
                AlbumCommentActivity.this.requestAlbumCommentData(Constant.DOWN, mStartCommentId, true);
                mSwipeRefreshLayout.setRefreshing(false);
                isErr = false;
            }
        }, Constant.DELAY_MILLIS);
    }

    @Override
    public void onLoadMoreRequested() {

        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (mCurrentCounter >= mTotalCounter) {
                    mAdapter.loadMoreEnd();
                } else {
                    if (!isErr) {
                        AlbumCommentActivity.this.requestAlbumCommentData(Constant.DOWN, mStartCommentId, false);
                        mAdapter.loadMoreComplete();
                    } else {
                        isErr = true;
                        Toast.makeText(AlbumCommentActivity.this, R.string.network_err, Toast.LENGTH_LONG).show();
                        mAdapter.loadMoreFail();
                    }
                }
            }
        }, Constant.DELAY_MILLIS);
    }
}