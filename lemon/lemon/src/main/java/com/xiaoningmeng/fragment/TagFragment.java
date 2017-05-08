package com.xiaoningmeng.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.orhanobut.logger.Logger;
import com.umeng.analytics.MobclickAgent;
import com.xiaoningmeng.AlbumDetailActivity;
import com.xiaoningmeng.R;
import com.xiaoningmeng.TagActivity;
import com.xiaoningmeng.adapter.AlbumAdapter;
import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.base.LazyFragment;
import com.xiaoningmeng.bean.AlbumInfo;
import com.xiaoningmeng.bean.TagAblumList;
import com.xiaoningmeng.bean.TagAlbum;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.http.JsonResponse;
import com.xiaoningmeng.http.LHttpRequest;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.xiaoningmeng.http.LHttpRequest.mRetrofit;

public class TagFragment extends LazyFragment implements SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {

    private Context mContext;
    private RecyclerView mRecyclerView;
    private AlbumAdapter mQuickAdapter;
    private SwipeRefreshLayout mRefreshLayout;
    private List<AlbumInfo> mAlbumInfos;
    private List<TagAlbum> mTagAlbums;
    private List<TagAlbum> mCurrentTagAlbums;
    private TagActivity.TagParam mTagParam;
    private boolean isAttach;
    private boolean isPrepared;
    private LayoutInflater mInflater;
    private Boolean albumClickable;
    private int PageSize = Constant.GRID_REQ_LEN;
    private int singleScreenItemNum = 6;
    private boolean isErr;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = View.inflate(getActivity(),
                R.layout.fragment_tag, null);
        if (!isAttach) {
            mTagParam = getArguments().getParcelable(TagActivity.Fragment_Tag);
        }
        isAttach = false;
        isLoadData = false;
        mContext = this.getActivity();
        mRecyclerView = (RecyclerView) contentView.findViewById(R.id.rv_list);
        mRefreshLayout = (SwipeRefreshLayout) contentView.findViewById(R.id.swipeLayout);
        mRecyclerView.setHasFixedSize(true);
        mRefreshLayout.setOnRefreshListener(this);
        int spanCount = 2;
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), spanCount));
        mAlbumInfos = new ArrayList<>();
        mTagAlbums = new ArrayList<>();
        mCurrentTagAlbums = new ArrayList<>();
        mInflater = inflater;
        initAdapter();
        isPrepared = true;
        lazyLoad();
        return contentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            MobclickAgent.onEvent(getActivity(), "event_show_tag");
        }
        albumClickable = true;
    }

    private void initAdapter() {

        mQuickAdapter = new AlbumAdapter(mAlbumInfos);
        mQuickAdapter.setOnLoadMoreListener(this);
        mQuickAdapter.setAutoLoadMoreSize(PageSize);
        isErr = false;
        mQuickAdapter.isFirstOnly(true);
        TagFragment.ItemOffsetDecoration itemDecoration = new TagFragment.ItemOffsetDecoration(mContext, R.dimen.page_offset, R.dimen.item_offset);
        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.addOnItemTouchListener(new OnItemChildClickListener() {

            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                super.onItemChildClick(adapter, view, position);
                if (position >= 0 && albumClickable) {
                    AlbumInfo albumInfo = (AlbumInfo) adapter.getItem(position);
                    Intent intent = new Intent(getActivity(), AlbumDetailActivity.class);
                    intent.putExtra("albumId", albumInfo.getId());
                    intent.putExtra("albumInfo", albumInfo);
                    ((BaseActivity) getActivity()).startShareTransitionActivity(intent, view, "albumImage");
                    albumClickable = false;
                }
            }

            @Override
            public void onSimpleItemChildClick(BaseQuickAdapter adapter, View view, int position) {

            }
        });
        mRecyclerView.setAdapter(mQuickAdapter);
    }

    public void requestData(final String direction, final String relationId, final boolean isReafresh) {

        int isSpecialtTag = 0;
        if (mTagParam.special != null) {
            isSpecialtTag = 1;
        }
        LHttpRequest.GetTagAblumListRequest getTagAblumListRequest = mRetrofit.create(LHttpRequest.GetTagAblumListRequest.class);
        Call<JsonResponse<TagAblumList>> call = getTagAblumListRequest.getResult(mTagParam.tag, 0, direction, relationId, isSpecialtTag, PageSize);
        call.enqueue(new Callback<JsonResponse<TagAblumList>>() {

            @Override
            public void onResponse(Call<JsonResponse<TagAblumList>> call, Response<JsonResponse<TagAblumList>> response) {

                if (response.isSuccessful() && response.body().isSuccessful()) {

                    TagAblumList data = response.body().getData();
                    isLoadData = true;
                    if (isReafresh) {
                        mAlbumInfos.clear();
                        mTagAlbums.clear();
                    }
                    if (direction == Constant.FRIST || direction == Constant.UP) {
                        mRefreshLayout.setRefreshing(false);
                        if (data == null || data.getTagalbumlist() == null || data.getTagalbumlist().size() == 0) {
                            mQuickAdapter.setEmptyView(R.layout.empty_view, (ViewGroup) mRecyclerView.getParent());
                        }
                    }
                    if (data != null && data.getTagalbumlist() != null) {
                        mCurrentTagAlbums = data.getTagalbumlist();
                        List<AlbumInfo> albumInfos = new ArrayList<>();
                        for (TagAlbum tagAlbum : mCurrentTagAlbums) {
                            albumInfos.add(tagAlbum.getAlbuminfo());
                        }
                        mTagAlbums.addAll(mCurrentTagAlbums);
                        mQuickAdapter.addData(albumInfos);
                    }
                } else {

                    Logger.e(response.toString());
                    isLoadData = true;
                }
            }

            @Override
            public void onFailure(Call<JsonResponse<TagAblumList>> call, Throwable t) {
                isErr = true;
                if (null != getActivity() && TagFragment.this.isAdded()) {
                    Toast.makeText(getActivity(), R.string.network_err, Toast.LENGTH_LONG).show();
                }
                mQuickAdapter.loadMoreFail();
            }
        });
    }

    public void refreshData(TagActivity.TagParam tagParam) {
        isAttach = true;
        isLoadData = false;
        mTagParam = tagParam;
    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible) {
            return;
        }
        mRefreshLayout.setRefreshing(true);
        onRefresh();
    }

    @Override
    public void onRefresh() {

        mQuickAdapter.setEmptyView(R.layout.loading_view, (ViewGroup) mRecyclerView.getParent());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mTagAlbums.size() > 0) {
                    String relationId = mTagAlbums.get(0).getId();
                    if (relationId != null) {
                        requestData(Constant.UP, relationId, false);
                    }
                } else {
                    requestData(Constant.FRIST, Constant.FRIST_ID, false);
                }
                mRefreshLayout.setRefreshing(false);
                isErr = false;
            }
        }, Constant.DELAY_MILLIS);
    }

    @Override
    public void onLoadMoreRequested() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (!isErr) {
                    if (mCurrentTagAlbums.size() < PageSize) {
                        //http://stackoverflow.com/questions/11631408/android-fragment-getactivity-sometime-returns-null
                        mQuickAdapter.loadMoreEnd(singleScreenItemNum >= mTagAlbums.size());
                    } else {

                        int size = mTagAlbums.size();
                        if (size > 0) {
                            String relationId = mTagAlbums.get(size - 1).getId();
                            if (relationId != null) {
                                requestData(Constant.DOWN, relationId, false);
                            }
                        }
                        mQuickAdapter.loadMoreComplete();
                    }
                } else {
                    isErr = true;
                    Toast.makeText(getActivity(), R.string.network_err, Toast.LENGTH_LONG).show();
                    mQuickAdapter.loadMoreFail();
                }
            }
        }, Constant.DELAY_MILLIS);
    }

    //http://stackoverflow.com/questions/28531996/android-recyclerview-gridlayoutmanager-column-spacing
    public class ItemOffsetDecoration extends RecyclerView.ItemDecoration {

        private int mPageOffset;
        private int mItemOffset;
        private int mSpanCount = 2;


        public ItemOffsetDecoration(int pageOffset, int itemOffset) {

            mPageOffset = pageOffset;
            mItemOffset = itemOffset;
        }

        public ItemOffsetDecoration(@NonNull Context context, @DimenRes int pageOffsetId, @DimenRes int itemOffsetId) {

            this(mContext.getResources().getDimensionPixelSize(pageOffsetId), mContext.getResources().getDimensionPixelSize(itemOffsetId));
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

            super.getItemOffsets(outRect, view, parent, state);
            int pos = parent.getChildAdapterPosition(view);
            int left = 0;
            int right = 0;
            int top = 0;
            int bottom = 0;
            if (pos % mSpanCount == 0) {
                left = mPageOffset;
                right = mItemOffset;
            } else {
                left = mItemOffset;
                right = mPageOffset;
            }
            outRect.set(left, top, right, bottom);
        }
    }
}
