package com.xiaoningmeng.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TelUtil {

	// 手机号码检查
	public static boolean isTel(String tel) {
		Pattern p = Pattern
				.compile("^((13[0-9])|(14[0-9])|(15[0-9])|(17[0-9])|(18[0-9]))\\d{8}$");
		Matcher m = p.matcher(tel);
		return m.matches();
	}

	// 姓名检查
	public static boolean isChineseName(String name) {
		return name.matches("[\u4e00-\u9fa5]{2,6}");
	}

	public static boolean isPwd(String pwd) {
		String regex = "^[0-9a-zA-Z]{6,16}$";/* "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,12}$" */
		return pwd.matches(regex);
	}

	public static boolean isEmail(String email) {

		String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
		Pattern regex = Pattern.compile(check);
		Matcher matcher = regex.matcher(email);
		return matcher.matches();
	}
	
	  /**
     * 验证QQ号码
     * 
     * @param QQ
     * @return
     */
    public static boolean checkQQ(String QQ) {
    	 String regex = "[1-9]\\d{4,14}";
    	return Pattern.compile(regex).matcher(QQ).matches();
    }
	
	  /**
	    * 判断邮编
	    * @param paramString
	    * @return
	    */
	   public static boolean isZipNO(String zipString){
	      String str = "^[1-9][0-9]{5}$";
	      return Pattern.compile(str).matcher(zipString).matches();
	   }
}
