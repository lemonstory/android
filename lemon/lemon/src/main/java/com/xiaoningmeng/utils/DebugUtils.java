package com.xiaoningmeng.utils;

import android.util.Log;

import com.xiaoningmeng.BuildConfig;

public class DebugUtils {
	public static final String TAG = "lemon";
	public static final boolean ISTRACE = BuildConfig.LOG_DEBUG;

	public static void i(String msg) {

		i(TAG, msg);
	}

	public static void i(String tag, String msg) {

		if (ISTRACE) {
			Log.i(tag, msg == null ? "null" : msg);
		}
	}

	public static void d(String msg) {
		if (ISTRACE) {
			d(TAG, msg);
		}
	}

	public static void d(String tag, String msg) {
		if (ISTRACE) {
			Log.d(tag, msg == null ? "null" : msg);
		}
	}

	public static void syso(String msg) {
		if (ISTRACE) {
			System.out.println(msg);
		}
	}

	public static void e(String tag, String msg) {
		if (ISTRACE) {
			Log.e(tag, msg == null ? "null" : msg);
		}
	}

	public static void e(String msg) {
		if (ISTRACE) {
			e(TAG, msg);
		}
	}

	public static void exception(Exception e) {
		if (ISTRACE)
			e.printStackTrace();
	}

	public static void exception(Error e) {
		if (ISTRACE)
			e.printStackTrace();
	}

	public static void exception(String tag, Exception e) {

		e(tag, "exception Caused by:" + e.toString());
		for (StackTraceElement stackTraceElement : e.getStackTrace()) {
			e(tag, stackTraceElement.toString());
		}
	}

}
