package com.xiaoningmeng.player;

import com.xiaoningmeng.download.DownloadNotificationManager;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class MusicService extends Service {

	private PlayerManager mPlayerManager;
	private PlayNotificationManager mPlayNotificationManager;
	private PhoneStateListener mPhoneStateListener;
	public static final int PLAYING_NOTIFY_ID = 667667;
	private TelephonyManager mTelephonyManager;

	@Override
	public IBinder onBind(Intent intent) {

		return null;
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
				if (state == TelephonyManager.CALL_STATE_IDLE) {

				} else {
				}
			}
		};
		mTelephonyManager.listen(mPhoneStateListener,
				PhoneStateListener.LISTEN_CALL_STATE);
	}

	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {

		super.onStart(intent, startId);
		if(mPlayNotificationManager.notification != null){
			startForeground(PLAYING_NOTIFY_ID,
				mPlayNotificationManager.notification);
		}
	}

	public void onDestroy() {
		mTelephonyManager.listen(mPhoneStateListener,
				PhoneStateListener.LISTEN_NONE);
		mPlayerManager.stopPlay();
		DownloadNotificationManager.getInstance().cancel();
		stopForeground(true);
		System.exit(0);
		super.onDestroy();
	}

	public static void startService(Context context) {
		Intent i = new Intent();
		i.setClass(context, MusicService.class);
		context.startService(i);
	}

	public static void stopService(Context context) {
		Intent i = new Intent();
		i.setClass(context, MusicService.class);
		context.stopService(i);
	}
}
