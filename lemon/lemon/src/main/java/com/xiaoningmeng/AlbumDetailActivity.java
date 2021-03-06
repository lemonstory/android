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
import android.webkit.URLUtil;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.DraweeTransition;
import com.facebook.drawee.view.SimpleDraweeView;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.logger.Logger;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.xiaoningmeng.application.ActivityManager;
import com.xiaoningmeng.auth.UserAuth;
import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.bean.Album;
import com.xiaoningmeng.bean.AlbumInfo;
import com.xiaoningmeng.bean.AudioDownLoad;
import com.xiaoningmeng.bean.Comment;
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
import com.xiaoningmeng.http.JsonResponse;
import com.xiaoningmeng.http.LHttpRequest;
import com.xiaoningmeng.manager.PlayWaveManager;
import com.xiaoningmeng.player.PlayObserver;
import com.xiaoningmeng.player.PlayerManager;
import com.xiaoningmeng.utils.AppUtils;
import com.xiaoningmeng.utils.ImageUtils;
import com.xiaoningmeng.view.CircleProgressBar;
import com.xiaoningmeng.view.RatingBar;
import com.xiaoningmeng.view.ShareDialog;
import com.xiaoningmeng.view.StickyNavLayout;
import com.xiaoningmeng.view.dialog.TipDialog;

import java.util.List;

import de.greenrobot.event.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.xiaoningmeng.http.LHttpRequest.mRetrofit;


public class AlbumDetailActivity extends BaseActivity implements
        OnClickListener, PlayObserver, DownLoadObserver<AudioDownLoad> {

    private static final String ARG_ALBUM_ID = "albumId";
    private static final String ARG_ALBUM_INFO = "albumInfo";

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
    private int storysPage = 1;
    private List<Story> storyList;
    private List<Comment> commentList;
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
        Fresco.initialize(this);
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
        setRightHeadIcon(R.drawable.ic_player_flag_wave_01);

        mAlbumId = getAlbumIdWithIntent();
        boolean isScroll = getIntent().getBooleanExtra("isScroll", false);
        final int pager = getIntent().getIntExtra("pager", 1);
        mPlayTime = getIntent().getIntExtra("playtimes", 0);
        mPlayStoryId = getIntent().getStringExtra("playstoryid");
        AlbumInfo albumInfo = getIntent().getParcelableExtra(this.ARG_ALBUM_INFO);
        if (albumInfo != null) {
            mAlbumId = albumInfo.getId();
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
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();
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
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        PlayerManager.getInstance().unRegister(this);
        DownLoadClientImpl.getInstance().unregisterObserver(this);
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);

        setIntent(intent);
        mAlbumId = getAlbumIdWithIntent();
        AlbumInfo albumInfo = getIntent().getParcelableExtra(this.ARG_ALBUM_INFO);
        if (albumInfo != null) {
            mAlbumId = albumInfo.getId();
            fillAlbumInfoView(albumInfo);
        }
        requestAlbumDetailData();
        mViewPager.setCurrentItem(1);
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
        if (intent.hasExtra(this.ARG_ALBUM_ID)) {
            albumIdWithExtar = intent.getStringExtra(this.ARG_ALBUM_ID);
        }
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

            LHttpRequest.AlbumInfoRequest albumInfoRequest = mRetrofit.create(LHttpRequest.AlbumInfoRequest.class);
            Call<JsonResponse<Album>> call = albumInfoRequest.getResult(mAlbumId, storysPage);
            call.enqueue(new Callback<JsonResponse<Album>>() {

                @Override
                public void onResponse(Call<JsonResponse<Album>> call, Response<JsonResponse<Album>> response) {

                    if (response.isSuccessful() && response.body().isSuccessful()) {

                        Album data = response.body().getData();
                        albumInfo = data.getAlbumInfo();
                        storyList = data.getStoryListItems();
                        albumInfo.setStorylist(storyList);
                        commentList = data.getCommentlist();
                        fillAlbumInfoView(albumInfo);
                        mPlayListFragment.setStoryList(albumInfo, storyList, storysPage, mPlayStoryId, mPlayTime);
                        mIntroFragment.setIntro(albumInfo.getIntro(), data.getTagList());
                        mSimilarFragment.setAlbumList(data.getRecommendAlbumList());
                        recoveryPlayedPosition();
                        //开始播放
                        if (!PlayerManager.getInstance().isPlaying()) {
                            playOrPauseStory();
                        }
                        AlbumDetailActivity.this.notify(PlayerManager.getInstance().getPlayingStory());

                    } else {
                        Logger.e(response.toString());
                    }
                }

                @Override
                public void onFailure(Call<JsonResponse<Album>> call, Throwable t) {

                    Logger.e(t.toString());
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
        if (URLUtil.isValidUrl(albumInfo.getCover())) {
            Uri uri = Uri.parse(albumInfo.getCover());
            if (albumCoverUri == null || (albumCoverUri != null && albumCoverUri.compareTo(uri) != 0)) {
                albumCoverUri = uri;
                ImageUtils.displayImage(this, mCoverImg, albumCoverUri, 300, 300);
            }
        }

        initShareSharedElementTransition();
        mRatingBar.setStar(albumInfo.getStar_level() != null ? Integer.parseInt(albumInfo.getStar_level()) : 0);
        mFavTv.setSelected(albumInfo.getFav() == 1);

        //TODO:如果数字长度超过5位则需要格式化
        String favNumStr = "收藏";
        if (albumInfo.getFavnum() > 0 && Integer.toString(albumInfo.getFavnum()).length() < 5) {
            favNumStr = Integer.toString(albumInfo.getFavnum());
        }
        mFavTv.setText(favNumStr);

        String commentNumStr = "评论";
        if (albumInfo.getCommentnum() > 0 && Integer.toString(albumInfo.getCommentnum()).length() < 5) {
            commentNumStr = Integer.toString(albumInfo.getCommentnum());
        }
        mCommentTv.setText(commentNumStr);
        mListenerTv.setText(albumInfo.getListennum() != null ? String.valueOf(albumInfo.getListennum()) : "");
        mAgeLevelTv.setText(albumInfo.getAge_str());
        String albumBuyLink = albumInfo.getBuy_link();
        if (albumBuyLink != null && !albumBuyLink.equals("")) {
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
                    ShareBean shareBean = new ShareBean(albumInfo.getTitle(), albumInfo.getIntro(), albumInfo.getCover(), story.getMediapath(), Constant.SHARE_ALBUM_URL + albumInfo.getId());
                    mController = new ShareDialog().show(this, shareBean);
                }
                break;

            case R.id.tv_fav:
                view.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fav_anim_in));
                if (UserAuth.getInstance().isLogin(this)) {
                    favAblum(view);
                }
                break;

            case R.id.fl_buy:
                String albumBuyLink = albumInfo.getBuy_link();
                if (albumBuyLink != null && !albumBuyLink.equals("")) {
                    if (albumBuyLink.startsWith("http:") || albumBuyLink.startsWith("https:")) {
                        if (albumBuyLink.contains("taobao")) {
                            AppUtils.showTaobaoPage(AlbumDetailActivity.this, albumBuyLink);
                        } else {
                            WebViewActivity.openWebView(this, albumBuyLink);
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
        if (albumInfo != null) {

            if (albumInfo.getId().equals(albumId)) {
                if (PlayerManager.getInstance().isPlaying()) {
                    PlayerManager.getInstance().pausePlay();
                } else {
                    PlayerManager.getInstance().resumePlay();
                }
            } else if (albumInfo.getStorylist() != null && albumInfo.getStorylist().size() > 0) {
                PlayerManager.getInstance().playStory(albumInfo, storyList, 0, PlayerManager.AlbumSource.ALBUM_DETAIL);
                mPlayStoryId = albumInfo.getStorylist().get(0).getId();
            }
        } else if (mPlayStoryId != null) {
            PlayerManager.getInstance().playStory(albumInfo, storyList, mPlayStoryPosition, mPlayTime, PlayerManager.AlbumSource.ALBUM_DETAIL);
        }
    }

    private void displayAlbumComment() {

        Intent intent = new Intent(this, AlbumCommentActivity.class);
        if (albumInfo != null) {
            String albumId = albumInfo.getId();
            if (albumId != null && !albumId.equals("")) {
                intent.putExtra("albumId", albumId);
                startActivity(intent);
            }
        }
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

        if (albumInfo != null) {
            //推荐分享
            //三星市场不容许下载前出现提示框.
            //this.showShareDialog(albumInfo.getTitle());
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
        }
        MobclickAgent.onEvent(this, "event_batch_download");
    }

    private void favAblum(final View view) {


        if (albumInfo == null) {
            return;
        }
        if (albumInfo.getFav() == 0) {

            LHttpRequest.AddFavAlbumRequest addFavAlbumRequest = mRetrofit.create(LHttpRequest.AddFavAlbumRequest.class);
            Call<JsonResponse<String>> call = addFavAlbumRequest.getResult(mAlbumId);
            call.enqueue(new Callback<JsonResponse<String>>() {

                @Override
                public void onResponse(Call<JsonResponse<String>> call, Response<JsonResponse<String>> response) {

                    if (response.isSuccessful() && response.body().isSuccessful()) {
                        view.setSelected(true);
                        Animation favInAnim = AnimationUtils.loadAnimation(AlbumDetailActivity.this, R.anim.fav_anim_in);
                        view.startAnimation(favInAnim);
                        albumInfo.setFav(1);
                        new TipDialog.Builder(AlbumDetailActivity.this)
                                .setAutoDismiss(true).setTransparent(false)
                                .setTipText("收藏成功！").create().show();
                        albumInfo.updateAll("albumid =?", albumInfo.getId());
                        EventBus.getDefault().post(new FavEvent(albumInfo, 1));
                    } else {
                        Logger.e(response.toString());
                    }

                }

                @Override
                public void onFailure(Call<JsonResponse<String>> call, Throwable t) {
                    Logger.e(t.toString());
                }
            });

        } else {
            LHttpRequest.DelFavAlbumRequest delFavAlbumRequest = mRetrofit.create(LHttpRequest.DelFavAlbumRequest.class);
            Call<JsonResponse<String>> call = delFavAlbumRequest.getResult(mAlbumId);
            call.enqueue(new Callback<JsonResponse<String>>() {

                @Override
                public void onResponse(Call<JsonResponse<String>> call, Response<JsonResponse<String>> response) {

                    if (response.isSuccessful() && response.body().isSuccessful()) {

                        view.setSelected(false);
                        Animation favOutAnim = AnimationUtils.loadAnimation(AlbumDetailActivity.this, R.anim.fav_anim_out);
                        view.startAnimation(favOutAnim);
                        albumInfo.setFav(0);
                        new TipDialog.Builder(AlbumDetailActivity.this)
                                .setAutoDismiss(true).setTransparent(false)
                                .setTipText("取消收藏成功！").create().show();
                        albumInfo.updateAll("albumid =?", albumInfo.getId());
                        EventBus.getDefault().post(new FavEvent(albumInfo, 0));

                    } else {
                        Logger.e(response.toString());
                    }

                }

                @Override
                public void onFailure(Call<JsonResponse<String>> call, Throwable t) {
                    Logger.e(t.toString());
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
//        if (albumInfo == null || !albumInfo.getId().equals(music.albumid)) {
//            return;
//        }

        if (mAlbumId.equals(music.albumid)) {
            switch (music.playState) {
                case PLAY:
                    if (isFirst) {
                        setTitleName(music.title);
                        mPlayBtnImg.setImageResource(R.drawable.btn_album_pause);
                        notifyPlayList();
                    }
                    mPlayProgressBar.setVisibility(View.VISIBLE);
                    mPlayProgressBar.setMax(music.times);
                    mPlayProgressBar.setProgress(music.current);
                    break;
                case START:
                    setTitleName(music.title);
                    mPlayBtnImg.setImageResource(R.drawable.btn_album_pause);
                    mPlayProgressBar.setVisibility(View.VISIBLE);
                    mPlayProgressBar.setMax(music.times);
                    notifyPlayList();
                    mPlayProgressBar.setProgress(music.current);
                    break;
                case PAUSE:
                    setTitleName(music.title);
                    mPlayProgressBar.setMax(music.times);
                    mPlayProgressBar.setProgress(music.current);
                    mPlayBtnImg.setImageResource(R.drawable.btn_album_play);
                    notifyPlayList();
                    break;
                case RESUME:
                    mPlayBtnImg.setImageResource(R.drawable.btn_album_pause);
                    notifyPlayList();
                    break;
                case STOP:
                    setTitleName(music.title);
                    mPlayProgressBar.setMax(music.times == 0 ? 100 : music.times);
                    mPlayProgressBar.setProgress(music.current);
                    mPlayBtnImg.setImageResource(R.drawable.btn_album_play);
                    notifyPlayList();
                    break;
                default:
                    break;
            }
            if (isFirst) {
                isFirst = false;
            }
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
            Comment comment = data.getParcelableExtra("comment");
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
        if (albumInfo != null && favEvent.albumInfo.getId().equals(albumInfo.getId())) {
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
