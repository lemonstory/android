package com.xiaoningmeng.utils;

import android.util.Log;

public class TestUtils {
	public static long time;

	public static void getBetweenTime(String tag){
		long t= System.currentTimeMillis();
		if(time != 0){
			Log.e("huang", tag+(t-time));
		}
		time = t;
	}
}
