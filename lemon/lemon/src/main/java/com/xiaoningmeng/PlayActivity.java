package com.xiaoningmeng;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
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
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.xiaoningmeng.application.MyApplication;
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
import com.xiaoningmeng.http.LHttpHandler;
import com.xiaoningmeng.http.LHttpRequest;
import com.xiaoningmeng.player.PlayObserver;
import com.xiaoningmeng.player.PlayerManager;
import com.xiaoningmeng.player.PlayerManager.OnPlayingDownloadListener;
import com.xiaoningmeng.player.PlayerManager.PlayMode;
import com.xiaoningmeng.player.PlayerManager.PlayType;
import com.xiaoningmeng.utils.TimeUtils;
import com.xiaoningmeng.view.ShareDialog;
import com.xiaoningmeng.view.dialog.TipDialog;
import com.xiaoningmeng.view.dialog.TopDialog;

import de.greenrobot.event.EventBus;

public class PlayActivity extends BaseActivity implements OnClickListener,
		PlayObserver, OnSeekBarChangeListener,OnPlayingDownloadListener{

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
	private boolean isFirst = true;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Fresco.initialize(this);
		setContentView(R.layout.activity_player);
		setTinitColor(getResources().getColor(R.color.system_bar_tint_color));
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
		mSeekBar = (SeekBar) this.findViewById(R.id.seekbar_play_music);
		mMusicModeImg = (ImageView) this.findViewById(R.id.img_music_mode);
		mPlayCover = (SimpleDraweeView) this.findViewById(R.id.img_play_cover);
		mDownloadImg = (ImageView) this.findViewById(R.id.img_music_download);
		mPauseImg = (ImageView) this.findViewById(R.id.img_music_pause);
		mCommentTv = (TextView) this.findViewById(R.id.tv_music_comment);
		setRightHeadIcon(R.drawable.play_btn_reminder_selecotr);
		mFavImg = (ImageView) findViewById(R.id.img_music_fav);
		findViewById(R.id.img_head_right).setOnClickListener(this);
		mMusicModeImg.setOnClickListener(this);
		mSeekBar.setOnSeekBarChangeListener(this);
		mSeekBar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mPlayerManager.pausePlay();

			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_music_next:
			mPlayerManager.nextPlay();
			break;
		case R.id.img_music_per:
			mPlayerManager.prevPlay();
			break;
		case R.id.img_music_mode:
			Animation modeAnim = AnimationUtils.loadAnimation(PlayActivity.this, R.anim.fav_anim_in);
			v.startAnimation(modeAnim);
			changePlayMode();
			break;
		case R.id.img_head_right:
			startActivityForNew(new Intent(this, ReminderActivity.class));
			break;
		case R.id.img_music_pause:
			if (mPlayerManager.isPlaying()) {
				mPlayerManager.pausePlay();
				((ImageView) v).setImageResource(R.drawable.player_btn_play_normal);
			} else {
				((ImageView) v).setImageResource(R.drawable.player_btn_pause_normal);
				mPlayerManager.resumePlay();
			}
			break;
		case R.id.img_music_list:
			Animation listAnim = AnimationUtils.loadAnimation(PlayActivity.this, R.anim.fav_anim_in);
			v.startAnimation(listAnim);
			showPlayingList();
			break;
		case R.id.img_music_fav:
			if(UserAuth.auditUser(this, "登录后,才能收藏故事喔.")){
				favAblum(v);
			}
			break;
		case R.id.rl_music_comment:
			Animation commentAnim = AnimationUtils.loadAnimation(PlayActivity.this, R.anim.fav_anim_in);
			v.startAnimation(commentAnim);
			Intent intent = new Intent(this, AblumDetailActivity.class);
			intent.putExtra("isScroll", true);
			intent.putExtra("pager", 2);
			intent.putExtra("albumId", mPlayerManager.getPlayingStory().albumid);
			startActivityForNew(intent);
			break;
		case R.id.img_music_download:
			downloadStory(v);
			break;
		case R.id.img_music_share:
			shareStory(v);
			break;
		case R.id.tv_play_list_close:
			break;
		default:
			break;
		}
	}

	//分享
	private void shareStory(View v) {
		Animation shareAnim = AnimationUtils.loadAnimation(PlayActivity.this, R.anim.fav_anim_in);
		v.startAnimation(shareAnim);
		final AlbumInfo albumInfo = mPlayerManager.getPlayingStory().albumInfo;
		//如果是搜索过来则没有专辑信息，需要重新加载
		if(albumInfo == null){
			LHttpRequest.getInstance().albumInfoReq(this,10,mPlayerManager.getPlayingStory().albumid,
					MyApplication.getInstance().getUid(),
					new LHttpHandler<Album>(this) {

						@Override
						public void onGetDataSuccess(Album data) {
							AlbumInfo albumInfo2 = data.getAlbuminfo();
							Story story = albumInfo.getStorylist().get(0);
							ShareBean shareBean = new ShareBean(albumInfo2.getTitle(),albumInfo.getIntro(), albumInfo2.getCover(),story.getMediapath(), Constant.SHARE_ALBUM_URL + albumInfo2.getAlbumid());
							mController = new ShareDialog().show(PlayActivity.this, shareBean);
						}
					});
		}else {
			Story story = albumInfo.getStorylist().get(0);
			ShareBean shareBean = new ShareBean(albumInfo.getTitle(),albumInfo.getIntro(), albumInfo.getCover(),story.getMediapath(), Constant.SHARE_ALBUM_URL + albumInfo.getAlbumid());
			mController = new ShareDialog().show(this, shareBean);
		}
	}

	//下载
	private void downloadStory(View v) {

		Animation donwloadAnim = AnimationUtils.loadAnimation(PlayActivity.this, R.anim.fav_anim_in);
		v.startAnimation(donwloadAnim);
		Toast.makeText(this, "开始下载...", Toast.LENGTH_SHORT).show();
		//如果是搜索过来则没有专辑信息，需要重新加载
		if(mPlayerManager.getPlayingStory().albumInfo == null){
			LHttpRequest.getInstance().albumInfoReq(this,10,mPlayerManager.getPlayingStory().albumid,
					MyApplication.getInstance().getUid(),
					new LHttpHandler<Album>(this) {

						@Override
						public void onGetDataSuccess(Album data) {
							DownLoadClientImpl.getInstance().addAlbum(data.getAlbuminfo());
							download();
						}
					});
		}else {
			download();
		}
		MobclickAgent.onEvent(this, "event_download");
	}

	private void download() {
		if (mPlayerManager.getPlayingStory().playType == PlayType.NET) {
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
		if(albumInfo == null){
			LHttpRequest.getInstance().albumInfoReq(this,10,mPlayerManager.getPlayingStory().albumid,
				MyApplication.getInstance().getUid(),
				new LHttpHandler<Album>(this) {

					@Override
					public void onGetDataSuccess(Album data) {
						DownLoadClientImpl.getInstance().addAlbum(data.getAlbuminfo());
						fav(view, data.getAlbuminfo());
					}
				});
		}else {
			fav(view, albumInfo);
		}
		MobclickAgent.onEvent(this,"event_collect");
	}

	private void fav(final View view, final AlbumInfo albumInfo) {
		if (albumInfo.getFav() == 0) {
			LHttpRequest.getInstance().addFavAlbumRequest(this,
					albumInfo.getId(),
					new LHttpHandler<String>(this) {

						@Override
						public void onGetDataSuccess(String data) {
							view.setSelected(true);
							Animation favInAnim = AnimationUtils.loadAnimation(PlayActivity.this, R.anim.fav_anim_in);
							view.startAnimation(favInAnim);
							albumInfo.setFav(1);
							new TipDialog.Builder(PlayActivity.this)
									.setAutoDismiss(true).setTransparent(false)
									.setTipText("收藏成功！").create().show();
							albumInfo.updateAll("albumid =?",
									albumInfo.getAlbumid());

						}
					});
		} else {
			LHttpRequest.getInstance().delFavAlbumRequest(this,
					mPlayerManager.getPlayingStory().albumid,
					new LHttpHandler<String>(this) {

						@Override
						public void onGetDataSuccess(String data) {
							view.setSelected(false);
							albumInfo.setFav(0);
							Animation favOutAnim = AnimationUtils.loadAnimation(PlayActivity.this, R.anim.fav_anim_out);
							view.startAnimation(favOutAnim);
							new TipDialog.Builder(PlayActivity.this)
									.setAutoDismiss(true).setTransparent(false)
									.setTipText("取消收藏成功！").create().show();
							albumInfo.updateAll("albumid =?",
									albumInfo.getAlbumid());
						}
					});
		}
	}

	private void changePlayMode() {
		int mode = mPlayerManager.getPlayMode();
		if (mode == PlayMode.CYCLE) {
			mPlayerManager.setPlayMode(PlayMode.SINGLE);
			mMusicModeImg.setImageResource(R.drawable.play_btn_once_selecotr);
			TopDialog.create(this, (ViewGroup) findViewById(R.id.rl_content), "已经切换到单曲循环模式").show();
		} else if (mode == PlayMode.SINGLE) {
			mPlayerManager.setPlayMode(PlayMode.RANDOM);
			mMusicModeImg.setImageResource(R.drawable.play_btn_random_selecotr);
			TopDialog.create(this, (ViewGroup)findViewById(R.id.rl_content),"已经切换到随机播放模式").show();
		} else {
			mPlayerManager.setPlayMode(PlayMode.CYCLE);
			mMusicModeImg.setImageResource(R.drawable.play_btn_repat_selecotr);
			TopDialog.create(this,(ViewGroup)findViewById(R.id.rl_content),"已经切换到顺序循环模式").show();
		}
	}

	private void setPlayMode() {
		int mode = mPlayerManager.getPlayMode();
		if (mode == PlayMode.CYCLE) {
			mMusicModeImg.setImageResource(R.drawable.play_btn_repat_selecotr);
		} else if (mode == PlayMode.SINGLE) {
			mMusicModeImg.setImageResource(R.drawable.play_btn_once_selecotr);
		} else {
			mMusicModeImg.setImageResource(R.drawable.play_btn_random_selecotr);
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {

		int progress = seekBar.getProgress();

		mSeekBar.setProgress(progress);
		mPlayerManager.seekPlay(progress);
	}

	@Override
	public void notify(PlayingStory music) {

		switch (music.playState) {
		case PLAY:
			if (isFirst) {			
				mPauseImg.setImageResource(R.drawable.player_btn_pause_normal);
				initPlayView();
			}
			break;
		case START:
			mPauseImg.setImageResource(R.drawable.player_btn_pause_normal);
			initPlayView();
			break;
		case PAUSE:
			if (isFirst) 				
				initPlayView();
			mPauseImg.setImageResource(R.drawable.player_btn_play_normal);
			break;
		case RESUME:
			if (isFirst) 				
				initPlayView();
			mPauseImg.setImageResource(R.drawable.player_btn_pause_normal);
			break;
		case STOP:
			if (isFirst) 				
				initPlayView();
			mPauseImg.setImageResource(R.drawable.player_btn_play_normal);
			break;
		case ERROR:
			break;
		}
		mSeekBar.setMax(music.times);
		mSeekBar.setProgress(music.current);
		mFinishTimeTv.setText(TimeUtils.getShortTimeShot(music.current));
		mSeekBar.setSecondaryProgress(music.buffer);
		if (isFirst) {
			isFirst = false;
		}
	}

	private void showPlayingList(){
		
		AlbumInfo albumInfo = mPlayerManager.getPlayingStory().albumInfo;
		if(mPlayerManager.getPlayingStory().albumSource == PlayerManager.AlbumSource.ALBUM_DETAIL){
			Intent intent = new Intent(this, AblumDetailActivity.class);
			intent.putExtra("albumId", albumInfo.getAlbumid());
			startActivityForNew(intent);
		}else if(mPlayerManager.getPlayingStory().albumSource == PlayerManager.AlbumSource.DOWNLOAD){
			Intent i = new Intent(this, DownloadStoryActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.putExtra("type", DownloadStoryActivity.HISTORY_TYPE);
			i.putExtra("album", albumInfo);
			startActivityForNew(i);
		}
	}

	@Override
	protected void onDestroy() {
		PlayerManager.getInstance().unRegister(this);
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}


	public void initPlayView() {
		
		PlayingStory music = mPlayerManager.getPlayingStory();
		setTitleName(music.title);
		Uri playCoverUri = null;
		if (music.playcover != null && !music.playcover.equals("")){
			playCoverUri = Uri.parse(music.playcover);
		}else if (music.albumInfo != null && music.albumInfo.getCover() != null && !music.albumInfo.getCover().equals("")) {
			playCoverUri = Uri.parse(music.albumInfo.getCover());
		}
		if (playCoverUri != null) {
			mPlayCover.setImageURI(playCoverUri);
		}

		if(music.albumInfo != null){
			mFavImg.setSelected(music.albumInfo.getFav() == 1);
			mCommentTv.setText(music.albumInfo.getCommentnum() != 0 ? (music.albumInfo.getCommentnum()+""):"");
		}else{
			mFavImg.setSelected(false);
			mCommentTv.setText(1+"");
		}
		mLeaveTimeTv.setText(" / "+ TimeUtils.getShortTimeShot(music.times));
		notifyDownload();
	}


	@Override
	public void finish() {
		super.oldFinish();
		animationForOTop();
	}

	

	@Override
	public void notifyDownload() {

		PlayingStory music = mPlayerManager.getPlayingStory();
		mDownloadImg.setSelected(music.playType == PlayType.LOCAL);

	}
	
	private boolean hasPermanentKey() {
		  boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
		  boolean hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);
		  if (hasBackKey && hasHomeKey) {
		  		 return false;
		  } else {
		  		 return true;
		  }
		}
	
	public void onEventMainThread(FavEvent favEvent){
		
		AlbumInfo albumInfo = PlayerManager.getInstance().getPlayingStory().albumInfo;
		if(albumInfo != null && favEvent.albumInfo.getAlbumid().equals(albumInfo.getAlbumid())){
			if(favEvent.fav == 1){
				albumInfo.setFav(1);
				mFavImg.setSelected(true);
			}else{
				albumInfo.setFav(0);
				mFavImg.setSelected(false);
			}
		}
	}
	
	public void onEventMainThread(CommentEvent commentEvent){
		AlbumInfo albumInfo = PlayerManager.getInstance().getPlayingStory().albumInfo;
		if(albumInfo != null && commentEvent.albumInfo.getAlbumid().equals(albumInfo.getAlbumid())){
			mCommentTv.setText(commentEvent.commentCount+"");
			albumInfo.setCommentnum(commentEvent.commentCount);
		}
	}

	private ShareAction mController;
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		UMShareAPI.get(this).onActivityResult( requestCode, resultCode, data);
	}
}
