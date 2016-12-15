package com.xiaoningmeng.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.umeng.analytics.MobclickAgent;
import com.xiaoningmeng.AlbumDetailActivity;
import com.xiaoningmeng.adapter.AlbumAdapter;
import com.xiaoningmeng.bean.AlbumInfo;
import com.xiaoningmeng.manager.EmptyHelper;
import com.xiaoningmeng.R;
import com.xiaoningmeng.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

public class AblumSimilarFragment extends BaseFragment {

    private RecyclerView mRecyclerView;
    private List<AlbumInfo> albumList;
    private EmptyHelper mEmptyHelper;
    private AlbumAdapter mAdapter;
    private Boolean albumClickable;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View contentView = View.inflate(getActivity(), R.layout.fragment_ablum_similar, null);
        mRecyclerView = (RecyclerView) contentView.findViewById(R.id.id_stickynavlayout_innerscrollview);
        mRecyclerView.setHasFixedSize(true);
        this.spanCount = 2;
        GridLayoutManager manager = new GridLayoutManager(getContext(), spanCount);
        mRecyclerView.setLayoutManager(manager);
        albumList = new ArrayList<>();
        initAdapter();
        if (albumList.size() > 0) {
            loadingData(albumList);
        }
        return contentView;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            MobclickAgent.onEvent(getActivity(), "event_show_similar");
        }
        albumClickable = true;
    }

    public void setAlbumList(List<AlbumInfo> albumList) {

        loadingData(albumList);
    }

    private void loadingData(List<AlbumInfo> albumList) {

        this.albumList = albumList;
        if (mRecyclerView != null) {
            this.mAdapter.setNewData(this.albumList);
        }
    }

    public void initAdapter() {

        mAdapter = new AlbumAdapter(albumList);
        //mAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        mEmptyHelper = new EmptyHelper(getContext(), mRecyclerView, mAdapter);
        mEmptyHelper.setEmptyView(EmptyHelper.LOADING, false, getString(R.string.loading_tip));
        View footerView = this.getFooterView();
        mAdapter.addFooterView(footerView, 0);
        mRecyclerView.setAdapter(mAdapter);
        ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(18));
        mRecyclerView.addOnItemTouchListener(
                new OnItemChildClickListener() {
                    @Override
                    public void SimpleOnItemChildClick(BaseQuickAdapter adapter, View view, int position) {


                    }

                    @Override
                    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {


                    }

                    @Override
                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                        super.onItemClick(adapter, view, position);
                        AlbumInfo albumInfo = albumList.get(position);
                        //增加开关，避免连续点击，Activity跳转动画出现错误
                        if (albumClickable) {
                            startAlbumInfoActivity(view, albumInfo);
                            albumClickable = false;
                        }
                    }
                }
        );
    }

    public BaseQuickAdapter getAdapter() {
        return mAdapter;
    }

    public void startAlbumInfoActivity(View view, AlbumInfo albumInfo) {

        Intent intent = new Intent(getActivity(), AlbumDetailActivity.class);
        intent.putExtra("albumId", albumInfo.getId());
        intent.putExtra("albumInfo", albumInfo);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
    }
}

