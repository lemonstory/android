package com.xiaoningmeng.reminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class Reminder {

	private static void remind(Context context, boolean cancel, int seconds) {

		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(context, RemindBroadcastReceiver.class);
		i.setAction(RemindBroadcastReceiver.REMINDER_CLOSE);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
		if (cancel) {
			am.cancel(pi);
		} else {
			Calendar c = Calendar.getInstance();
			am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + seconds
					* 1000, pi);

		}
	}

	private static void remindAlarm(Context context, boolean cancel, int hour,int minute) {

		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(context, RemindBroadcastReceiver.class);
		i.setAction(RemindBroadcastReceiver.REMINDER);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
		if (cancel) {
			am.cancel(pi);
		} else {
			Calendar cal=Calendar.getInstance();
			int currentHour = cal.get(Calendar.HOUR_OF_DAY);
			int currentMinute = cal.get(Calendar.MINUTE);
			if(currentHour > hour || (currentHour == hour && currentMinute >= minute)){
				cal.add(Calendar.DAY_OF_MONTH, 1);
			}
			cal.set(Calendar.HOUR_OF_DAY, hour);
			cal.set(Calendar.MINUTE, minute);
			cal.set(Calendar.SECOND,0);
			am.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 24*3600000, pi);

		}
	}
	
	private static void remindDelayAlarm(Context context, boolean cancel, int seconds) {


		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(context, RemindBroadcastReceiver.class);
		i.setAction(RemindBroadcastReceiver.REMINDER_DELAY);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
		if (cancel) {
			am.cancel(pi);
		} else {
			Calendar c = Calendar.getInstance();
			am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + seconds
					* 1000, pi);

		}
	}

	public static void cancelAlarmReminder(Context context) {
		remindAlarm(context, true, 0,0);
	}

	public static void setAlarmReminder(Context context, int hour,int minute) {
		remindAlarm(context, false, hour,minute);
	}
	
	public static void cancelDelayAlarmReminder(Context context) {
		remindDelayAlarm(context, true,0);
	}

	public static void setDelayAlarmReminder(Context context, int seconds) {
		remindDelayAlarm(context, false, seconds);
	}

	public static void cancelReminder(Context context) {
		remind(context, true, 0);
	}

	public static void setReminder(Context context, int seconds) {
		remind(context, false, seconds);
	}

}
