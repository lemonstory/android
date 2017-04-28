package com.xiaoningmeng;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;

import com.astuetz.PagerSlidingTabStrip;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.bean.AlbumRecommend;
import com.xiaoningmeng.bean.PlayingStory;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.fragment.AlbumRecommendFragment;
import com.xiaoningmeng.http.JsonResponse;
import com.xiaoningmeng.http.LHttpRequest;
import com.xiaoningmeng.manager.PlayWaveManager;
import com.xiaoningmeng.player.PlayObserver;
import com.xiaoningmeng.player.PlayerManager;
import com.xiaoningmeng.utils.DebugUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.xiaoningmeng.http.LHttpRequest.mRetrofit;

/**
 * TODO:现在实现方案的不足：
 * 1:AlbumRecommendActivity 先做一次Api请求,得到age_level后初始化pageview及fragment
 * 然后fragemnt里面在做一次相同Api请求获取专辑数据。第二次根据age_level发起新的请求.
 */
public class AlbumRecommendActivity extends BaseActivity implements AlbumRecommendFragment.OnFragmentInteractionListener, PlayObserver {

    private PagerSlidingTabStrip mIndicator;
    private ViewPager mViewPager;
    private ImageView mWaveImg;
    private AlbumRecommendFragment[] mAlbumRecommendFragments;
    private AlbumRecommendActivity.AlbumRecommendFragmentPagerAdapter mPagerAdapter;
    private List<AlbumRecommend.AgeLevelBean.AgeLevelItemBean> mAgeLevelItems;

    private String pageTitle;
    private String recommendUrl;
    private int minAge = Constant.MIN_AGE;
    private int maxAge = Constant.MAX_AGE;
    private int pageSize = 20;
    private int mCurrentPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_recommend);
        Fresco.initialize(this);
        mIndicator = (PagerSlidingTabStrip) findViewById(R.id.tab_indicator);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mWaveImg = (ImageView) findViewById(R.id.img_head_right);
        PlayerManager.getInstance().register(this);
        setRightHeadIcon(R.drawable.ic_player_flag_wave_01);
        Intent intent = this.getIntent();
        parseIntent(intent);
        setPageTitle();
        requestAlbumRecommendData(recommendUrl, minAge, maxAge, mCurrentPage, pageSize);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PlayWaveManager.getInstance().loadWaveAnim(this, mWaveImg);
    }

    @Override
    protected void onPause() {
        super.onPause();
        PlayWaveManager.getInstance().mContext = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PlayerManager.getInstance().unRegister(this);
        PlayWaveManager.getInstance().mContext = null;
    }

    private void parseIntent(Intent intent) {

        pageTitle = intent.getStringExtra("pageTitle");
        String dataUrl = String.valueOf(intent.getData());
        Uri uri = Uri.parse(dataUrl);
        if (uri.getQueryParameter("min_age") != null && !uri.getQueryParameter("min_age").equals("")) {
            minAge = Integer.parseInt(uri.getQueryParameter("min_age"));
        }
        if (uri.getQueryParameter("max_age") != null && !uri.getQueryParameter("max_age").equals("")) {
            maxAge = Integer.parseInt(uri.getQueryParameter("max_age"));
        }
        String urlPath = dataUrl.replace("xnm://", "http://");
        recommendUrl = urlPath.substring(0, urlPath.indexOf("?"));
        DebugUtils.d("recommendUrl = " + recommendUrl);
    }

    private void setPageTitle() {
        if (pageTitle != null && !pageTitle.equals("")) {
            this.setTitleName(pageTitle);
        }
    }

    @Override
    public void notify(PlayingStory music) {
        PlayWaveManager.getInstance().notify(music);
    }

    private void requestAlbumRecommendData(final String url, int minAge, int maxAge, int page, int len) {

        if (url != null && !url.equals("")) {

            LHttpRequest.GetAlbumRecommendRequest getAlbumRecommendRequest = mRetrofit.create(LHttpRequest.GetAlbumRecommendRequest.class);
            Call<JsonResponse<AlbumRecommend>> call = getAlbumRecommendRequest.getResult(url, minAge, maxAge, mCurrentPage, pageSize);
            call.enqueue(new Callback<JsonResponse<AlbumRecommend>>() {

                @Override
                public void onResponse(Call<JsonResponse<AlbumRecommend>> call, Response<JsonResponse<AlbumRecommend>> response) {

                    if (response.isSuccessful() && response.body().isSuccessful()) {

                        AlbumRecommend data = response.body().getData();
                        mAgeLevelItems = data.getAge_level().getItems();
                        int albumRecommendFragmentCount = Integer.parseInt(data.getAge_level().getTotal());
                        mAlbumRecommendFragments = new AlbumRecommendFragment[albumRecommendFragmentCount];
                        int selectTabPos = 0;
                        for (int i = 0; i < albumRecommendFragmentCount; i++) {
                            if (data.getAge_level().getItems().get(i).getSelected() == 1) {
                                selectTabPos = i;
                            }

                            int minAge = Integer.parseInt(data.getAge_level().getItems().get(i).getMin_age());
                            int maxAge = Integer.parseInt(data.getAge_level().getItems().get(i).getMax_age());
                            mAlbumRecommendFragments[i] = AlbumRecommendFragment.newInstance(url, minAge, maxAge);
                        }
                        mPagerAdapter = new AlbumRecommendFragmentPagerAdapter(getSupportFragmentManager());
                        mViewPager.setAdapter(mPagerAdapter);
                        //mViewPager.setOffscreenPageLimit(0);
                        mIndicator.setViewPager(mViewPager);
                        mViewPager.setCurrentItem(selectTabPos);
                    }
                }

                @Override
                public void onFailure(Call<JsonResponse<AlbumRecommend>> call, Throwable t) {

                }

            });
        }
    }

    public class AlbumRecommendFragmentPagerAdapter extends FragmentPagerAdapter {

        public AlbumRecommendFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            if (mAlbumRecommendFragments[position] == null) {
                mAlbumRecommendFragments[position] = new AlbumRecommendFragment();
            }

            Fragment fragment = mAlbumRecommendFragments[position];
            return fragment;
        }

        @Override
        public int getCount() {

            int count = 0;
            count = mAlbumRecommendFragments.length;
            return count;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            String title = "";
            title = mAgeLevelItems.get(position).getAge_level_str();
            return title;
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

        DebugUtils.d("onFragmentInteraction RUN!!!!!!!!");
    }
}
