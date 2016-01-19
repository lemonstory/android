package com.xiaoningmeng.utils;

import com.xiaoningmeng.http.ConstantURL;

public class AvatarUtils {

	
	public static String getAvatarUrl(String uid,String avatarTime,int size){

		return ConstantURL.AVATAR_URL+"/avatar/"+uid+"/"+avatarTime+(size == -1?"":("/"+size));
	}
	
	//常用时间
	
	public static String getShortTimeStr(String time) {
		
		String t =  time.substring(11, time.length()-3);
		String date = time.substring(8, 10);
		String month = time.substring(5, 7);
		
		return month+"/"+date +" "+t;
	}
	

	//常用时间
	
	public static String getShortTimeStr2(String time) {
		
		String date = time.substring(8, 10);
		String month = time.substring(5, 7);
		
		return month+"/"+date;
	}

}
