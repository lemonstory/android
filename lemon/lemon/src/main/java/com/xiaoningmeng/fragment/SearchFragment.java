package com.xiaoningmeng.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baoyz.swipemenu.xlistview.XListView;
import com.umeng.analytics.MobclickAgent;
import com.xiaoningmeng.R;
import com.xiaoningmeng.adapter.SearchAdapter;
import com.xiaoningmeng.adapter.SearchDefaultAdapter2;
import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.base.BaseFragment;
import com.xiaoningmeng.bean.AlbumInfo;
import com.xiaoningmeng.bean.SearchContent;
import com.xiaoningmeng.bean.SearchData;
import com.xiaoningmeng.db.SearchDao;
import com.xiaoningmeng.http.JsonCallback;
import com.xiaoningmeng.http.LHttpRequest;
import com.xiaoningmeng.view.SearchView;
import com.xiaoningmeng.view.SearchView.OnSearchViewListener;
import com.xiaoningmeng.view.TabIndicatorView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class SearchFragment extends BaseFragment implements OnSearchViewListener, OnClickListener {

    private XListView mListView;
    private BaseActivity mContext;
    private SearchAdapter mSearchAdapter;
    private SearchDefaultAdapter2 mDefaultAdapter;
    private List<AlbumInfo> mAlbumInfos;
    private TabIndicatorView mIndicator;
    private ViewPager mViewPager;
    private List<SearchContent> mHotContents;
    private List<SearchContent> mLastContents;
    private AtomicBoolean isReq = new AtomicBoolean();
    private SearchView mSearchView;
    private View mSearchContentView;
    public static String[] TAB_TITLES = new String[]{"声音", "专辑"};
    public BaseFragment[] mSearchFragments = new BaseFragment[2];
    private int mPager = 1;
    private String mSearchContent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = (BaseActivity) getActivity();
        View contentView = View.inflate(mContext, R.layout.fragment_search, null);
        mListView = (XListView) contentView.findViewById(R.id.id_stickynavlayout_innerscrollview);
        mSearchView = ((SearchView) getActivity().findViewById(R.id.search_bar));
        mListView.setPullLoadEnable(false);
        mListView.setPullRefreshEnable(false);
        mAlbumInfos = new ArrayList<>();

        mHotContents = new ArrayList<>();
        mLastContents = new ArrayList<>();
        mLastContents.addAll(SearchDao.getInstance().getSearchContentList(20));
        mDefaultAdapter = new SearchDefaultAdapter2(getActivity(), mHotContents, mLastContents);
        mSearchAdapter = new SearchAdapter(getActivity(), mAlbumInfos);
        mListView.setAdapter(mDefaultAdapter);
        requestDefaultSearchReq();
        mSearchView.loadView(false);
        mSearchView.setOnSearchViewListener(this);
        return contentView;
    }

    public void requestDefaultSearchReq() {

        LHttpRequest.getInstance().getHotSearchReq(mContext, 20, new JsonCallback<List<SearchContent>>(mContext) {

            @Override
            public void onGetDataSuccess(List<SearchContent> data) {
                if (data != null && data.size() > 0) {
                    mHotContents.addAll(data);
                    mDefaultAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    public void search(final String searchContent) {
        if (isReq.get()) {
            return;
        }
        isReq.set(true);
        mSearchContent = searchContent;
        mAlbumInfos.clear();
        mContext.setLoadingTip("搜索中...");
        mPager = 1;
        LHttpRequest.getInstance().searchReq(getActivity(), searchContent, 10, mPager, null,
                new JsonCallback<SearchData>(mContext) {

                    @Override
                    public void onGetDataSuccess(SearchData data) {
                        mPager++;
                        mSearchContentView.setVisibility(View.VISIBLE);
                        if (data != null && (data.getAlbumcount() != 0 || data.getStorycount() != 0)) {
                            hideEmptyTip();
                            ((TextView) mIndicator.getChildAt(1)).setText(TAB_TITLES[1] + "（" + data.getAlbumcount() + "）");
                            ((TextView) mIndicator.getChildAt(0)).setText(TAB_TITLES[0] + "（" + data.getStorycount() + "）");
                            ((SearchStoryChildFragment) mSearchFragments[0]).setStoryList(data.getStorylist());
                            ((SearchAlbumChildFragment) mSearchFragments[1]).setAlbumList(data.getAlbumlist());
                        } else {
                            showEmptyTip();
                        }
                    }

                    @Override
                    public void onFailure(String responseString) {

                        mSearchContentView.setVisibility(View.VISIBLE);
                        showEmptyTip();

                    }

                    @Override
                    public void onFinish() {
                        isReq.set(false);
                        if (SearchDao.getInstance().addSearch(searchContent)) {
                            mLastContents.add(0, new SearchContent(searchContent));
                            mDefaultAdapter.notifyDataSetChanged();
                        }
                        super.onFinish();
                    }
                });
        MobclickAgent.onEvent(mContext, "event_search");
    }

    public void moreSearch(String searchType) {
        if (mSearchContent == null && isReq.get()) {
            return;
        }
        isReq.set(true);

        mAlbumInfos.clear();
        LHttpRequest.getInstance().searchReq(getActivity(), mSearchContent, 10, mPager, searchType,
                new JsonCallback<SearchData>() {

                    @Override
                    public void onGetDataSuccess(SearchData data) {
                        mPager++;
                        mSearchContentView.setVisibility(View.VISIBLE);
                        ((SearchStoryChildFragment) mSearchFragments[0]).addStoryList(data.getStorylist());
                        ((SearchAlbumChildFragment) mSearchFragments[1]).addAlbumList(data.getAlbumlist());

                    }

                    @Override
                    public void onFinish() {
                        isReq.set(false);
                        ((SearchStoryChildFragment) mSearchFragments[0]).stopLoadMore();
                        ((SearchAlbumChildFragment) mSearchFragments[1]).stopLoadMore();
                        super.onFinish();

                    }
                });
    }

    @Override
    public void showDefault() {
        if (mSearchContentView != null) {
            mSearchContentView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void showEmpty(boolean isShowListView) {
        if (getView() != null) {
            ViewGroup contentView = (ViewGroup) getView();
            if (mSearchContentView == null) {
                mSearchContentView = View.inflate(mContext, R.layout.fragment_empty_search2, null);
                mIndicator = (TabIndicatorView) mSearchContentView.findViewById(R.id.tab_indicator);
                mViewPager = (ViewPager) mSearchContentView.findViewById(R.id.viewpager);
                emptyView = (TextView) mSearchContentView.findViewById(R.id.tv_empty_tip);
                emptyView.setBackgroundColor(getResources().getColor(R.color.white));
                emptyView.setText("没有搜索匹配的内容");
                mIndicator.init(0, TAB_TITLES, mViewPager);
                mIndicator.getChildAt(0).setOnClickListener(this);
                mIndicator.getChildAt(1).setOnClickListener(this);
                mViewPager.setOffscreenPageLimit(1);
                mViewPager.setAdapter(new TabFragmentPagerAdapter(getChildFragmentManager()));
                mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset,
                                               int positionOffsetPixels) {

                        mIndicator.onScrolled((mViewPager.getWidth() + mViewPager.getPageMargin()) * position + positionOffsetPixels);
                    }

                    @Override
                    public void onPageSelected(int position) {
                        mIndicator.onSwitched(position);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });

                contentView.addView(mSearchContentView);

            }
            emptyView.setVisibility(View.GONE);
            mIndicator.setVisibility(View.INVISIBLE);
            mViewPager.setVisibility(View.INVISIBLE);
            mSearchContentView.setVisibility(View.VISIBLE);
            mAlbumInfos.clear();
            mSearchAdapter.notifyDataSetChanged();

        }
    }

    TextView emptyView;

    public void showEmptyTip() {
        emptyView.setVisibility(View.VISIBLE);
        mIndicator.setVisibility(View.GONE);
        mViewPager.setVisibility(View.GONE);

    }

    public void hideEmptyTip() {
        emptyView.setVisibility(View.GONE);
        mIndicator.setVisibility(View.VISIBLE);
        mViewPager.setVisibility(View.VISIBLE);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case TabIndicatorView.BSSEEID + 0:
                mIndicator.setCurrentTab(0);
                break;
            case TabIndicatorView.BSSEEID + 1:
                mIndicator.setCurrentTab(1);
                break;
        }

    }

    public class TabFragmentPagerAdapter extends FragmentPagerAdapter {


        public TabFragmentPagerAdapter(FragmentManager fm) {
            super(fm);

        }

        @Override
        public Fragment getItem(int position) {

            if (mSearchFragments[position] == null) {
                switch (position) {
                    case 0:
                        mSearchFragments[position] = new SearchStoryChildFragment();
                        break;
                    case 1:
                        mSearchFragments[position] = new SearchAlbumChildFragment();
                        break;
                }

            }
            return mSearchFragments[position];
        }

        @Override
        public int getCount() {

            return mSearchFragments.length;
        }

        @Override
        public int getItemPosition(Object object) {

            return PagerAdapter.POSITION_NONE;
        }
    }

}
