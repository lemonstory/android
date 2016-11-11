package com.xiaoningmeng;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.xiaoningmeng.adapter.AblumCommentAdapter;
import com.xiaoningmeng.application.MyApplication;
import com.xiaoningmeng.auth.UserAuth;
import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.bean.Comment;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.http.JsonCallback;
import com.xiaoningmeng.http.LHttpRequest;
import com.xiaoningmeng.manager.EmptyHelper;

import java.util.ArrayList;
import java.util.List;

public class AlbumCommentActivity extends BaseActivity implements BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private EmptyHelper mEmptyHelper;
    private AblumCommentAdapter mAdapter;
    private List<Comment.ItemsBean> mCurrentComments = new ArrayList<>();
    private String mAblumId;
    private String mStartCommentId = "0";
    private int pageSize = 20;
    private View footerView;
    private boolean isErr;
    private View notLoadingView;
    private int delayMillis = 1000;
    private int mCurrentCounter = 0;
    private int mTotalCounter = 0;

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

        mEmptyHelper = new EmptyHelper(this, mRecyclerView, mAdapter);
        mEmptyHelper.setEmptyView(EmptyHelper.LOADING, true, getString(R.string.loading_tip));
        footerView = this.getListEndView();
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnLoadMoreListener(this);
        mAdapter.openLoadMore(pageSize);
        isErr = false;
        AlbumCommentActivity.this.requestAlbumCommentData(Constant.DOWN, mStartCommentId, false);

        mRecyclerView.addOnItemTouchListener(
                new OnItemChildClickListener() {
                    @Override
                    public void SimpleOnItemChildClick(BaseQuickAdapter adapter, View view, int position) {

                        Comment.ItemsBean item = (Comment.ItemsBean) adapter.getData().get(position);
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

        LHttpRequest.getInstance().albumCommentReq(this, mAblumId, direction, startId, pageSize,
                MyApplication.getInstance().getUid(),
                new JsonCallback<Comment>() {

                    @Override
                    public void onGetDataSuccess(Comment data) {

                        if (data != null) {
                            if (data.getTotal() != null && Integer.parseInt(data.getTotal()) > 0) {
                                mTotalCounter = Integer.parseInt(data.getTotal());
                                String title = String.format("评论(%s)", mTotalCounter);
                                AlbumCommentActivity.this.setTitleName(title);
                            }

                            if (data.getItems() != null && data.getItems().size() > 0) {
                                mCurrentCounter = data.getItems().size();
                                mCurrentComments = data.getItems();
                                Comment.ItemsBean currentCommentItem = data.getItems().get(data.getItems().size() - 1);
                                mStartCommentId = currentCommentItem.getId();
                                if (isRefreshing) {
                                    mAdapter.setNewData(mCurrentComments);
                                } else {
                                    mAdapter.addData(mCurrentComments);
                                }
                                mCurrentCounter = mAdapter.getData().size();
                            } else {
                                mEmptyHelper.setEmptyView(EmptyHelper.EMPTY, true, getString(R.string.empty_album_comment));
                            }

                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String failureResponse) {
                        isErr = true;
                        Toast.makeText(AlbumCommentActivity.this, R.string.network_err, Toast.LENGTH_LONG).show();
                        mAdapter.showLoadMoreFailedView();
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                    }
                });
    }

    private void writeComment() {
        if (UserAuth.auditUser(this, "登录后,才能评论故事喔.")) {
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
                mAdapter.removeAllFooterView();
                mSwipeRefreshLayout.setRefreshing(false);
                isErr = false;
            }
        }, delayMillis);
    }

    @Override
    public void onLoadMoreRequested() {

        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {

                if (mCurrentCounter >= mTotalCounter) {
                    mAdapter.loadComplete();
                    if (notLoadingView == null) {
                        notLoadingView = getLayoutInflater().inflate(R.layout.list_end_view, (ViewGroup) mRecyclerView.getParent(), false);
                    }
                    mAdapter.addFooterView(notLoadingView);
                } else {
                    if (!isErr) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                AlbumCommentActivity.this.requestAlbumCommentData(Constant.DOWN, mStartCommentId, false);
                            }
                        }, delayMillis);
                    } else {
                        isErr = true;
                        Toast.makeText(AlbumCommentActivity.this, R.string.network_err, Toast.LENGTH_LONG).show();
                        mAdapter.showLoadMoreFailedView();
                    }
                }
            }
        });
    }
}