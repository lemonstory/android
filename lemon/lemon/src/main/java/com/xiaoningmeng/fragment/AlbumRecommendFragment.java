package com.xiaoningmeng.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
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
import com.xiaoningmeng.http.JsonCallback;
import com.xiaoningmeng.http.LHttpRequest;
import com.xiaoningmeng.manager.EmptyHelper;
import com.xiaoningmeng.utils.DebugUtils;

import java.util.ArrayList;
import java.util.List;

public class AlbumRecommendFragment extends LazyFragment implements SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {

    private static final String ARG_RECOMMEND_URL = "recommendUrl";
    private static final String ARG_MIN_AGE = "minAge";
    private static final String ARG_MAX_AGE = "maxAge";

    private OnFragmentInteractionListener mListener;

    private boolean isPrepared;
    private AblumRecommendAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private EmptyHelper mEmptyHelper;
    private int mCurrentPage = 1;
    private int pageSize = 20;
    private boolean isErr;
    private View notLoadingView;
    private int delayMillis = 1000;
    private List<AlbumRecommend.AlbumItemBean> mAlbumItems;
    private String recommendUrl = "";
    private String minAge = String.valueOf(Constant.MIN_AGE);
    private String maxAge = String.valueOf(Constant.MAX_AGE);

    public static AlbumRecommendFragment newInstance(String recommendUrl, String minAge, String maxAge) {

        AlbumRecommendFragment fragment = new AlbumRecommendFragment();
        Bundle args = new Bundle();
        args.putString(ARG_RECOMMEND_URL, recommendUrl);
        args.putString(ARG_MIN_AGE, minAge);
        args.putString(ARG_MAX_AGE, maxAge);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

//        DebugUtils.d("AlbumRecommendFragment -- onCreate -- RUN!!!");
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recommendUrl = getArguments().getString(ARG_RECOMMEND_URL);
            minAge = getArguments().getString(ARG_MIN_AGE);
            maxAge = getArguments().getString(ARG_MAX_AGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

//        DebugUtils.d("AlbumRecommendFragment -- onCreateView -- RUN!!!");
//        DebugUtils.d("AlbumRecommendFragment -- onCreateView -- isPrepared:" + isPrepared + " -- isVisible:" + getUserVisibleHint());
        View contentView = View.inflate(getActivity(), R.layout.fragment_album_recommend, null);
        mRecyclerView = (RecyclerView) contentView.findViewById(R.id.id_stickynavlayout_innerscrollview);
        mRecyclerView.setHasFixedSize(true);
        this.spanCount = 2;
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
        mAdapter.openLoadMore(pageSize);
        isErr = false;
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(18));
        mRecyclerView.addOnItemTouchListener(
                new OnItemChildClickListener() {
                    @Override
                    public void SimpleOnItemChildClick(BaseQuickAdapter adapter, View view, int position) {

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
                        AlbumRecommend.AlbumItemBean item = (AlbumRecommend.AlbumItemBean) adapter.getData().get(position);
                        Intent i = new Intent(getActivity(), AlbumDetailActivity.class);
                        AlbumInfo albumInfo = new AlbumInfo(item.getId(), item.getTitle(), "", "0", item.getCover(), item.getListennum(), 0, 0, item.getRecommenddesc(), item.getAge_str(), "");
                        i.putExtra("albumInfo", (Parcelable) albumInfo);
                        startActivityForNew(i);
                    }
                }
        );
    }

    @Override
    public void onRefresh() {

//        DebugUtils.d("AlbumRecommendFragment -- onRefresh -- RUN!!!");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mCurrentPage = 1;
                requestAlbumRecommendData(recommendUrl, minAge, maxAge, mCurrentPage, pageSize);
                mAdapter.removeAllFooterView();
                mSwipeRefreshLayout.setRefreshing(false);
                isErr = false;
            }
        }, delayMillis);
    }

    private void requestAlbumRecommendData(String url, String minAge, String maxAge, int page, final int pageSize) {

//        DebugUtils.d("AlbumRecommendFragment -- requestAlbumRecommendData -- RUN!!!");
        LHttpRequest.getInstance().getAlbumRecommendReq(getActivity(), url, minAge, maxAge, page, pageSize, new JsonCallback<AlbumRecommend>() {

            @Override
            public void onGetDataSuccess(AlbumRecommend data) {

                isLoadData = true;
                mSwipeRefreshLayout.setRefreshing(false);

                mAlbumItems = data.getItems();
                mAdapter.addData(mAlbumItems);
         }

            @Override
            public void onFailure(String responseString) {

                isLoadData = true;
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }


    @Override
    public void onLoadMoreRequested() {

//        DebugUtils.d("AlbumRecommendFragment -- onLoadMoreRequested -- RUN!!!");
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {

                if (mAlbumItems.size() < pageSize) {
                    mAdapter.loadComplete();
                    if (notLoadingView == null) {
                        notLoadingView = getActivity().getLayoutInflater().inflate(R.layout.list_end_view, (ViewGroup) mRecyclerView.getParent(), false);
                    }
                    if (notLoadingView != null && notLoadingView.getParent() != null) {
                        { ((ViewGroup) notLoadingView.getParent()).removeView(notLoadingView); }
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
                        }, delayMillis);
                    } else {
                        isErr = true;
                        Toast.makeText(getActivity(), R.string.network_err, Toast.LENGTH_LONG).show();
                        mAdapter.showLoadMoreFailedView();
                    }
                }
            }
        });
    }

    @Override
    protected void lazyLoad() {

//        DebugUtils.d("AlbumRecommendFragment -- lazyLoad -- RUN!!!");
//        DebugUtils.d("AlbumRecommendFragment -- lazyLoad -- isPrepared:" + isPrepared + " -- isVisible:" + isVisible);
        if (!isPrepared || !isVisible) {
            return;
        }
        mSwipeRefreshLayout.setRefreshing(true);
        requestAlbumRecommendData(recommendUrl, minAge, maxAge, mCurrentPage, pageSize);

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
}
