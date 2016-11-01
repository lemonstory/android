package com.xiaoningmeng.utils;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class TimeUtils {
	public static long ONEDAYMILLIS = 24 * 60 * 60 * 1000;

	public static String getShowTimeString(long time) {

		Date date = new Date(time);
		return getShowTimeString(date);
	}

	public static String getShowTimeString1(long time) {
		Date date = new Date(time);
		return getSimpleDateFormat("yyyy-MM-dd", date);
	}

	public static String getShowTimeString(Date time) {
		long currentTimeMillis = System.currentTimeMillis();
		Date now = new Date(currentTimeMillis);
		long timeDistance = currentTimeMillis - time.getTime();
		long todayStartMillis = currentTimeMillis
				- (now.getHours() * 3600 + now.getMinutes() * 60 + now
						.getSeconds()) * 1000;
		if (timeDistance < 3600000) {
			if (timeDistance / (60000) == 0) {
				return "刚刚";
			}
			return timeDistance / (60000) + "分钟前";
		} else if (timeDistance < ONEDAYMILLIS) {
			return timeDistance / (3600000) + "小时前";
		} else if (time.getTime() > todayStartMillis - ONEDAYMILLIS) {
			return "1天前 ";
		} else if (time.getTime() > todayStartMillis - 2 * ONEDAYMILLIS) {
			return "2天前 ";
		} else if (time.getTime() > todayStartMillis - 3 * ONEDAYMILLIS) {
			return "3天前 ";
		} else if (time.getTime() > todayStartMillis - 4 * ONEDAYMILLIS) {
			return "4天前 ";
		} else if (time.getTime() > todayStartMillis - 5 * ONEDAYMILLIS) {
			return "5天前 ";
		} else if (time.getTime() > todayStartMillis - 6 * ONEDAYMILLIS) {
			return "6天前 ";
		} else if (time.getTime() > todayStartMillis - 7 * ONEDAYMILLIS) {
			return "7天前 ";
		} else {
			return getSimpleDateFormat("yyyy-MM-dd", time);
		}
	}

	public static String getShowTimeFromatString(String fromatDate) {
		return getShowTimeString(strToDateLong(fromatDate));
	}

	public static String getSimpleDateFormat(String pattern, Date time) {
		return new SimpleDateFormat(pattern).format(time);
	}

	public static Date getDateByString(String pattern, String dateStr) {
		SimpleDateFormat sf = new SimpleDateFormat(pattern);// 设置一个时间转换器
		Date d = null;
		if (dateStr != null && !"".equals(dateStr)) {
			try {
				dateStr = dateStr.replace("-", "");
				d = sf.parse(dateStr);// 将字符串s通过转换器转换为date类型
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return d;
	}

	public static boolean isSameDay(long currentTime, long lastTime) {
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		String last_time = sf.format(lastTime);
		String current_time = sf.format(currentTime);
		return last_time.equals(current_time);
	}

	public static int getTag(long duration_time) {
		int tag = (int) duration_time / 10000;
		if (duration_time / 1000 < 5) {
			tag = 5;
		} else if (duration_time % 10000 != 0) {
			tag = (tag + 1) * 10;
		} else {
			tag = tag * 10;
		}

		if (tag > 60) {
			tag = 70;
		}
		return tag;
	}

	public static Date afterDay(int num, String time) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(TimeUtils.getDateByString("yyyyMMddHHmmss", time));
		calendar.add(Calendar.DATE, num);
		Date createDate = calendar.getTime();
		return createDate;
	}

	/**
	 * 获取现在时间
	 * 
	 * @return 返回时间类型 yyyy-MM-dd HH:mm:ss
	 */
	public static Date getNowDate() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(currentTime);
		ParsePosition pos = new ParsePosition(8);
		Date currentTime_2 = formatter.parse(dateString, pos);
		return currentTime_2;
	}

	/**
	 * 获取现在时间
	 * 
	 * @return返回短时间格式 yyyy-MM-dd
	 */
	public static Date getNowDateShort() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(currentTime);
		ParsePosition pos = new ParsePosition(8);
		Date currentTime_2 = formatter.parse(dateString, pos);
		return currentTime_2;
	}

	/**
	 * 获取现在时间
	 * 
	 * @return返回字符串格式 yyyy-MM-dd HH:mm:ss
	 */
	public static String getStringDate() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	/**
	 * 获取现在时间
	 * 
	 * @return返回字符串格式 yyyyMMdd HH:mm
	 */
	public static String getSynchTimeString() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("MM月dd日 HH:mm");
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	/**
	 * 获取现在时间
	 * 
	 * @return 返回短时间字符串格式yyyy-MM-dd
	 */
	public static String getStringDateShort() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	/**
	 * 获取时间 小时:分;秒 HH:mm:ss
	 * 
	 * @return
	 */
	public static String getTimeShort() {
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		Date currentTime = new Date();
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	/**
	 * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
	 * 
	 * @param strDate
	 * @return
	 */
	public static Date strToDateLong(String strDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		ParsePosition pos = new ParsePosition(0);
		Date strtodate = formatter.parse(strDate, pos);
		return strtodate;
	}

	/** * 将长时间格式时间转换为字符串 yyyy-MM-dd HH:mm:ss * * @param dateDate * @return */
	public static String dateToStrLong(java.util.Date dateDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(dateDate);
		return dateString;
	}

	/**
	 * 将短时间格式时间转换为字符串 yyyy-MM-dd
	 * 
	 * @param dateDate
	 * @param k
	 * @return
	 */
	public static String dateToStr(java.util.Date dateDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(dateDate);
		return dateString;
	}

	/**
	 * 将短时间格式字符串转换为时间 yyyy-MM-dd
	 * 
	 * @param strDate
	 * @return
	 */
	public static Date strToDate(String strDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		ParsePosition pos = new ParsePosition(0);
		Date strtodate = formatter.parse(strDate, pos);
		return strtodate;
	}

	/**
	 * 得到现在时间
	 * 
	 * @return
	 */
	public static Date getNow() {
		Date currentTime = new Date();
		return currentTime;
	}

	/**
	 * 提取一个月中的最后一天
	 * 
	 * @param day
	 * @return
	 */
	public static Date getLastDate(long day) {
		Date date = new Date();
		long date_3_hm = date.getTime() - 3600000 * 34 * day;
		Date date_3_hm_date = new Date(date_3_hm);
		return date_3_hm_date;
	}

	/**
	 * 得到现在时间
	 * 
	 * @return 字符串 yyyyMMdd HHmmss
	 */
	public static String getStringToday() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd HHmmss");
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	/**
	 * 得到现在小时
	 */
	public static String getHour() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(currentTime);
		String hour;
		hour = dateString.substring(11, 13);
		return hour;
	}

	/**
	 * 得到现在分钟
	 * 
	 * @return
	 */
	public static String getTime() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(currentTime);
		String min;
		min = dateString.substring(14, 16);
		return min;
	}

	/**
	 * 根据用户传入的时间表示格式，返回当前时间的格式 如果是yyyyMMdd，注意字母y不能大写。
	 * 
	 * @param sformat
	 *            yyyyMMddhhmmss
	 * @return
	 */
	public static String getUserDate(String sformat) {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat(sformat);
		String dateString = formatter.format(currentTime);
		return dateString;
	}

	/**
	 * 二个小时时间间的差值,必须保证二个时间都是"HH:MM"的格式，返回字符型的分钟
	 */
	public static String getTwoHour(String st1, String st2) {
		String[] kk = null;
		String[] jj = null;
		kk = st1.split(":");
		jj = st2.split(":");
		if (Integer.parseInt(kk[0]) < Integer.parseInt(jj[0]))
			return "0";
		else {
			double y = Double.parseDouble(kk[0]) + Double.parseDouble(kk[1])
					/ 60;
			double u = Double.parseDouble(jj[0]) + Double.parseDouble(jj[1])
					/ 60;
			if ((y - u) > 0)
				return y - u + "";
			else
				return "0";
		}
	}

	/**
	 * 得到二个日期间的间隔天数
	 */
	public static String getTwoDay(String sj1, String sj2) {
		SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
		long day = 0;
		try {
			java.util.Date date = myFormatter.parse(sj1);
			java.util.Date mydate = myFormatter.parse(sj2);
			day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);
		} catch (Exception e) {
			return "";
		}
		return day + "";
	}

	/**
	 * 时间前推或后推分钟,其中JJ表示分钟.
	 */
	public static String getPreTime(String sj1, String jj) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String mydate1 = "";
		try {
			Date date1 = format.parse(sj1);
			long Time = (date1.getTime() / 1000) + Integer.parseInt(jj) * 60;
			date1.setTime(Time * 1000);
			mydate1 = format.format(date1);
		} catch (Exception e) {
		}
		return mydate1;
	}

	/**
	 * 得到一个时间延后或前移几天的时间,nowdate为时间,delay为前移或后延的天数
	 */
	public static String getNextDay(String nowdate, String delay) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			String mdate = "";
			Date d = strToDate(nowdate);
			long myTime = (d.getTime() / 1000) + Integer.parseInt(delay) * 24
					* 60 * 60;
			d.setTime(myTime * 1000);
			mdate = format.format(d);
			return mdate;
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 判断是否润年
	 * 
	 * @param ddate
	 * @return
	 */
	public static boolean isLeapYear(String ddate) {

		/**
		 * 详细设计： 1.被400整除是闰年，否则： 2.不能被4整除则不是闰年 3.能被4整除同时不能被100整除则是闰年
		 * 3.能被4整除同时能被100整除则不是闰年
		 */
		Date d = strToDate(ddate);
		GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
		gc.setTime(d);
		int year = gc.get(Calendar.YEAR);
		if ((year % 400) == 0)
			return true;
		else if ((year % 4) == 0) {
			return (year % 100) != 0;
		} else
			return false;
	}

	/**
	 * 返回美国时间格式 26 Apr 2006
	 * 
	 * @param str
	 * @return
	 */
	public static String getEDate(String str) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		ParsePosition pos = new ParsePosition(0);
		Date strtodate = formatter.parse(str, pos);
		String j = strtodate.toString();
		String[] k = j.split(" ");
		return k[2] + k[1].toUpperCase() + k[5].substring(2, 4);
	}

	/**
	 * 获取一个月的最后一天
	 * 
	 * @param dat
	 * @return
	 */
	public static String getEndDateOfMonth(String dat) {// yyyy-MM-dd
		String str = dat.substring(0, 8);
		String month = dat.substring(5, 7);
		int mon = Integer.parseInt(month);
		if (mon == 1 || mon == 3 || mon == 5 || mon == 7 || mon == 8
				|| mon == 10 || mon == 12) {
			str += "31";
		} else if (mon == 4 || mon == 6 || mon == 9 || mon == 11) {
			str += "30";
		} else {
			if (isLeapYear(dat)) {
				str += "29";
			} else {
				str += "28";
			}
		}
		return str;
	}

	/**
	 * 判断二个时间是否在同一个周
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean isSameWeekDates(Date date1, Date date2) {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(date1);
		cal2.setTime(date2);
		int subYear = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
		if (0 == subYear) {
			if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2
					.get(Calendar.WEEK_OF_YEAR))
				return true;
		} else if (1 == subYear && 11 == cal2.get(Calendar.MONTH)) {
			// 如果12月的最后一周横跨来年第一周的话则最后一周即算做来年的第一周
			if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2
					.get(Calendar.WEEK_OF_YEAR))
				return true;
		} else if (-1 == subYear && 11 == cal1.get(Calendar.MONTH)) {
			if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2
					.get(Calendar.WEEK_OF_YEAR))
				return true;
		}
		return false;
	}

	/**
	 * 产生周序列,即得到当前时间所在的年度是第几周
	 * 
	 * @return
	 */
	public static String getSeqWeek() {
		Calendar c = Calendar.getInstance(Locale.CHINA);
		String week = Integer.toString(c.get(Calendar.WEEK_OF_YEAR));
		if (week.length() == 1)
			week = "0" + week;
		String year = Integer.toString(c.get(Calendar.YEAR));
		return year + week;
	}

	// 将90秒转换为01:30
	public static String getFormatTimeWithSeconds(int seconds) {

		int m = seconds / 60;
		int s = seconds % 60;
		String result = String.format("%1$02d:%2$02d", m, s);
		return result;
	}

	public static String getFromatTimeWithMillisecond(long millsecond) {

		int seconds = (int) (millsecond / 1000);
		return getFormatTimeWithSeconds(seconds);
	}

	public static double getBetweenMins(String str1, String str2) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");// 输入日期的格式
		long l1 = 0, l2 = 0;

		try {
			l1 = sdf.parse(str1).getTime();
			l2 = sdf.parse(str2).getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return (l2 - l1) / (1000 * 60);

	}

	public static double getBetweenMins2(long l1, long l2) {

		return (l2 - l1) / (1000 * 60);
	}

	public static long FromatTimeToMinnisecond(String str1) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 输入日期的格式
		long l1 = 0;
		try {
			l1 = sdf.parse(str1).getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return l1;
	}

	public static Date FromatTimeToDate(String str1) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 输入日期的格式
		Date date = null;
		try {
			date = sdf.parse(str1);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return date;
	}
	public static String formatDateToShot(long time) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");// 输入日期的格式
		Date date = new Date(time);
		return sdf.format(date);
	}

	public static String getShortTimeShot(int seconds) {
		String shot = "00:00";
		int minute = seconds / 60;
		String leftSecond = seconds % 60 >= 10 ? (seconds % 60 + "")
				: ("0" + seconds % 60);
		if (minute > 0) {
			String leftMinute = minute % 60 >= 10 ? (minute % 60 + "")
					: ("0" + minute % 60);
			if (minute >= 60) {
				int hour = minute / 60;
				shot = hour + ":" + leftMinute + ":" + leftSecond;
			} else {
				shot = leftMinute + ":" + leftSecond;
			}
		} else {
			shot = "00:" + leftSecond;
		}
		return shot;
	}
	
	

	
	public static String completeDay(int month,int day){
		if(day <10){
			return MONTH[month]+" 0"+day;
		}
		return MONTH[month]+" "+day;
	}
	
	public static final String[] MONTH={"一月","二月","三月","四月","五月","六月","七月","八月","九月","十月","十一月","十二月"};
	
	public static boolean isSameMonth(long time1,long time2){
		
		 Calendar  cal1 = Calendar.getInstance();
		 Calendar cal2 = Calendar.getInstance();
		 cal1.setTimeInMillis(time1);
		 cal2.setTimeInMillis(time2);
		 int subYear = cal1.get(Calendar.YEAR)-cal2.get(Calendar.YEAR);
		 if(subYear == 0){
			 if(cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)){
				 return true;
			 }
	  }
	 return false;
	}
	 
	 public static boolean isSameOneDay(long time1,long time2){
			
		 Calendar  cal1 = Calendar.getInstance();
		 Calendar cal2 = Calendar.getInstance();
		 cal1.setTimeInMillis(time1);
		 cal2.setTimeInMillis(time2);
		 int subYear = cal1.get(Calendar.YEAR)-cal2.get(Calendar.YEAR);
		 if(subYear == 0){
			 if(cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)){
				 if(cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)){
					 return true;
				 }
			 }
	  }
	 return false;
	 }
	 
	 

}
