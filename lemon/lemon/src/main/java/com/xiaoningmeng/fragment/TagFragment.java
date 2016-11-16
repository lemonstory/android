package com.xiaoningmeng.fragment;

import android.content.Intent;
import android.os.Bundle;
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
import com.xiaoningmeng.bean.TagAlbum;
import com.xiaoningmeng.bean.TagDetail;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.http.JsonCallback;
import com.xiaoningmeng.http.LHttpRequest;

import java.util.ArrayList;
import java.util.List;

public class TagFragment extends LazyFragment
        implements SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {

    private RecyclerView mRecyclerView;
    private AlbumAdapter mQuickAdapter;
    private SwipeRefreshLayout mRefreshLayout;
    private List<AlbumInfo> mAlbumInfos;
    private List<TagAlbum> mTagAlbums;
    private TagActivity.TagParam mTagParam;
    private boolean isAttach;
    private boolean isPrepared;
    private LayoutInflater mInflater;
    private View notLoadingView;
    private Boolean albumClickable;

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
        mRecyclerView = (RecyclerView) contentView.findViewById(R.id.rv_list);
        mRefreshLayout = (SwipeRefreshLayout) contentView.findViewById(R.id.swipeLayout);
        mRecyclerView.setHasFixedSize(true);
        mRefreshLayout.setOnRefreshListener(this);
        this.spanCount = 2;
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
        mQuickAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        mQuickAdapter.setOnLoadMoreListener(this);
        mQuickAdapter.openLoadMore(20);
        setEmptyView(true);
        mQuickAdapter.isFirstOnly(true);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(18));
        mRecyclerView.addOnItemTouchListener(new OnItemChildClickListener() {

            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                super.onItemChildClick(adapter, view, position);
                if (position >= 0 && albumClickable) {
                    AlbumInfo albumInfo = (AlbumInfo) adapter.getItem(position);
                    Intent intent = new Intent(getActivity(), AlbumDetailActivity.class);
                    intent.putExtra("albumId", albumInfo.getAlbumid());
                    intent.putExtra("albumInfo", albumInfo);
                    ((BaseActivity) getActivity()).startShareTransitionActivity(intent, view, "albumImage");
                    albumClickable = false;
                }
            }

            @Override
            public void SimpleOnItemChildClick(BaseQuickAdapter adapter, View view, int position) {

            }
        });
        mRecyclerView.setAdapter(mQuickAdapter);
    }

    public void requestData(final String direction, String relationId, final boolean isReafresh) {

        LHttpRequest.getInstance().getTagAblumListReq(getActivity(), mTagParam.tag, 0, direction, relationId,
                mTagParam.special, Constant.GRID_REQ_LEN, new JsonCallback<TagDetail>() {
                    @Override
                    public void onGetDataSuccess(TagDetail data) {
                        isLoadData = true;
                        if (isReafresh) {
                            mAlbumInfos.clear();
                            mTagAlbums.clear();
                        }
                        if (direction == Constant.FRIST || direction == Constant.UP) {
                            mRefreshLayout.setRefreshing(false);
                            if (data == null || data.getTagalbumlist() == null
                                    || data.getTagalbumlist().size() == 0) {
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
                            if (direction == Constant.DOWN && tagAlbumList.size() == 0) {
                                mQuickAdapter.loadComplete();
                                if (notLoadingView == null) {
                                    notLoadingView = TagFragment.this.getActivity().getLayoutInflater().inflate(R.layout.list_end_view, (ViewGroup) mRecyclerView.getParent(), false);
                                }
                                mQuickAdapter.addFooterView(notLoadingView);

                            }
                            mQuickAdapter.addData(albumInfos);
                        }
                    }

                    @Override
                    public void onFailure(String responseString) {
                        isLoadData = true;
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
}
