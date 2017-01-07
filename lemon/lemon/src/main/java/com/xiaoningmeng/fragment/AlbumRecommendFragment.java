package com.xiaoningmeng.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
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
import com.xiaoningmeng.AlbumDetailActivity;
import com.xiaoningmeng.R;
import com.xiaoningmeng.adapter.AblumRecommendAdapter;
import com.xiaoningmeng.base.LazyFragment;
import com.xiaoningmeng.bean.AlbumInfo;
import com.xiaoningmeng.bean.AlbumRecommend;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.http.JsonResponse;
import com.xiaoningmeng.http.LHttpRequest;
import com.xiaoningmeng.manager.EmptyHelper;
import com.xiaoningmeng.utils.DebugUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.xiaoningmeng.http.LHttpRequest.mRetrofit;

public class AlbumRecommendFragment extends LazyFragment implements SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {

    private static final String ARG_RECOMMEND_URL = "recommendUrl";
    private static final String ARG_MIN_AGE = "minAge";
    private static final String ARG_MAX_AGE = "maxAge";

    private OnFragmentInteractionListener mListener;

    private boolean isPrepared;
    private Context mContext;
    private AblumRecommendAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private EmptyHelper mEmptyHelper;
    private int mCurrentPage = 1;
    private int pageSize = 20;
    private boolean isErr;
    private View notLoadingView;
    private List<AlbumInfo> mAlbumItems;
    private String recommendUrl = "";
    private int minAge = Constant.MIN_AGE;
    private int maxAge = Constant.MAX_AGE;

    public static AlbumRecommendFragment newInstance(String recommendUrl, int minAge, int maxAge) {

        AlbumRecommendFragment fragment = new AlbumRecommendFragment();
        Bundle args = new Bundle();
        args.putString(ARG_RECOMMEND_URL, recommendUrl);
        args.putString(ARG_MIN_AGE, String.valueOf(minAge));
        args.putString(ARG_MAX_AGE, String.valueOf(maxAge));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

//        DebugUtils.d("AlbumRecommendFragment -- onCreate -- RUN!!!");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recommendUrl = getArguments().getString(ARG_RECOMMEND_URL);
            minAge = Integer.parseInt(getArguments().getString(ARG_MIN_AGE));
            maxAge = Integer.parseInt(getArguments().getString(ARG_MAX_AGE));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

//        DebugUtils.d("AlbumRecommendFragment -- onCreateView -- RUN!!!");
//        DebugUtils.d("AlbumRecommendFragment -- onCreateView -- isPrepared:" + isPrepared + " -- isVisible:" + getUserVisibleHint());
        View contentView = View.inflate(getActivity(), R.layout.fragment_album_recommend, null);
        mContext = this.getActivity();
        mRecyclerView = (RecyclerView) contentView.findViewById(R.id.id_stickynavlayout_innerscrollview);
        mRecyclerView.setHasFixedSize(true);
        int spanCount = 2;
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), spanCount));
        mSwipeRefreshLayout = (SwipeRefreshLayout) contentView.findViewById(R.id.swipeLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        isPrepared = true;
        //TODO: 2(3)次往返左右滑动到该fragment时isVisible会为false. bug!!!
        isVisible = true;
        lazyLoad();
        initAdapter();
        return contentView;
    }

    @Override
    public void onAttach(Context context) {

//        DebugUtils.d("AlbumRecommendFragment -- onAttach -- RUN!!!");
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {

//        DebugUtils.d("AlbumRecommendFragment -- onDetach -- RUN!!!");
        super.onDetach();
        mListener = null;
    }

    private void initAdapter() {

        mAlbumItems = new ArrayList<>();
        mAdapter = new AblumRecommendAdapter(mAlbumItems);
        mRecyclerView.setAdapter(mAdapter);
        mEmptyHelper = new EmptyHelper(getActivity(), mRecyclerView, mAdapter);
        mEmptyHelper.setEmptyView(EmptyHelper.LOADING, true, getString(R.string.loading_tip));
        mAdapter.setOnLoadMoreListener(this);
        //mAdapter.openLoadMore(pageSize);
        isErr = false;
        AlbumRecommendFragment.ItemOffsetDecoration itemDecoration = new AlbumRecommendFragment.ItemOffsetDecoration(mContext, R.dimen.page_offset, R.dimen.item_offset);
        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.addOnItemTouchListener(
                new OnItemChildClickListener() {
                    @Override
                    public void onSimpleItemChildClick(BaseQuickAdapter adapter, View view, int position) {

//                        DebugUtils.d("AlbumRecommendFragment -- SimpleOnItemChildClick -- RUN!");
                    }

                    @Override
                    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

                        super.onItemChildClick(adapter, view, position);
                    }

                    @Override
                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                        super.onItemClick(adapter, view, position);
//                        DebugUtils.d("AlbumRecommendFragment -- onItemClick -- RUN!");
                        AlbumInfo item = (AlbumInfo) adapter.getData().get(position);
                        Intent i = new Intent(getActivity(), AlbumDetailActivity.class);
                        AlbumInfo albumInfo = new AlbumInfo(
                                item.getId(),
                                item.getTitle(),
                                item.getIntro(),
                                item.getStar_level(),
                                item.getCover(),
                                item.getListennum(),
                                item.getStory_num(),
                                item.getFavnum(),
                                item.getCommentnum(),
                                item.getRecommenddesc(),
                                item.getAge_str(),
                                item.getBuy_link());
                        i.putExtra("albumInfo", (Parcelable) albumInfo);
                        startActivityForNew(i);
                    }
                }
        );
    }

    @Override
    public void onRefresh() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mCurrentPage = 1;
                requestAlbumRecommendData(recommendUrl, minAge, maxAge, mCurrentPage, pageSize);
                mAdapter.removeAllFooterView();
                mSwipeRefreshLayout.setRefreshing(false);
                isErr = false;
            }
        }, Constant.DELAY_MILLIS);
    }

    private void requestAlbumRecommendData(String url, int minAge, int maxAge, final int page, final int pageSize) {

        if (url != null && !url.equals("")) {

            LHttpRequest.GetAlbumRecommendRequest getAlbumRecommendRequest = mRetrofit.create(LHttpRequest.GetAlbumRecommendRequest.class);
            Call<JsonResponse<AlbumRecommend>> call = getAlbumRecommendRequest.getResult(url, minAge, maxAge, mCurrentPage, pageSize);
            call.enqueue(new Callback<JsonResponse<AlbumRecommend>>() {

                @Override
                public void onResponse(Call<JsonResponse<AlbumRecommend>> call, Response<JsonResponse<AlbumRecommend>> response) {

                    if (response.isSuccessful() && response.body().isSuccessful()) {

                        AlbumRecommend data = response.body().getData();
                        mAlbumItems = data.getItems();
                        if (mSwipeRefreshLayout.isRefreshing()) {
                            mAdapter.setNewData(mAlbumItems);
                        } else {
                            mAdapter.addData(mAlbumItems);
                        }
                        isLoadData = true;
                        mSwipeRefreshLayout.setRefreshing(false);

                        if (1 == page && mAlbumItems.size() < pageSize) {
                            mAdapter.loadMoreComplete();
                            if (notLoadingView == null) {
                                notLoadingView = getActivity().getLayoutInflater().inflate(R.layout.list_footer_view, (ViewGroup) mRecyclerView.getParent(), false);
                            }
                            if (notLoadingView != null && notLoadingView.getParent() != null) {
                                {
                                    ((ViewGroup) notLoadingView.getParent()).removeView(notLoadingView);
                                }
                            }
                            mAdapter.addFooterView(notLoadingView);
                        }
                    } else {
                        DebugUtils.e(response.toString());
                    }
                }


                @Override
                public void onFailure(Call<JsonResponse<AlbumRecommend>> call, Throwable t) {

                    DebugUtils.e(t.toString());
                    isLoadData = true;
                    mSwipeRefreshLayout.setRefreshing(false);
                }

            });
        }
    }


    @Override
    public void onLoadMoreRequested() {

//        DebugUtils.d("AlbumRecommendFragment -- onLoadMoreRequested -- RUN!!!");
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {

                if (mAlbumItems.size() < pageSize) {
                    mAdapter.loadMoreComplete();
                    if (notLoadingView == null) {
                        notLoadingView = getActivity().getLayoutInflater().inflate(R.layout.list_footer_view, (ViewGroup) mRecyclerView.getParent(), false);
                    }
                    if (notLoadingView != null && notLoadingView.getParent() != null) {
                        {
                            ((ViewGroup) notLoadingView.getParent()).removeView(notLoadingView);
                        }
                    }
                    mAdapter.addFooterView(notLoadingView);
                } else {
                    if (!isErr) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mCurrentPage = mCurrentPage + 1;
                                requestAlbumRecommendData(recommendUrl, minAge, maxAge, mCurrentPage, pageSize);
                            }
                        }, Constant.DELAY_MILLIS);
                    } else {
                        isErr = true;
                        Toast.makeText(getActivity(), R.string.network_err, Toast.LENGTH_LONG).show();
                        mAdapter.loadMoreFail();
                    }
                }
            }
        });
    }

    @Override
    protected void lazyLoad() {

        DebugUtils.d("AlbumRecommendFragment -- lazyLoad -- RUN!!!");
//        DebugUtils.d("AlbumRecommendFragment -- lazyLoad -- isPrepared:" + isPrepared + " -- isVisible:" + isVisible);
        if (!isPrepared || !isVisible) {
            return;
        }
        mSwipeRefreshLayout.setRefreshing(true);
        onRefresh();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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