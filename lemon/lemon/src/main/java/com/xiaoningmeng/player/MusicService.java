package com.xiaoningmeng.player;

import com.xiaoningmeng.application.MyApplication;
import com.xiaoningmeng.download.DownloadNotificationManager;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class MusicService extends Service {

	private PlayerManager mPlayerManager;
	private PlayNotificationManager mPlayNotificationManager;
	private PhoneStateListener mPhoneStateListener;
	public static final int PLAYING_NOTIFY_ID = 667667;
	private TelephonyManager mTelephonyManager;


	/**
	 * binder
	 */
	public static class ServiceBinder extends Binder {

		private MusicService mService = null;

		public ServiceBinder(MusicService service) {
			mService = service;
		}

		public MusicService getService () {
			return mService;
		}
	}





	@Override
	public void onCreate() {
		super.onCreate();
		mPlayerManager = PlayerManager.getInstance();
		mPlayNotificationManager = PlayNotificationManager.getInstance();
		mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		mPhoneStateListener = new PhoneStateListener() {
			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
				switch(state){
					case TelephonyManager.CALL_STATE_IDLE:
						break;
					//接听电话 暂停播放
					case TelephonyManager.CALL_STATE_OFFHOOK:
						if(mPlayerManager.isPlaying()) {
							mPlayerManager.pausePlay();
						}
						break;
					//响铃
					case TelephonyManager.CALL_STATE_RINGING:
						if(mPlayerManager.isPlaying()) {
							mPlayerManager.pausePlay();
						}
						break;
				}
			}
		};
		mTelephonyManager.listen(mPhoneStateListener,
				PhoneStateListener.LISTEN_CALL_STATE);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return new ServiceBinder(this);
	}



	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		if(mPlayNotificationManager.notification != null){
			startForeground(PLAYING_NOTIFY_ID,
					mPlayNotificationManager.notification);
		}
		return START_STICKY;
	}

	@Override
	public boolean stopService(Intent name) {
		stopForeground(true);
		return super.stopService(name);
	}

	public void onDestroy() {
		mTelephonyManager.listen(mPhoneStateListener,
				PhoneStateListener.LISTEN_NONE);
		mPlayerManager.stopPlay();
		DownloadNotificationManager.getInstance().cancel();
		super.onDestroy();
	}

	public static void startService(Context context) {
		MyApplication.getInstance().startMusicService();
	}

	public static void stopService(Context context) {
		/*Intent i = new Intent();
		i.setClass(context, MusicService.class);
		context.stopService(i);*/
		MyApplication.getInstance().unbindMusicService();
	}
}
