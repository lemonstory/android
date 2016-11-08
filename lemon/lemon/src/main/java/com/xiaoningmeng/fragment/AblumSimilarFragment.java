package com.xiaoningmeng.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.umeng.analytics.MobclickAgent;
import com.xiaoningmeng.AblumDetailActivity;
import com.xiaoningmeng.R;
import com.xiaoningmeng.adapter.AlbumAdatper;
import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.base.BaseFragment;
import com.xiaoningmeng.bean.AlbumInfo;
import com.xiaoningmeng.manager.EmptyHelper;
import com.xiaoningmeng.utils.DebugUtils;

import java.util.ArrayList;
import java.util.List;

public class AblumSimilarFragment extends BaseFragment {

    private RecyclerView mRecyclerView;
    private List<AlbumInfo> albumList;
    private EmptyHelper mEmptyHelper;
    private AlbumAdatper mAdapter;
    private Boolean albumClickable;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        DebugUtils.d("AblumSimilarFragment ----> onCreateView RUN!!");
//        mContext = (AblumDetailActivity) getActivity();
        View contentView = View.inflate(getActivity(), R.layout.fragment_ablum_similar, null);
        mRecyclerView = (RecyclerView) contentView.findViewById(R.id.id_stickynavlayout_innerscrollview);
        mRecyclerView.setHasFixedSize(true);
        int spanCount = 2;
        GridLayoutManager manager = new GridLayoutManager(getContext(), spanCount);
        mRecyclerView.setLayoutManager(manager);
        albumList = new ArrayList<>();
        initAdapter();
        return contentView;
    }


    @Override
    public void onResume() {
        super.onResume();
        DebugUtils.d("AblumSimilarFragment ----> onResume RUN!!");
        if (albumList.size() > 0) {
            loadingData(albumList);
        }
        if (getActivity() != null) {
            MobclickAgent.onEvent(getActivity(), "event_show_similar");
        }
        albumClickable = true;
    }

    public void setAlbumList(List<AlbumInfo> albumList) {

        DebugUtils.d("AblumSimilarFragment ----> setAlbumList RUN!!");
        loadingData(albumList);
    }

    private void loadingData(List<AlbumInfo> albumList) {

        DebugUtils.d("AblumSimilarFragment ----> setAlbumList 111 RUN!!");
        DebugUtils.d("AblumSimilarFragment ----> setAlbumList mRecyclerView = " + mRecyclerView.toString());
        this.albumList = albumList;
        if (mRecyclerView != null) {

            DebugUtils.d("AblumSimilarFragment ----> loadingData 222 RUN!!");
            this.mAdapter.setNewData(this.albumList);
        }
    }

    public void initAdapter() {

        mAdapter = new AlbumAdatper(albumList);
        mAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        mEmptyHelper = new EmptyHelper(getContext(), mRecyclerView, mAdapter);
        mEmptyHelper.setEmptyView(EmptyHelper.LOADING, false, getString(R.string.loading_tip));
        View footerView = this.getListEndView();
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

                        DebugUtils.d("AblumSimilarFragment  --> onItemClick RUN! Position = " + position);
                        super.onItemClick(adapter, view, position);
                        AlbumInfo albumInfo = albumList.get(position);
                        //增加开关，避免连续点击，Activity跳转动画出现错误
                        if (albumClickable) {
                            DebugUtils.d("albumClickable = " + albumClickable);
                            DebugUtils.d("AlbumInfo = " + albumInfo.toString());
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

        Intent intent = new Intent(getActivity(), AblumDetailActivity.class);
        intent.putExtra("albumId", albumInfo.getAlbumid());
        intent.putExtra("albumInfo", albumInfo);

        ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
                ((BaseActivity) getActivity()),
                new Pair<View, String>(view.findViewById(R.id.img_story_cover), "albumImage")
        );
        Bundle bundle = activityOptions.toBundle();
        startActivity(intent, bundle);
    }

    /**
     * 设置专辑间距
     */
    public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

        int mSpace;

        public SpaceItemDecoration(int space) {
            this.mSpace = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int pos = parent.getChildAdapterPosition(view);

            //DebugUtils.d(">>>>>>>>>>>>>>>  pos = " + pos + "; lastPos = " + lastPos);
            outRect.right = mSpace;
            outRect.top = 0;
            outRect.bottom = 0;
            if (pos % 2 == 0) {
                outRect.left = mSpace;
            } else {
                outRect.left = 0;
            }
        }
    }
}

