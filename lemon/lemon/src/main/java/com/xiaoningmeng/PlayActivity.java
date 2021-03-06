package com.xiaoningmeng;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.orhanobut.logger.Logger;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.Config;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.xiaoningmeng.auth.UserAuth;
import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.bean.Album;
import com.xiaoningmeng.bean.AlbumInfo;
import com.xiaoningmeng.bean.AudioDownLoad;
import com.xiaoningmeng.bean.PlayingStory;
import com.xiaoningmeng.bean.ShareBean;
import com.xiaoningmeng.bean.Story;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.download.DownLoadClientImpl;
import com.xiaoningmeng.event.CommentEvent;
import com.xiaoningmeng.event.FavEvent;
import com.xiaoningmeng.http.JsonResponse;
import com.xiaoningmeng.http.LHttpRequest;
import com.xiaoningmeng.player.PlayObserver;
import com.xiaoningmeng.player.PlayerManager;
import com.xiaoningmeng.utils.TimeUtils;
import com.xiaoningmeng.view.ShareDialog;
import com.xiaoningmeng.view.dialog.TipDialog;
import com.xiaoningmeng.view.dialog.TopDialog;

import de.greenrobot.event.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.xiaoningmeng.http.LHttpRequest.mRetrofit;

public class PlayActivity extends BaseActivity implements OnClickListener,
        PlayObserver, OnSeekBarChangeListener, PlayerManager.OnPlayingDownloadListener {

    private SeekBar mSeekBar;
    private TextView mFinishTimeTv;
    private TextView mLeaveTimeTv;
    private ImageView mMusicModeImg;
    private PlayerManager mPlayerManager;
    private SimpleDraweeView mPlayCover;
    private ImageView mDownloadImg;
    private ImageView mFavImg;
    private ImageView mPauseImg;
    private TextView mCommentTv;
    private ImageView mCommentImg;
    private boolean isFirst = true;
    private boolean isTrackingTouch = false;
    private Animation coverRotateAnim;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_player);
        mPlayerManager = PlayerManager.getInstance();
        initView();
        setPlayMode();
        notify(mPlayerManager.getPlayingStory());
        mPlayerManager.register(this);
        mPlayerManager.setOnPlayingDownloadListener(this);
        EventBus.getDefault().register(this);

    }

    private void initView() {

        mFinishTimeTv = (TextView) this.findViewById(R.id.tv_finish_time);
        mLeaveTimeTv = (TextView) this.findViewById(R.id.tv_leave_time);
        mSeekBar = (SeekBar) this.findViewById(R.id.seekbar_play_album);
        mMusicModeImg = (ImageView) this.findViewById(R.id.img_album_mode);
        mPlayCover = (SimpleDraweeView) this.findViewById(R.id.img_play_cover);
        mDownloadImg = (ImageView) this.findViewById(R.id.img_album_download);
        mPauseImg = (ImageView) this.findViewById(R.id.img_album_pause);
        mCommentTv = (TextView) this.findViewById(R.id.tv_album_comment);
        mCommentImg = (ImageView) this.findViewById(R.id.img_album_comment);
        mFavImg = (ImageView) findViewById(R.id.img_album_fav);
        mMusicModeImg.setOnClickListener(this);
        mSeekBar.setOnSeekBarChangeListener(this);
        mSeekBar.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mPlayerManager.pausePlay();

            }
        });
        coverRotateAnim = AnimationUtils.loadAnimation(this, R.anim.uniform_rotate);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.img_album_next:
                mPlayerManager.nextPlay();
                break;

            case R.id.img_album_per:
                mPlayerManager.prevPlay();
                break;

            case R.id.img_album_mode:
                Animation modeAnim = AnimationUtils.loadAnimation(PlayActivity.this, R.anim.fav_anim_in);
                v.startAnimation(modeAnim);
                changePlayMode();
                break;

            case R.id.img_player_reminder:
                startActivityForNew(new Intent(this, ReminderActivity.class));
                break;

            case R.id.img_album_pause:
                if (mPlayerManager.isPlaying()) {
                    mPlayerManager.pausePlay();
                    ((ImageView) v).setImageResource(R.drawable.btn_player_play_normal);
                    if (coverRotateAnim != null) {
                        mPlayCover.clearAnimation();
                    }
                } else {
                    ((ImageView) v).setImageResource(R.drawable.btn_player_pause_normal);
                    if (coverRotateAnim != null) {
                        mPlayCover.startAnimation(coverRotateAnim);
                    }
                    mPlayerManager.resumePlay();
                }
                break;

            case R.id.img_album_list:
                Animation listAnim = AnimationUtils.loadAnimation(PlayActivity.this, R.anim.fav_anim_in);
                v.startAnimation(listAnim);
                showPlayingList();
                break;

            case R.id.img_album_fav:
                if (UserAuth.getInstance().isLogin(this)) {
                    favAblum(v);
                }
                break;

            case R.id.rl_album_comment:
                Animation commentAnim = AnimationUtils.loadAnimation(PlayActivity.this, R.anim.fav_anim_in);
                v.startAnimation(commentAnim);
                this.displayAlbumComment();
                break;

            case R.id.img_album_download:
                downloadStory(v);
                break;

            case R.id.img_album_share:
                shareStory(v);
                break;

            case R.id.tv_play_list_close:
                break;

            default:
                break;
        }
    }

    //TODO:这里PlayingStory构造垃圾，播放的数据逻辑实现垃圾
    private void shareStory(View v) {

        Logger.d("shareStory Click!!!!");
        Animation shareAnim = AnimationUtils.loadAnimation(PlayActivity.this, R.anim.fav_anim_in);
        v.startAnimation(shareAnim);
        PlayingStory playingStory = mPlayerManager.getPlayingStory();
        final AlbumInfo albumInfo = playingStory.albumInfo;

        //如果是搜索过来则没有专辑信息，需要重新加载
        String title = "";
        String content = "";
        String iconUrl = "";
        String musicUrl = playingStory.mediapath;
        String url = Constant.SHARE_ALBUM_URL + playingStory.albumid;

        if (albumInfo == null || albumInfo.getStorylist().size() == 0) {

            title = playingStory.title;
            content = playingStory.intro;
            iconUrl = playingStory.playcover;
            url = Constant.SHARE_ALBUM_URL + playingStory.albumid;

        } else {

            title = albumInfo.getTitle();
            content = albumInfo.getIntro();
            iconUrl = albumInfo.getCover();
        }

        if (title == null || title.equals("")) {
            title = this.getString(R.string.app_name);
        }

        if (content == null || content.equals("")) {
            content = this.getString(R.string.app_desc);
        }

        ShareBean shareBean = new ShareBean(title, content, iconUrl, musicUrl, url);
        mController = new ShareDialog().show(this, shareBean);
    }

    //下载
    private void downloadStory(View v) {

        Animation donwloadAnim = AnimationUtils.loadAnimation(PlayActivity.this, R.anim.fav_anim_in);
        v.startAnimation(donwloadAnim);
        Toast.makeText(this, "开始下载...", Toast.LENGTH_SHORT).show();
        //如果是搜索过来则没有专辑信息，需要重新加载
        if (mPlayerManager.getPlayingStory().albumInfo == null) {

            LHttpRequest.AlbumInfoRequest albumInfoRequest = mRetrofit.create(LHttpRequest.AlbumInfoRequest.class);
            Call<JsonResponse<Album>> call = albumInfoRequest.getResult(mPlayerManager.getPlayingStory().albumid, 1);
            call.enqueue(new Callback<JsonResponse<Album>>() {

                @Override
                public void onResponse(Call<JsonResponse<Album>> call, Response<JsonResponse<Album>> response) {

                    if (response.isSuccessful() && response.body().isSuccessful()) {
                        Album data = response.body().getData();
                        DownLoadClientImpl.getInstance().addAlbum(data.getAlbumInfo());
                        download();
                    } else {
                        Logger.e(response.toString());
                    }
                }

                @Override
                public void onFailure(Call<JsonResponse<Album>> call, Throwable t) {
                    Logger.e(t.toString());
                }
            });
        } else {
            download();
        }
        MobclickAgent.onEvent(this, "event_download");
    }

    private void download() {
        if (mPlayerManager.getPlayingStory().playType == PlayerManager.PlayType.NET) {
            Story music = mPlayerManager.getPlayList().get(mPlayerManager.position).getStory();
            AudioDownLoad downLoad = new AudioDownLoad(music, mPlayerManager.position);
            DownLoadClientImpl.getInstance().download(downLoad);
        } else {
            new TipDialog.Builder(PlayActivity.this).setAutoDismiss(true)
                    .setTransparent(false).setTipText("嗯哈，你已经下载过啦").create().show();
        }
    }

    private void favAblum(final View view) {

        final AlbumInfo albumInfo = mPlayerManager.getPlayingStory().albumInfo;
        //如果是搜索过来则没有专辑信息，需要重新加载
        if (albumInfo == null) {

            LHttpRequest.AlbumInfoRequest albumInfoRequest = mRetrofit.create(LHttpRequest.AlbumInfoRequest.class);
            Call<JsonResponse<Album>> call = albumInfoRequest.getResult(mPlayerManager.getPlayingStory().albumid, 1);
            call.enqueue(new Callback<JsonResponse<Album>>() {

                @Override
                public void onResponse(Call<JsonResponse<Album>> call, Response<JsonResponse<Album>> response) {

                    if (response.isSuccessful() && response.body().isSuccessful()) {
                        Album data = response.body().getData();
                        DownLoadClientImpl.getInstance().addAlbum(data.getAlbumInfo());
                        fav(view, data.getAlbumInfo());
                    } else {
                        Logger.e(response.toString());
                    }
                }

                @Override
                public void onFailure(Call<JsonResponse<Album>> call, Throwable t) {
                    Logger.e(t.toString());
                }
            });
        } else {
            fav(view, albumInfo);
        }
        MobclickAgent.onEvent(this, "event_collect");
    }

    private void fav(final View view, final AlbumInfo albumInfo) {
        if (albumInfo.getFav() == 0) {

            LHttpRequest.AddFavAlbumRequest addFavAlbumRequest = mRetrofit.create(LHttpRequest.AddFavAlbumRequest.class);
            Call<JsonResponse<String>> call = addFavAlbumRequest.getResult(mPlayerManager.getPlayingStory().albumid);
            call.enqueue(new Callback<JsonResponse<String>>() {

                @Override
                public void onResponse(Call<JsonResponse<String>> call, Response<JsonResponse<String>> response) {

                    if (response.isSuccessful() && response.body().isSuccessful()) {
                        view.setSelected(true);
                        Animation favInAnim = AnimationUtils.loadAnimation(PlayActivity.this, R.anim.fav_anim_in);
                        view.startAnimation(favInAnim);
                        albumInfo.setFav(1);
                        new TipDialog.Builder(PlayActivity.this)
                                .setAutoDismiss(true).setTransparent(false)
                                .setTipText("收藏成功！").create().show();
                        albumInfo.updateAll("albumid =?",
                                albumInfo.getId());
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
            Call<JsonResponse<String>> call = delFavAlbumRequest.getResult(mPlayerManager.getPlayingStory().albumid);
            call.enqueue(new Callback<JsonResponse<String>>() {

                @Override
                public void onResponse(Call<JsonResponse<String>> call, Response<JsonResponse<String>> response) {

                    if (response.isSuccessful() && response.body().isSuccessful()) {

                        view.setSelected(false);
                        albumInfo.setFav(0);
                        Animation favOutAnim = AnimationUtils.loadAnimation(PlayActivity.this, R.anim.fav_anim_out);
                        view.startAnimation(favOutAnim);
                        new TipDialog.Builder(PlayActivity.this)
                                .setAutoDismiss(true).setTransparent(false)
                                .setTipText("取消收藏成功！").create().show();
                        albumInfo.updateAll("albumid =?",
                                albumInfo.getId());

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
    }

    private void changePlayMode() {
        int mode = mPlayerManager.getPlayMode();
        if (mode == PlayerManager.PlayMode.CYCLE) {
            mPlayerManager.setPlayMode(PlayerManager.PlayMode.SINGLE);
            mMusicModeImg.setImageResource(R.drawable.selector_btn_player_single);
            TopDialog.create(this, (ViewGroup) findViewById(R.id.rl_content), "已经切换到单曲循环模式").show();
        } else if (mode == PlayerManager.PlayMode.SINGLE) {
            mPlayerManager.setPlayMode(PlayerManager.PlayMode.RANDOM);
            mMusicModeImg.setImageResource(R.drawable.selector_btn_player_random);
            TopDialog.create(this, (ViewGroup) findViewById(R.id.rl_content), "已经切换到随机播放模式").show();
        } else {
            mPlayerManager.setPlayMode(PlayerManager.PlayMode.CYCLE);
            mMusicModeImg.setImageResource(R.drawable.selector_btn_player_repeat);
            TopDialog.create(this, (ViewGroup) findViewById(R.id.rl_content), "已经切换到顺序循环模式").show();
        }
    }

    private void setPlayMode() {
        int mode = mPlayerManager.getPlayMode();
        if (mode == PlayerManager.PlayMode.CYCLE) {
            mMusicModeImg.setImageResource(R.drawable.selector_btn_player_repeat);
        } else if (mode == PlayerManager.PlayMode.SINGLE) {
            mMusicModeImg.setImageResource(R.drawable.selector_btn_player_single);
        } else {
            mMusicModeImg.setImageResource(R.drawable.selector_btn_player_random);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        if (fromUser) {
            mFinishTimeTv.setText(TimeUtils.getShortTimeShot(progress));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

        isTrackingTouch = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

        int progress = seekBar.getProgress();
        mSeekBar.setProgress(progress);
        mPlayerManager.seekPlay(progress);
        isTrackingTouch = false;
    }

    @Override
    public void notify(PlayingStory music) {
        switch (music.playState) {
            case PLAY:
                if (isFirst) {
                    mPauseImg.setImageResource(R.drawable.btn_player_pause_normal);
                    initPlayView();
                }
                break;
            case START:
                mPauseImg.setImageResource(R.drawable.btn_player_pause_normal);
                initPlayView();
                break;
            case PAUSE:
                if (isFirst)
                    initPlayView();
                mPauseImg.setImageResource(R.drawable.btn_player_play_normal);
                break;
            case RESUME:
                if (isFirst)
                    initPlayView();
                mPauseImg.setImageResource(R.drawable.btn_player_pause_normal);
                break;
            case STOP:
                if (isFirst)
                    initPlayView();
                mPauseImg.setImageResource(R.drawable.btn_player_play_normal);
                break;
            case ERROR:
                break;
        }
        mSeekBar.setMax(music.times);
        mSeekBar.setSecondaryProgress(music.buffer);

        //按住SeekBar时进度不更改
        if (!isTrackingTouch) {
            mSeekBar.setProgress(music.current);
            mFinishTimeTv.setText(TimeUtils.getShortTimeShot(music.current));
        }

        if (isFirst) {
            isFirst = false;
            if (coverRotateAnim != null && (music.playState == PlayerManager.PlayState.PLAY || music.playState == PlayerManager.PlayState.START || music.playState == PlayerManager.PlayState.RESUME)) {
                mPlayCover.startAnimation(coverRotateAnim);
            }
        }
    }

    private void showPlayingList() {

        AlbumInfo albumInfo = mPlayerManager.getPlayingStory().albumInfo;
        if (mPlayerManager.getPlayingStory().albumSource == PlayerManager.AlbumSource.ALBUM_DETAIL && null != albumInfo) {
            Intent intent = new Intent(this, AlbumDetailActivity.class);
            intent.putExtra("albumId", albumInfo.getId());
            startActivityForNew(intent);
        } else if (mPlayerManager.getPlayingStory().albumSource == PlayerManager.AlbumSource.DOWNLOAD) {
            Intent i = new Intent(this, DownloadStoryActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("type", DownloadStoryActivity.HISTORY_TYPE);
            i.putExtra("album", albumInfo);
            startActivityForNew(i);
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        mPlayerManager = null;
        PlayerManager.getInstance().mOnPlayingDownloadListener = null;
        PlayerManager.getInstance().unRegister(this);
        EventBus.getDefault().unregister(this);
        Config.dialog = null;
    }


    public void initPlayView() {

        PlayingStory music = mPlayerManager.getPlayingStory();
        setTitleName(music.title);
        Uri playCoverUri = null;
        if (music.playcover != null && !music.playcover.equals("")) {
            playCoverUri = Uri.parse(music.playcover);
        } else if (music.albumInfo != null && music.albumInfo.getCover() != null && !music.albumInfo.getCover().equals("")) {
            playCoverUri = Uri.parse(music.albumInfo.getCover());
        }
        if (playCoverUri != null) {
            mPlayCover.setImageURI(playCoverUri);
        }

        if (music.albumInfo != null) {
            mFavImg.setSelected(music.albumInfo.getFav() == 1);
            if (music.albumInfo.getCommentnum() > 0) {
                mCommentImg.setImageResource(R.drawable.play_btn_comments_selector);
                mCommentTv.setText(music.albumInfo.getCommentnum() != 0 ? (music.albumInfo.getCommentnum() + "") : "");
            }
        } else {
            mFavImg.setSelected(false);
        }
        mLeaveTimeTv.setText(TimeUtils.getShortTimeShot(music.times));
        notifyDownload();
    }


    @Override
    public void finish() {
        super.oldFinish();
        animationForOTop();
        mPlayCover.clearAnimation();
    }


    @Override
    public void notifyDownload() {

        PlayingStory music = mPlayerManager.getPlayingStory();
        mDownloadImg.setSelected(music.playType == PlayerManager.PlayType.LOCAL);

    }


    public void onEventMainThread(FavEvent favEvent) {

        AlbumInfo albumInfo = PlayerManager.getInstance().getPlayingStory().albumInfo;
        if (albumInfo != null && favEvent.albumInfo.getId().equals(albumInfo.getId())) {
            if (favEvent.fav == 1) {
                albumInfo.setFav(1);
                mFavImg.setSelected(true);
            } else {
                albumInfo.setFav(0);
                mFavImg.setSelected(false);
            }
        }
    }

    public void onEventMainThread(CommentEvent commentEvent) {
        AlbumInfo albumInfo = PlayerManager.getInstance().getPlayingStory().albumInfo;
        if (albumInfo != null && commentEvent.albumInfo.getId().equals(albumInfo.getId())) {
            mCommentTv.setText(commentEvent.commentCount + "");
            albumInfo.setCommentnum(commentEvent.commentCount);
        }
    }

    private ShareAction mController;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    private void displayAlbumComment() {

        Intent intent = new Intent(this, AlbumCommentActivity.class);
        intent.putExtra("albumId", mPlayerManager.getPlayingStory().albumid);
        startActivity(intent);
    }
}