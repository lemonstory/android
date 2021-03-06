package com.xiaoningmeng.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
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
import com.xiaoningmeng.R;
import com.xiaoningmeng.adapter.AlbumAdapter;
import com.xiaoningmeng.base.BaseFragment;
import com.xiaoningmeng.bean.AlbumInfo;

import java.util.ArrayList;
import java.util.List;

public class AblumSimilarFragment extends BaseFragment {

    private RecyclerView mRecyclerView;
    private List<AlbumInfo> albumList;
    private AlbumAdapter mAdapter;
    private Boolean albumClickable;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View contentView = View.inflate(getActivity(), R.layout.fragment_ablum_similar, null);
        mContext = this.getActivity();
        mRecyclerView = (RecyclerView) contentView.findViewById(R.id.id_stickynavlayout_innerscrollview);
        mRecyclerView.setHasFixedSize(true);
        int spanCount = 2;
        GridLayoutManager manager = new GridLayoutManager(getContext(), spanCount);
        mRecyclerView.setLayoutManager(manager);
        albumList = new ArrayList<>();
        initAdapter();
        mAdapter.setEmptyView(R.layout.loading_view, (ViewGroup) mRecyclerView.getParent());
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
        mRecyclerView.setAdapter(mAdapter);
        ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        AblumSimilarFragment.ItemOffsetDecoration itemDecoration = new AblumSimilarFragment.ItemOffsetDecoration(mContext, R.dimen.page_offset,R.dimen.item_offset);
        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.addOnItemTouchListener(
                new OnItemChildClickListener() {
                    @Override
                    public void onSimpleItemChildClick(BaseQuickAdapter adapter, View view, int position) {


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

    //http://stackoverflow.com/questions/28531996/android-recyclerview-gridlayoutmanager-column-spacing
    public class ItemOffsetDecoration extends RecyclerView.ItemDecoration {

        private int mPageOffset;
        private int mItemOffset;
        private int mSpanCount = 2;


        public ItemOffsetDecoration(int pageOffset,int itemOffset) {

            mPageOffset = pageOffset;
            mItemOffset = itemOffset;
        }

        public ItemOffsetDecoration(@NonNull Context context, @DimenRes int pageOffsetId, @DimenRes int itemOffsetId) {

            this(mContext.getResources().getDimensionPixelSize(pageOffsetId),mContext.getResources().getDimensionPixelSize(itemOffsetId));
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

