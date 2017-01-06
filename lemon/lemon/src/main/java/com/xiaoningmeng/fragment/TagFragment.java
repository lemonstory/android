package com.xiaoningmeng.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
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
import com.xiaoningmeng.utils.DebugUtils;

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
    private TagActivity.TagParam mTagParam;
    private boolean isAttach;
    private boolean isPrepared;
    private LayoutInflater mInflater;
    private View mFooterView;
    private Boolean albumClickable;
    private int PageSize = 20;

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
        //mQuickAdapter.openLoadMore(PageSize);
        setEmptyView(true);
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
        Call<JsonResponse<TagAblumList>> call = getTagAblumListRequest.getResult(mTagParam.tag, 0, direction, relationId, isSpecialtTag, Constant.GRID_REQ_LEN);
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
                            setEmptyView(false);
                        }
                    }
                    if (data != null && data.getTagalbumlist() != null) {
                        List<TagAlbum> tagAlbumList = data.getTagalbumlist();
                        List<AlbumInfo> albumInfos = new ArrayList<>();
                        for (TagAlbum tagAlbum : tagAlbumList) {
                            albumInfos.add(tagAlbum.getAlbuminfo());
                        }
                        mTagAlbums.addAll(tagAlbumList);
                        mQuickAdapter.addData(albumInfos);

                        if (tagAlbumList.size() < Constant.GRID_REQ_LEN) {

                            if (mFooterView != null && mFooterView.getParent() != null) {
                                {
                                    ((ViewGroup) mFooterView.getParent()).removeView(mFooterView);
                                }
                            }

                            mQuickAdapter.loadMoreComplete();
                            //http://stackoverflow.com/questions/11631408/android-fragment-getactivity-sometime-returns-null
                            if (null != TagFragment.this.getActivity() && TagFragment.this.isAdded()) {
                                if (mFooterView == null) {
                                    mFooterView = TagFragment.this.getActivity().getLayoutInflater().inflate(R.layout.list_footer_view, (ViewGroup) mRecyclerView.getParent(), false);
                                }
                                mQuickAdapter.addFooterView(mFooterView);
                            }

                        }
                    }
                } else {

                    DebugUtils.e(response.toString());
                    isLoadData = true;
                }
            }

            @Override
            public void onFailure(Call<JsonResponse<TagAblumList>> call, Throwable t) {

                DebugUtils.e(t.toString());
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
        requestData(Constant.FRIST, Constant.FRIST_ID, true);
    }

    @Override
    public void onRefresh() {

        if (mTagAlbums.size() > 0) {
            String relationId = mTagAlbums.get(0).getId();
            if (relationId != null) {
                requestData(Constant.UP, relationId, false);
            }
        } else {
            requestData(Constant.FRIST, Constant.FRIST_ID, false);
        }
    }

    @Override
    public void onLoadMoreRequested() {
        int size = mTagAlbums.size();
        if (size > 0) {
            String relationId = mTagAlbums.get(size - 1).getId();
            if (relationId != null) {
                requestData(Constant.DOWN, relationId, false);
            }
        } else {
            requestData(Constant.FRIST, Constant.FRIST_ID, false);
        }
    }

    public void setEmptyView(boolean isLoading) {
        View emptyView;
        if (isLoading) {
            emptyView = mInflater.inflate(R.layout.layout_loading2, (ViewGroup) mRecyclerView.getParent(), false);
        } else {
            emptyView = mInflater.inflate(R.layout.layout_empty, (ViewGroup) mRecyclerView.getParent(), false);
        }
        changedView(emptyView);
    }


    private void changedView(View view) {
        if (mQuickAdapter.getEmptyView() != view) {
            mQuickAdapter.setEmptyView(view);
            mQuickAdapter.notifyItemChanged(0);
        }
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
