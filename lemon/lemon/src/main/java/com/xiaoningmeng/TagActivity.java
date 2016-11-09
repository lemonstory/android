package com.xiaoningmeng;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.xiaoningmeng.adapter.PopAdapter;
import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.bean.PlayingStory;
import com.xiaoningmeng.bean.Special;
import com.xiaoningmeng.bean.Tag;
import com.xiaoningmeng.bean.TagDetail;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.fragment.TagFragment;
import com.xiaoningmeng.http.JsonCallback;
import com.xiaoningmeng.http.LHttpRequest;
import com.xiaoningmeng.manager.PlayWaveManager;
import com.xiaoningmeng.player.PlayObserver;
import com.xiaoningmeng.player.PlayerManager;
import com.xiaoningmeng.view.PagerSlidingTabStrip;
import com.xiaoningmeng.view.dialog.DrawableDialogLoading;

import java.util.ArrayList;
import java.util.List;


public class TagActivity extends BaseActivity implements View.OnClickListener,PlayObserver {

    private PagerSlidingTabStrip mIndicator;
    private ViewPager mViewPager;
    private TextView mClassifyTv;
    private ImageView mCoverImg;
    private ImageView mDropImg;
    private TagFragment[] mTagFragments;
    private String mSelectFristTag;
    private String mSelectSecondTag;
    private List<Tag> mFristTagList;
    private List<Tag> mSecondTagList;
    private List<Special> mSpecialList;
    public static final String Fragment_Tag ="Fragment_Tag";
    private List<TagParam> mTagParams;
    private TabFragmentPagerAdapter mPagerAdapter;
    private boolean isFirst = true;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        Fresco.initialize(this);
        setContentView(R.layout.activity_classification);
        Tag selectTag = getIntent().getParcelableExtra("classification");
        String classificationName = getIntent().getStringExtra("classification_name");
        mIndicator = (PagerSlidingTabStrip) findViewById(R.id.tab_indicator);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mClassifyTv = (TextView) findViewById(R.id.tv_head_classify);
        mDropImg = (ImageView)findViewById(R.id.img_head_drop);
        mCoverImg = (ImageView) findViewById(R.id.img_head_right);
        if(classificationName != null){
            mClassifyTv.setText(classificationName);
        }
        mTagParams = new ArrayList<>();
        mFristTagList = new ArrayList<>();
        mSecondTagList = new ArrayList<>();
        mSpecialList = new ArrayList<>();
        PlayerManager.getInstance().register(this);
        setLoading(new DrawableDialogLoading(this));
        String selectTagId = Constant.DEFAULT_TAG_ID;//避免空指针,给个默认值
        if (selectTag != null &&  selectTag.getId() != null && !selectTag.getId().equals("")) {
            selectTagId = selectTag.getId();
        }
        requestData(selectTagId);
    }

    private  void requestData(String selectTagId){
        LHttpRequest.getInstance().getTagAblumListReq(this,selectTagId, 1,
                Constant.FRIST, Constant.FRIST_ID, null, 0, new JsonCallback<TagDetail>(this) {
            @Override
            public void onGetDataSuccess(TagDetail data) {

                if(data != null){
                    mTagParams.clear();
                    mSelectFristTag = data.getSelectfirsttagid();
                    mSelectSecondTag = data.getSelectsecondtagid();
                    mSecondTagList = data.getSecondtaglist();
                    mFristTagList = data.getFirsttaglist();
                    mSpecialList = data.getSpecialtaglist();
                    if (mSpecialList != null) {
                        for (Special specail : mSpecialList) {
                            mTagParams.add(new TagParam(specail.getName(),mSelectFristTag, specail.getParamkey()));
                        }
                    }
                    int selectTabPos = 0;
                    if (mSecondTagList != null) {
                        for (Tag tag : mSecondTagList) {
                            mTagParams.add(new TagParam(tag.getName(),tag.getId(), null));
                            if (tag.getId() != null && tag.getId().equals(mSelectSecondTag)) {
                                selectTabPos = mTagParams.size()-1;
                            }

                        }
                    }
                    for (Tag tag : mFristTagList) {
                        if (tag.getId().equals(mSelectFristTag)) {
                            mClassifyTv.setText(tag.getName());
                        }
                    }
                    mTagFragments = new TagFragment[mTagParams.size()];
                    if(isFirst) {
                        mPagerAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager());
                        mViewPager.setAdapter(mPagerAdapter);
                        mViewPager.setOffscreenPageLimit(0);
                        mIndicator.setViewPager(mViewPager);
                    }else{
                        for(int i = 0; i < mPagerAdapter.getCount();i++){
                            TagFragment fragment = (TagFragment) mPagerAdapter.instantiateItem(mViewPager,i);
                            if(fragment != null && mTagParams.size() > i){
                                fragment.refreshData(mTagParams.get(i));
                            }
                        }
                        mPagerAdapter.notifyDataSetChanged();
                    }
                    mViewPager.setCurrentItem(selectTabPos);
                    isFirst = false;
                }
            }
        });
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_head_classify:
            case R.id.img_head_drop:
                showPop();
                break;
            case R.id.img_head_search:
                startActivityForNew(new Intent(this,SearchActivity.class));
                break;
        }
    }


    private ListView mPopListView;
    private ViewGroup mPopView;
    private PopupWindow mPopupWindow;
    private PopAdapter mPopAdapter;

    public void showPop() {

        if (mPopView == null) {
            mPopView = (ViewGroup) View.inflate(this, R.layout.pop_classification, null);
            mPopListView = (ListView) mPopView.getChildAt(0);
            mPopupWindow = new PopupWindow(mPopView,  WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
            mPopupWindow.setFocusable(true);
            mPopupWindow.setOutsideTouchable(true);
            mPopAdapter = new PopAdapter(this,mFristTagList,mSelectFristTag);
            mPopListView.setAdapter(mPopAdapter);
        }else{
            mPopAdapter.setSelectTag(mSelectFristTag);
            mPopAdapter.notifyDataSetChanged();
        }
        mPopListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (null != mFristTagList) {
                    Tag tag = mFristTagList.get(position);
                    mSelectFristTag = tag.getId();
                    mClassifyTv.setText(tag.getName());
                    requestData(tag.getId());
                }
                mPopupWindow.dismiss();
            }
        });
        mPopupWindow.setAnimationStyle(R.style.pop_top_anim);
        mPopupWindow.showAsDropDown(findViewById(R.id.tv_head_classify), 0, 0);
        ObjectAnimator animator = ObjectAnimator.ofFloat(mDropImg, "rotation", 0, 180).setDuration(250);
        animator.start();
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ObjectAnimator animator = ObjectAnimator.ofFloat(mDropImg, "rotation", 180, 0).setDuration(250);
                animator.start();
            }
        });
    }


        public  class TabFragmentPagerAdapter extends FragmentPagerAdapter {


            public TabFragmentPagerAdapter(FragmentManager fm) {
                super(fm);


        }

        @Override
        public Fragment getItem(int arg0) {

            if(mTagFragments[arg0] == null){
                mTagFragments[arg0] = new TagFragment();
            }
            Bundle bundle = new Bundle();
            bundle.putParcelable(Fragment_Tag,mTagParams.get(arg0));
            mTagFragments[arg0].setArguments(bundle);
            return mTagFragments[arg0];
        }

        @Override
        public int getCount() {

            return mTagFragments.length;
        }
        @Override
        public int getItemPosition(Object object) {

            return PagerAdapter.POSITION_NONE;
        }
            @Override
            public CharSequence getPageTitle(int position) {
                return mTagParams.get(position).name;
            }

    }


    @Override
    protected void onResume() {
        PlayWaveManager.getInstance().loadWaveAnim(this, mCoverImg);
        super.onResume();

    }

    @Override
    public void notify(PlayingStory music) {
        PlayWaveManager.getInstance().notify(music);

    }


    @Override
    public void onDestroy() {
        PlayerManager.getInstance().unRegister(this);
        super.onDestroy();
    }


    public static class TagParam implements Parcelable {
        public String name;
        public String tag;
        public String special;

        public TagParam(String name,String tag, String special) {
            this.name = name;
            this.tag = tag;
            this.special = special;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.name);
            dest.writeString(this.tag);
            dest.writeString(this.special);
        }

        protected TagParam(Parcel in) {
            this.name = in.readString();
            this.tag = in.readString();
            this.special = in.readString();
        }

        public static final Parcelable.Creator<TagParam> CREATOR = new Parcelable.Creator<TagParam>() {
            public TagParam createFromParcel(Parcel source) {
                return new TagParam(source);
            }

            public TagParam[] newArray(int size) {
                return new TagParam[size];
            }
        };
    }
}