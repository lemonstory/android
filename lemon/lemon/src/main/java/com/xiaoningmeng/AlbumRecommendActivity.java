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
import com.xiaoningmeng.fragment.AlbumRecommendFragment;
import com.xiaoningmeng.fragment.AlbumRecommendFragment.OnFragmentInteractionListener;
import com.xiaoningmeng.http.JsonCallback;
import com.xiaoningmeng.http.LHttpRequest;
import com.xiaoningmeng.manager.PlayWaveManager;
import com.xiaoningmeng.player.PlayObserver;
import com.xiaoningmeng.player.PlayerManager;
import com.xiaoningmeng.utils.DebugUtils;

import java.util.List;

public class AlbumRecommendActivity extends BaseActivity implements OnFragmentInteractionListener, PlayObserver {

    private PagerSlidingTabStrip mIndicator;
    private ViewPager mViewPager;
    private ImageView mWaveImg;
    private AlbumRecommendFragment[] mAlbumRecommendFragments;
    public static final String Fragment_Tag = "Fragment_Tag";
    private List<TagActivity.TagParam> mTagParams;
    private AlbumRecommendActivity.AlbumRecommendFragmentPagerAdapter mPagerAdapter;
    private boolean isFirst = true;

    private List<AlbumRecommend.AgeLevelBean.AgeLevelItemBean> mAgeLevelItems;
    private List<AlbumRecommend.AlbumItemBean> mAlbumItems;
    private String pageTitle;
    private String recommendUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_recommend);
        Fresco.initialize(this);
        mIndicator = (PagerSlidingTabStrip) findViewById(R.id.tab_indicator);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mWaveImg = (ImageView) findViewById(R.id.img_head_right);
        PlayerManager.getInstance().register(this);
        setRightHeadIcon(R.drawable.play_flag_wave_01);
        Intent intent = this.getIntent();
        parseIntent(intent);
        setPageTitle();
        requestAlbumRecommendData(recommendUrl, "0", "2", "0", "20");
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
    private void parseIntent(Intent intent) {

        pageTitle = intent.getStringExtra("pageTitle");
        String dataUrl = String.valueOf(intent.getData());
        recommendUrl = dataUrl.replace("xnm://","http://");
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

    private void requestAlbumRecommendData(final String url, String minAge, String maxAge, String startAlbumId, String len) {


        if(url != null && !url.equals("")) {
            LHttpRequest.getInstance().getAlbumRecommendReq(this, url, minAge, maxAge, startAlbumId, len, new JsonCallback<AlbumRecommend>() {

                @Override
                public void onGetDataSuccess(AlbumRecommend data) {

                    mAlbumItems = data.getItems();
                    mAgeLevelItems = data.getAge_level().getItems();
                    int albumRecommendFragmentCount = Integer.parseInt(data.getAge_level().getTotal());
                    mAlbumRecommendFragments = new AlbumRecommendFragment[albumRecommendFragmentCount];
                    int selectTabPos = 0;
                    for (int i = 0; i < albumRecommendFragmentCount; i++) {
                        if (data.getAge_level().getItems().get(i).getSelected() == 1) {
                            selectTabPos = i;
                        }

                        String minAge = data.getAge_level().getItems().get(i).getMin_age();
                        String maxAge = data.getAge_level().getItems().get(i).getMax_age();
                        mAlbumRecommendFragments[i] = AlbumRecommendFragment.newInstance(url,minAge, maxAge);
                    }
                    mPagerAdapter = new AlbumRecommendFragmentPagerAdapter(getSupportFragmentManager());
                    mViewPager.setAdapter(mPagerAdapter);
                    mViewPager.setOffscreenPageLimit(0);
                    mIndicator.setViewPager(mViewPager);
                    mViewPager.setCurrentItem(selectTabPos);
                }

                @Override
                public void onFailure(int code, String msg) {
                    super.onFailure(code, msg);
                }

                @Override
                public void onFinish() {
                    super.onFinish();
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
//            Bundle bundle = new Bundle();
//            bundle.putParcelable(Fragment_Tag, mAlbumRecommendFragments.get(position));
//            mAlbumRecommendFragments[position].setArguments(bundle);
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

            DebugUtils.d("========== getPageTitle is RUN!!!======= Position = " + position);
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
