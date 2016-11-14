package com.xiaoningmeng;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.DraweeTransition;
import com.facebook.drawee.view.SimpleDraweeView;
import com.orhanobut.dialogplus.DialogPlus;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.xiaoningmeng.application.ActivityManager;
import com.xiaoningmeng.application.MyApplication;
import com.xiaoningmeng.auth.UserAuth;
import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.bean.Album;
import com.xiaoningmeng.bean.AlbumInfo;
import com.xiaoningmeng.bean.AudioDownLoad;
import com.xiaoningmeng.bean.CommentInfo;
import com.xiaoningmeng.bean.PlayingStory;
import com.xiaoningmeng.bean.ShareBean;
import com.xiaoningmeng.bean.Story;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.download.DownLoadClientImpl;
import com.xiaoningmeng.download.DownLoadObserver;
import com.xiaoningmeng.event.CommentEvent;
import com.xiaoningmeng.event.FavEvent;
import com.xiaoningmeng.fragment.AblumDetailIntroFragment;
import com.xiaoningmeng.fragment.AblumDetailPlayListFragment;
import com.xiaoningmeng.fragment.AblumSimilarFragment;
import com.xiaoningmeng.http.JsonCallback;
import com.xiaoningmeng.http.LHttpRequest;
import com.xiaoningmeng.manager.PlayWaveManager;
import com.xiaoningmeng.player.PlayObserver;
import com.xiaoningmeng.player.PlayerManager;
import com.xiaoningmeng.player.PlayerManager.AlbumSource;
import com.xiaoningmeng.utils.AppUtils;
import com.xiaoningmeng.utils.ImageUtils;
import com.xiaoningmeng.view.CircleProgressBar;
import com.xiaoningmeng.view.RatingBar;
import com.xiaoningmeng.view.ShareDialog;
import com.xiaoningmeng.view.StickyNavLayout;
import com.xiaoningmeng.view.dialog.TipDialog;

import java.util.List;

import de.greenrobot.event.EventBus;


public class AlbumDetailActivity extends BaseActivity implements
        OnClickListener, PlayObserver, DownLoadObserver<AudioDownLoad> {

    private Context mContext;
    private ViewPager mViewPager;
    private AblumDetailPlayListFragment mPlayListFragment;
    private AblumDetailIntroFragment mIntroFragment;
    private AblumSimilarFragment mSimilarFragment;
    private TextView mPlayListTabTv;
    private TextView mIntroTabTv;
    private TextView mFavTv;
    private TextView mListenerTv;
    private TextView mAgeLevelTv;
    private TextView mSimilarTabTv;
    private FrameLayout mBuyFl;
    private StickyNavLayout mStickyNavLayout;
    private CircleProgressBar mPlayProgressBar;
    private TextView mAlbumTitleTv;
    private ImageView mPlayBtnImg;
    private SimpleDraweeView mCoverImg;
    private Uri albumCoverUri;
    private ImageView mWaveImg;
    private TextView mCommentTv;

    private RatingBar mRatingBar;
    private AlbumInfo albumInfo;
    private List<Story> storyList;
    private List<CommentInfo> commentList;
    private boolean isFirst = true;
    private boolean isLoadImage;
    private int mPlayTime;
    private String mPlayStoryId;
    private int mPlayStoryPosition;
    private String mAlbumId;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            Story story = (Story) msg.obj;
            int pos = msg.arg1;
            if (DownLoadClientImpl.getInstance().getDownloadStatus(story.getMediapath()) == -1) {
                DownLoadClientImpl.getInstance().download(new AudioDownLoad(story, pos), albumInfo);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ablum_detail);
        mContext = this;
        mViewPager = (ViewPager) findViewById(R.id.id_stickynavlayout_viewpager);
        mPlayListTabTv = (TextView) findViewById(R.id.tv_ablum_detail_play_list);
        mIntroTabTv = (TextView) findViewById(R.id.tv_ablum_detail_info);
        mListenerTv = (TextView) findViewById(R.id.tv_ablum_listen_num);
        mAgeLevelTv = (TextView) findViewById(R.id.tv_ablum_age_level);
        mSimilarTabTv = (TextView) findViewById(R.id.tv_ablum_similar);
        mBuyFl = (FrameLayout) findViewById(R.id.fl_buy);
        mPlayBtnImg = (ImageView) findViewById(R.id.img_ablum_detail_btn);
        mWaveImg = (ImageView) findViewById(R.id.img_head_right);
        mCoverImg = (SimpleDraweeView) findViewById(R.id.img_ablum_detail_cover);
        mCommentTv = (TextView) findViewById(R.id.tv_comment);
        mStickyNavLayout = (StickyNavLayout) findViewById(R.id.StickyNavLayout);
        mAlbumTitleTv = (TextView) findViewById(R.id.tv_ablum_detail_title);
        mRatingBar = (RatingBar) findViewById(R.id.rb_ablum_detail_rate);
        mFavTv = (TextView) findViewById(R.id.tv_fav);
        mPlayListTabTv.setOnClickListener(this);
        mIntroTabTv.setOnClickListener(this);
        mSimilarTabTv.setOnClickListener(this);
        mPlayProgressBar = (CircleProgressBar) findViewById(R.id.circleProgressBar);
        PlayerManager.getInstance().register(this);
        setRightHeadIcon(R.drawable.play_flag_wave_01);

        mAlbumId = getAlbumIdWithIntent();
        boolean isScroll = getIntent().getBooleanExtra("isScroll", false);
        final int pager = getIntent().getIntExtra("pager", 1);
        mPlayTime = getIntent().getIntExtra("playtimes", 0);
        mPlayStoryId = getIntent().getStringExtra("playstoryid");
        AlbumInfo albumInfo = getIntent().getParcelableExtra("albumInfo");
        if (albumInfo != null) {
            fillAlbumInfoView(albumInfo);
        }

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPlayListFragment = new AblumDetailPlayListFragment();
                mIntroFragment = new AblumDetailIntroFragment();
                mSimilarFragment = new AblumSimilarFragment();
                mViewPager.setAdapter(new TabFragmentPagerAdapter(getSupportFragmentManager()));
                mViewPager.setOffscreenPageLimit(2);
                selectTab(pager);
                mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset,
                                               int positionOffsetPixels) {
                    }

                    @Override
                    public void onPageSelected(int position) {
                        selectTab(position);

                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
                mViewPager.setCurrentItem(pager);
                DownLoadClientImpl.getInstance().registerObserver(AlbumDetailActivity.this);
                if (!EventBus.getDefault().isRegistered(AlbumDetailActivity.this)) {
                    EventBus.getDefault().register(AlbumDetailActivity.this);
                }
                requestAlbumDetailData();
            }
        }, 300);

        if (isScroll) {
            mStickyNavLayout.postDelayed(new Runnable() {

                @Override
                public void run() {
                    mStickyNavLayout.scrollTo(0, 800);

                }
            }, 100);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);

        setIntent(intent);
        mAlbumId = getAlbumIdWithIntent();
        AlbumInfo albumInfo = getIntent().getParcelableExtra("albumInfo");
        if (albumInfo != null) {
            fillAlbumInfoView(albumInfo);
        }
        requestAlbumDetailData();
        mViewPager.setCurrentItem(1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAlbumId = getAlbumIdWithIntent();
        PlayWaveManager.getInstance().loadWaveAnim(this, mWaveImg);

    }

    @Override
    protected void onPause() {
        super.onPause();
        PlayWaveManager.getInstance().mContext = null;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        PlayerManager.getInstance().unRegister(this);
        DownLoadClientImpl.getInstance().unregisterObserver(this);
        EventBus.getDefault().unregister(this);
    }

    private void initShareSharedElementTransition() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setSharedElementEnterTransition(DraweeTransition.createTransitionSet(
                    ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.CENTER_CROP));
            getWindow().setSharedElementReturnTransition(DraweeTransition.createTransitionSet(
                    ScalingUtils.ScaleType.CENTER_CROP, ScalingUtils.ScaleType.CENTER_CROP));
        }
    }

    private String getAlbumIdWithIntent() {

        String albumIdWithIntent = "";
        String albumIdWithExtar = "";
        String albumIdWithData = "";

        Intent intent = this.getIntent();
        Uri data = intent.getData();
        albumIdWithExtar = intent.getStringExtra("albumId");
        if (null != data) {
            albumIdWithData = data.getQueryParameter("albumid");
        }

        if (albumIdWithExtar != null && !albumIdWithExtar.equals("")) {
            albumIdWithIntent = albumIdWithExtar;
        } else if (albumIdWithData != null && !albumIdWithData.equals("")) {
            albumIdWithIntent = albumIdWithData;
        }
        return albumIdWithIntent;
    }

    public void requestAlbumDetailData() {

        if (mAlbumId != null && !mAlbumId.equals("")) {

            LHttpRequest.getInstance().albumInfoReq(this, 10, mAlbumId,
                    MyApplication.getInstance().getUid(),
                    new JsonCallback<Album>() {

                        @Override
                        public void onGetDataSuccess(Album data) {
                            albumInfo = data.getAlbumInfo();
                            storyList = data.getStoryListItems();
                            albumInfo.setStorylist(storyList);
                            commentList = data.getCommentlist();
                            fillAlbumInfoView(albumInfo);
                            mPlayListFragment.setStoryList(albumInfo, storyList, mPlayStoryId, mPlayTime);
                            mIntroFragment.setIntro(albumInfo.getIntro(), data.getTagList());
                            mSimilarFragment.setAlbumList(data.getRecommendAlbumList());
                            recoveryPlayedPosition();
                            //开始播放
                            if (!PlayerManager.getInstance().isPlaying()) {
                                playOrPauseStory();
                            }
                            AlbumDetailActivity.this.notify(PlayerManager.getInstance().getPlayingStory());
                        }

                        @Override
                        public void onFailure(String failureResponse) {
                            mPlayListFragment.onFailure();
                        }
                    });
        }
    }

    //恢复到上次播放的位置
    private void recoveryPlayedPosition() {
        if (mPlayStoryId != null && mPlayTime > 0 && storyList != null) {
            mPlayProgressBar.postDelayed(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < storyList.size(); i++) {
                        if (mPlayStoryId.equals(storyList.get(i).getId())) {
                            Story s = storyList.get(i);
                            mPlayStoryPosition = i;
                            mPlayProgressBar.setMax(Integer.parseInt(s.getTimes()));
                            mPlayProgressBar.setProgress(mPlayTime);
                            setTitleName(s.getTitle());
                        }
                    }
                }
            }, 100);
        }
    }

    private void fillAlbumInfoView(AlbumInfo albumInfo) {

        mAlbumTitleTv.setText(albumInfo.getTitle());
        setTitleName(albumInfo.getTitle());
        Uri uri = Uri.parse(albumInfo.getCover());
        if (albumCoverUri == null || (albumCoverUri != null && albumCoverUri.compareTo(uri) != 0)) {
            albumCoverUri = uri;
            ImageUtils.displayImage(this, mCoverImg, albumCoverUri, 300, 300);
        }

        initShareSharedElementTransition();
        mRatingBar.setStar(albumInfo.getStar_level() != null ? Integer.parseInt(albumInfo.getStar_level()) : 0);
        mFavTv.setSelected(albumInfo.getFav() == 1);
        mFavTv.setText(albumInfo.getFavnum() == 0 ? "收藏" : (albumInfo.getFavnum() + ""));
        mCommentTv.setText(albumInfo.getCommentnum() == 0 ? "评论" : (albumInfo.getCommentnum() + ""));
        mListenerTv.setText(albumInfo.getListennum() + "");
        mAgeLevelTv.setText(albumInfo.getAge_str());
        String albumBuyLink = albumInfo.getBuy_link();
        if(albumBuyLink != null && albumBuyLink != "") {
            mBuyFl.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.tv_ablum_detail_info:
                mViewPager.setCurrentItem(0);
                break;

            case R.id.tv_ablum_detail_play_list:
                mViewPager.setCurrentItem(1);
                break;

            case R.id.tv_ablum_similar:
                mViewPager.setCurrentItem(2);
                break;

            case R.id.tv_comment:
                //view.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fav_anim_in));
                displayAlbumComment();
                break;

            case R.id.img_ablum_detail_btn:
                playOrPauseStory();
                break;

            case R.id.tv_batch_download:
                view.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fav_anim_in));
                batchDownloadClick();
                break;

            case R.id.tv_share:
                view.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fav_anim_in));
                if (albumInfo != null) {
                    Story story = albumInfo.getStorylist().get(0);
                    ShareBean shareBean = new ShareBean(albumInfo.getTitle(), albumInfo.getIntro(), albumInfo.getCover(), story.getMediapath(), Constant.SHARE_ALBUM_URL + albumInfo.getAlbumid());
                    mController = new ShareDialog().show(this, shareBean);
                }
                break;

            case R.id.tv_fav:
                view.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fav_anim_in));
                if (UserAuth.auditUser(this, null)) {
                    favAblum(view);
                }
                break;

            case R.id.fl_buy:
                String albumBuyLink = albumInfo.getBuy_link();
                if(albumBuyLink != null && albumBuyLink != "") {
                    if (albumBuyLink.startsWith("http:") || albumBuyLink.startsWith("https:")) {
                        if (albumBuyLink.contains("taobao")) {
                            AppUtils.showTaobaoPage(AlbumDetailActivity.this,albumBuyLink);
                        } else {
                            WebViewActivity.openWebView(this,albumBuyLink);
                        }
                    }
                }
                break;

            default:
                break;
        }
    }

    private void playOrPauseStory() {

        String albumId = PlayerManager.getInstance().getPlayingStory().albumid;
        if (albumInfo != null && albumInfo.getId().equals(albumId)) {
            if (PlayerManager.getInstance().isPlaying()) {
                PlayerManager.getInstance().pausePlay();
            } else {
                PlayerManager.getInstance().resumePlay();
            }
        } else if (mPlayStoryId != null) {
            PlayerManager.getInstance().playStory(albumInfo, storyList, mPlayStoryPosition, mPlayTime, AlbumSource.ALBUM_DETAIL);
        } else {
            PlayerManager.getInstance().playStory(albumInfo, storyList, 0, AlbumSource.ALBUM_DETAIL);
            mPlayStoryId = albumInfo.getStorylist().get(0).getId();
        }
    }

    private void displayAlbumComment() {

        Intent intent = new Intent(this, AlbumCommentActivity.class);
        intent.putExtra("albumId", albumInfo.getAlbumid());
        startActivity(intent);
    }

    private TipDialog tipDialog;

    private void showShareDialog(final String albumTitle) {

        String context = String.format("好喜欢\"%s\",分享给小伙伴~\\(≧▽≦)/~啦啦啦", albumTitle);
        tipDialog = new TipDialog.Builder(mContext)
                .setLayoutId(R.layout.dialog_prompt)
                .setHasBtn(true)
                .setTipText(context)
                .setEnterText("确定")
                .setOnClickListener(
                        new com.orhanobut.dialogplus.OnClickListener() {

                            @Override
                            public void onClick(DialogPlus dialog, View view) {
                                switch (view.getId()) {
                                    case R.id.tv_dialog_enter:
                                        String app_name = AlbumDetailActivity.this.getString(R.string.app_name);
                                        String app_desc = AlbumDetailActivity.this.getString(R.string.app_desc);
                                        new ShareDialog().show(AlbumDetailActivity.this, new ShareBean(app_name, app_desc, Constant.SHARE_OFFCAIL_ICON_URL, null, Constant.SHARE_OFFCAIL_URL));
                                        tipDialog.dimiss();
                                        break;
                                    case R.id.tv_dialog_cancel:
                                        tipDialog.dimiss();
                                        break;
                                }
                            }
                        }).create();
        tipDialog.show();
    }

    private void batchDownloadClick() {

        //推荐分享
        this.showShareDialog(albumInfo.getTitle());

        //批量下载
        if (storyList != null && storyList.size() > 0) {
            for (int i = 0; i < storyList.size(); i++) {
                Story story = storyList.get(i);
                Message msg = mHandler.obtainMessage();
                msg.obj = story;
                msg.arg1 = i;
                mHandler.sendMessageDelayed(msg, i * 10);
            }
        }
        MobclickAgent.onEvent(this, "event_batch_download");
    }

    private void favAblum(final View view) {
        if (albumInfo == null) {
            return;
        }
        if (albumInfo.getFav() == 0) {
            LHttpRequest.getInstance().addFavAlbumRequest(this,
                    mAlbumId,
                    new JsonCallback<String>(this) {

                        @Override
                        public void onGetDataSuccess(String data) {
                            view.setSelected(true);
                            Animation favInAnim = AnimationUtils.loadAnimation(AlbumDetailActivity.this, R.anim.fav_anim_in);
                            view.startAnimation(favInAnim);
                            albumInfo.setFav(1);
                            new TipDialog.Builder(AlbumDetailActivity.this)
                                    .setAutoDismiss(true).setTransparent(false)
                                    .setTipText("收藏成功！").create().show();
                            albumInfo.updateAll("albumid =?", albumInfo.getAlbumid());
                            EventBus.getDefault().post(new FavEvent(albumInfo, 1));

                        }
                    });
        } else {
            LHttpRequest.getInstance().delFavAlbumRequest(this, mAlbumId, new JsonCallback<String>(this) {

                @Override
                public void onGetDataSuccess(String data) {
                    view.setSelected(false);
                    Animation favOutAnim = AnimationUtils.loadAnimation(AlbumDetailActivity.this, R.anim.fav_anim_out);
                    view.startAnimation(favOutAnim);
                    albumInfo.setFav(0);
                    new TipDialog.Builder(AlbumDetailActivity.this)
                            .setAutoDismiss(true).setTransparent(false)
                            .setTipText("取消收藏成功！").create().show();
                    albumInfo.updateAll("albumid =?", albumInfo.getAlbumid());
                    EventBus.getDefault().post(new FavEvent(albumInfo, 0));
                }
            });
        }
        MobclickAgent.onEvent(this, "event_collect");
    }

    private void selectTab(int position) {

        mIntroTabTv.setSelected(position == 0);
        mPlayListTabTv.setSelected(position == 1);
        mSimilarTabTv.setSelected(position == 2);
    }

    public class TabFragmentPagerAdapter extends FragmentPagerAdapter {

        public TabFragmentPagerAdapter(FragmentManager fm) {
            super(fm);

        }

        @Override
        public Fragment getItem(int arg0) {
            switch (arg0) {
                case 0:
                    return mIntroFragment;
                case 1:
                    return mPlayListFragment;
                case 2:
                    return mSimilarFragment;
                default:
                    return mPlayListFragment;
            }
        }

        @Override
        public int getCount() {

            return 3;
        }

        @Override
        public int getItemPosition(Object object) {

            return PagerAdapter.POSITION_NONE;
        }

    }

    @Override
    public void notify(PlayingStory music) {

        PlayWaveManager.getInstance().notify(music);
        if (albumInfo == null || !albumInfo.getId().equals(music.albumid)) {
            return;
        }

        switch (music.playState) {
            case PLAY:
                if (isFirst) {
                    setTitleName(music.title);
                    mPlayBtnImg.setImageResource(R.drawable.album_pause);
                    notifyPlayList();
                }
                mPlayProgressBar.setVisibility(View.VISIBLE);
                mPlayProgressBar.setMax(music.times);
                mPlayProgressBar.setProgress(music.current);
                break;
            case START:
                setTitleName(music.title);
                mPlayBtnImg.setImageResource(R.drawable.album_pause);
                mPlayProgressBar.setVisibility(View.VISIBLE);
                mPlayProgressBar.setMax(music.times);
                notifyPlayList();
                mPlayProgressBar.setProgress(music.current);
                break;
            case PAUSE:
                setTitleName(music.title);
                mPlayProgressBar.setMax(music.times);
                mPlayProgressBar.setProgress(music.current);
                mPlayBtnImg.setImageResource(R.drawable.album_play);
                notifyPlayList();
                break;
            case RESUME:
                mPlayBtnImg.setImageResource(R.drawable.album_pause);
                notifyPlayList();
                break;
            case STOP:
                setTitleName(music.title);
                mPlayProgressBar.setMax(music.times == 0 ? 100 : music.times);
                mPlayProgressBar.setProgress(music.current);
                mPlayBtnImg.setImageResource(R.drawable.album_play);
                notifyPlayList();
                break;
            default:
                break;
        }
        if (isFirst) {
            isFirst = false;
        }
    }

    public void notifyPlayList() {

        if (storyList != null && storyList.size() > 0) {
            if (mPlayListFragment != null && mPlayListFragment.getAdapter() != null) {
                mPlayListFragment.getAdapter().notifyDataSetChanged();
            }
        }
    }

    @Override
    public void notifyData(AudioDownLoad t) {
        if (albumInfo == null || !albumInfo.getId().equals(t.getAlbumid())) {
            return;
        }
        //notifyPlayList();
        if (mPlayListFragment != null) {
            mPlayListFragment.notifyDownloadView(t);
        }
    }


    @Override
    public void notifyCancel(AudioDownLoad t) {
        notifyData(t);
    }

    private ShareAction mController;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 5 && data != null) {
            CommentInfo comment = data.getParcelableExtra("comment");
            int commentCount = albumInfo.getCommentnum() + 1;
            String commentCountStr = commentCount + "";
            albumInfo.setCommentnum(commentCount);
            //mCommentCountTv.setText(commentCountStr);
            EventBus.getDefault().post(new CommentEvent(albumInfo, commentCount));
        } else {
            UMShareAPI.get(AlbumDetailActivity.this).onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onEventMainThread(FavEvent favEvent) {
        if (albumInfo != null && favEvent.albumInfo.getAlbumid().equals(albumInfo.getAlbumid())) {
            if (favEvent.fav == 1) {
                mFavTv.setSelected(true);
                albumInfo.setFav(1);
                albumInfo.setFavnum(albumInfo.getFavnum() + 1);
            } else {
                mFavTv.setSelected(false);
                albumInfo.setFav(0);
                albumInfo.setFavnum(albumInfo.getFavnum() - 1);
            }
            mFavTv.setText(albumInfo.getFavnum() + "");
        }
    }

    @Override
    public void finish() {
        super.finish();
        if (ActivityManager.getScreenManager().getActivity(HomeActivity.class) == null) {
            startActivityForNew(new Intent(this, HomeActivity.class));
        }
    }
}
