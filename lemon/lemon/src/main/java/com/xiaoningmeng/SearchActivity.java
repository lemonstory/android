package com.xiaoningmeng;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.baoyz.swipemenu.xlistview.XListView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.xiaoningmeng.adapter.SearchAdapter;
import com.xiaoningmeng.adapter.SearchDefaultAdapter2;
import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.bean.AlbumInfo;
import com.xiaoningmeng.bean.SearchContent;
import com.xiaoningmeng.bean.SearchData;
import com.xiaoningmeng.db.SearchDao;
import com.xiaoningmeng.fragment.SearchAlbumChildFragment;
import com.xiaoningmeng.fragment.SearchStoryChildFragment;
import com.xiaoningmeng.http.LHttpRequest;
import com.xiaoningmeng.view.SearchView;
import com.xiaoningmeng.view.TabIndicatorView;

import com.xiaoningmeng.base.BaseFragment;
import com.xiaoningmeng.http.JsonCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


public class SearchActivity extends BaseActivity implements SearchView.OnSearchViewListener, View.OnClickListener {
    private XListView mListView;
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
    private FrameLayout mSearchFl;
    public static String[] TAB_TITLES = new String[]{"专辑", "声音"};
    public BaseFragment[] mSearchFragments = new BaseFragment[2];
    private int mPager = 1;
    private String mSearchContent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_search);
        mListView = (XListView) findViewById(R.id.id_stickynavlayout_innerscrollview);
        mSearchView = ((SearchView) findViewById(R.id.search_bar));
        mSearchFl = (FrameLayout) findViewById(R.id.fl_search_content);
        mListView.setPullLoadEnable(false);
        mListView.setPullRefreshEnable(false);
        mAlbumInfos = new ArrayList<>();
        mHotContents = new ArrayList<>();
        mLastContents = new ArrayList<>();
        mLastContents.addAll(SearchDao.getInstance().getSearchContentList(20));
        mDefaultAdapter = new SearchDefaultAdapter2(this, mHotContents, mLastContents);
        mSearchAdapter = new SearchAdapter(this, mAlbumInfos);
        mListView.setAdapter(mDefaultAdapter);
        requestDefaultSearchReq();
        mSearchView.loadView(true);
        mSearchView.setOnSearchViewListener(SearchActivity.this);
    }

    public void requestDefaultSearchReq() {

        LHttpRequest.getInstance().getHotSearchReq(this, 20, new JsonCallback<List<SearchContent>>() {

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
        setLoadingTip("搜索中...");
        mPager = 1;
        String searchType = "";
        LHttpRequest.getInstance().searchReq(this, searchContent, 10, mPager, searchType,
                new JsonCallback<SearchData>(this) {

                    @Override
                    public void onGetDataSuccess(SearchData data) {
                        mPager++;
                        mSearchContentView.setVisibility(View.VISIBLE);
                        if (data != null && (data.getAlbumcount() > 0 || data.getStorycount() > 0)) {
                            hideEmptyTip();
                            ((TextView) mIndicator.getChildAt(0)).setText(TAB_TITLES[0] + "（" + data.getAlbumcount() + "）");
                            ((TextView) mIndicator.getChildAt(1)).setText(TAB_TITLES[1] + "（" + data.getStorycount() + "）");
                            if (data.getAlbumcount() > 0 && null != data.getAlbumlist()) {
                                ((SearchAlbumChildFragment) mSearchFragments[0]).setAlbumList(data.getAlbumlist());
                            }
                            if (data.getStorycount() > 0 && null != data.getStorylist()) {
                                ((SearchStoryChildFragment) mSearchFragments[1]).setStoryList(data.getStorylist());
                            }
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
    }

    public void moreSearch(String searchType) {

        if (mSearchContent == null && isReq.get()) {
            return;
        }
        isReq.set(true);

        mAlbumInfos.clear();
        LHttpRequest.getInstance().searchReq(this, mSearchContent, 10, mPager, searchType,
                new JsonCallback<SearchData>() {

                    @Override
                    public void onGetDataSuccess(SearchData data) {
                        mPager++;
                        mSearchContentView.setVisibility(View.VISIBLE);
                        ((SearchAlbumChildFragment) mSearchFragments[0]).addAlbumList(data.getAlbumlist());
                        ((SearchStoryChildFragment) mSearchFragments[1]).addStoryList(data.getStorylist());
                    }

                    @Override
                    public void onFinish() {
                        isReq.set(false);
                        ((SearchAlbumChildFragment) mSearchFragments[0]).stopLoadMore();
                        ((SearchStoryChildFragment) mSearchFragments[1]).stopLoadMore();
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

        ViewGroup contentView = mSearchFl;
        if (mSearchContentView == null) {
            mSearchContentView = View.inflate(this, R.layout.fragment_empty_search2, null);
            mIndicator = (TabIndicatorView) mSearchContentView.findViewById(R.id.tab_indicator);
            mViewPager = (ViewPager) mSearchContentView.findViewById(R.id.viewpager);
            emptyView = (TextView) mSearchContentView.findViewById(R.id.tv_empty_tip);
            emptyView.setBackgroundColor(getResources().getColor(R.color.white));
            emptyView.setText("没有搜索匹配的内容");
            mIndicator.init(0, TAB_TITLES, mViewPager);
            mIndicator.getChildAt(0).setOnClickListener(this);
            mIndicator.getChildAt(1).setOnClickListener(this);
            mViewPager.setOffscreenPageLimit(1);
            mViewPager.setAdapter(new TabFragmentPagerAdapter(getSupportFragmentManager()));
            mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
                        mSearchFragments[position] = new SearchAlbumChildFragment();
                        break;
                    case 1:
                        mSearchFragments[position] = new SearchStoryChildFragment();
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (mSearchView.checkIsFocus()) { //检查mSearchView是否还有焦点
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void search1(String content) {
        mSearchView.search(content);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.search_translateyf100to0,
                R.anim.search_translatey0to100);
    }
}