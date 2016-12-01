package com.xiaoningmeng.bean;

import com.xiaoningmeng.utils.PreferenceUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WeekDay implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int day;
	private String dayStr;
	private boolean isCheck;
	
	
	public WeekDay(int day, String dayStr, boolean isCheck) {
		this.day = day;
		this.dayStr = dayStr;
		this.isCheck = isCheck;
	}
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public String getDayStr() {
		return dayStr;
	}
	public void setDayStr(String dayStr) {
		this.dayStr = dayStr;
	}
	public boolean isCheck() {
		return isCheck;
	}
	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}
	
	public static List<WeekDay> getWeek(){
		List<WeekDay> weekDays = new ArrayList<>();
		weekDays.add(new WeekDay(0,"周一",PreferenceUtil.getBoolean("week0", true)));
		weekDays.add(new WeekDay(1,"周二",PreferenceUtil.getBoolean("week1", true)));
		weekDays.add(new WeekDay(2,"周三",PreferenceUtil.getBoolean("week2", true)));
		weekDays.add(new WeekDay(3,"周四",PreferenceUtil.getBoolean("week3", true)));
		weekDays.add(new WeekDay(4,"周五",PreferenceUtil.getBoolean("week4", true)));
		weekDays.add(new WeekDay(5,"周六",PreferenceUtil.getBoolean("week5", true)));
		weekDays.add(new WeekDay(6,"周末",PreferenceUtil.getBoolean("week6", true)));
		return weekDays;
		
	}
	
	public static boolean isCheck(int pos){
		return PreferenceUtil.getBoolean("week"+pos, false);
	}
	
	public static void saveWeek(List<WeekDay> weekDays){
		PreferenceUtil.putBoolean("week0", weekDays.get(0).isCheck);
		PreferenceUtil.putBoolean("week1", weekDays.get(1).isCheck);
		PreferenceUtil.putBoolean("week2", weekDays.get(2).isCheck);
		PreferenceUtil.putBoolean("week3", weekDays.get(3).isCheck);
		PreferenceUtil.putBoolean("week4", weekDays.get(4).isCheck);
		PreferenceUtil.putBoolean("week5", weekDays.get(5).isCheck);
		PreferenceUtil.putBoolean("week6", weekDays.get(6).isCheck);
	}
}
