package com.xiaoningmeng.player;
import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.PowerManager;

import com.xiaoningmeng.application.MyApplication;

public class SoundManager{
	private static SoundManager mInstance;
	private MediaPlayer mMediaPlayer;
	private int position;

	private boolean isPlayStory;
	
	public static SoundManager getInstance() {
		if (mInstance == null) {
			synchronized (SoundManager.class) {
				if (mInstance == null) {
					mInstance = new SoundManager();
				}
			}
		}
		return mInstance;
	}
	
	private SoundManager() {
		
	}
	//位置 和模式（循环  一次）
	public void start(Context context,int position,int mode){
		if(PlayerManager.getInstance().isPlaying()){
			isPlayStory = true;
			PlayerManager.getInstance().pausePlay();
		}else{
			isPlayStory = false;
		}
		AssetManager manager = context.getAssets();
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
			mMediaPlayer.release();
		}
		mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mMediaPlayer.setWakeMode(MyApplication.getInstance()
				.getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
		if(mode == SoundMode.CYCLE){
			mMediaPlayer.setLooping(true);
		}else{
			mMediaPlayer.setLooping(false);
		}
		try {
			AssetFileDescriptor fileDescriptor = manager.openFd("music_file"
					+ position + ".m4a");
			mMediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(),
							fileDescriptor.getStartOffset(),
							fileDescriptor.getLength());
			mMediaPlayer.prepare();
			mMediaPlayer.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void end(){
		if(mMediaPlayer != null && mMediaPlayer.isPlaying()){
			mMediaPlayer.pause();
		}
		if(isPlayStory)
			PlayerManager.getInstance().startPlay(position, PlayerManager.getInstance().getPlayingStory().current);
	}


	
	public static class SoundMode{
		public static final int CYCLE = 0;
		public static final int ONCE = 1;
	}
	
	
}
