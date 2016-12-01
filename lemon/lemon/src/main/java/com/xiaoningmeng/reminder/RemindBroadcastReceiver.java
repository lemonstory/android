package com.xiaoningmeng.reminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xiaoningmeng.AlarmActivity;
import com.xiaoningmeng.DownloadStoryActivity;
import com.xiaoningmeng.PlayActivity;
import com.xiaoningmeng.bean.AlbumInfo;
import com.xiaoningmeng.bean.WeekDay;
import com.xiaoningmeng.player.PlayerManager;
import com.xiaoningmeng.utils.PreferenceUtil;

import java.util.Calendar;

public class RemindBroadcastReceiver extends BroadcastReceiver {

	public static final String NOTIFICATION_PAUSE = "com.xiaoningmeng.notification_pause";
	public static final String NOTIFICATION_RESUME = "com.xiaoningmeng.notification_resume";
	public static final String NOTIFICATION_NEXT = "com.xiaoningmeng.notification_next";
	public static final String NOTIFICATION_PLAY = "com.xiaoningmeng.notification_play";
	public static final String NOTIFICATION_DOWNLOAD = "com.xiaoningmeng.notification_download";
	public static final String NOTIFICATION_PER = "com.xiaoningmeng.notification_per";
	public static final String REMINDER_CLOSE = "com.xiaoningmeng.reminder_close";
	public static final String REMINDER = "com.xiaoningmeng.reminder";
	public static final String REMINDER_DELAY = "com.xiaoningmeng.reminder_delay";

	@Override
	public void onReceive(Context context, Intent intent) {

		String action = intent.getAction();
		if (NOTIFICATION_PAUSE.equals(action)) {
			PlayerManager.getInstance().pausePlay();
		} else if (NOTIFICATION_RESUME.equals(action)) {
			PlayerManager.getInstance().resumePlay();
		} else if (NOTIFICATION_NEXT.equals(action)) {
			PlayerManager.getInstance().nextPlay();
		} else if (NOTIFICATION_PER.equals(action)) {
			PlayerManager.getInstance().prevPlay();
		} else if (REMINDER_CLOSE.equals(action)) {
			PlayerManager.getInstance().pausePlay();
		} else if (REMINDER.equals(action) || REMINDER_DELAY.equals(action)) {
			alarmReimder(context);
		} else if (NOTIFICATION_PLAY.equals(action)) {
			Intent i = new Intent(context, PlayActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(i);
		}else if(NOTIFICATION_DOWNLOAD.equals(action)){
			notifyDownload(context, intent);
		}
	}

	private void notifyDownload(Context context, Intent intent) {
		
		int type = intent.getIntExtra("type", DownloadStoryActivity.HISTORY_TYPE);
		AlbumInfo albumInfo = intent.getParcelableExtra("album");
		Intent i = new Intent(context, DownloadStoryActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.putExtra("type", type);
		i.putExtra("album", albumInfo);
		context.startActivity(i);
	}

	private void alarmReimder(Context context) {
		Calendar now = Calendar.getInstance();
		//一周第一天是否为星期天
		boolean isFirstSunday = (now.getFirstDayOfWeek() == Calendar.SUNDAY);
		//获取周几
		int weekDay = now.get(Calendar.DAY_OF_WEEK);
		//若一周第一天为星期天，则-1
		if(isFirstSunday){
		  weekDay = weekDay - 1;
		  if(weekDay == 0){
		    weekDay = 7;
		  }
		}
		boolean isOpen = PreferenceUtil.getBoolean("isOpenAlarm", false);
		if(isOpen && WeekDay.isCheck(weekDay-1)){
			Intent i = new Intent(context, AlarmActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(i);
		}
	}
}
