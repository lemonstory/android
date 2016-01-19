package com.xiaoningmeng.application;

import java.util.Stack;

import android.app.Activity;

public class ActivityManager {
	private static Stack<Activity> activityStack;
	private static ActivityManager instance;

	private ActivityManager() {
	}

	public static ActivityManager getScreenManager() {
		if (instance == null) {
			instance = new ActivityManager();
		}
		return instance;
	}

	/**
	 * 关闭指定的acitivity;
	 * 
	 * @param activity
	 */
	public void popActivity(Activity activity) {
		if (activity != null) {
			// 在从自定义集合中取出当前Activity时，也进行了Activity的关闭操作
			activity.finish();
			activityStack.remove(activity);
		}
	}

	/**
	 * 获得当前栈顶Activity
	 * 
	 * @return
	 */
	public Activity currentActivity() {
		Activity activity = null;
		if (activityStack != null && !activityStack.empty())
			activity = activityStack.peek();
		return activity;
	}

	/**
	 * 将activity加入栈中
	 * 
	 * @param activity
	 */
	public void pushActivity(Activity activity) {
		if (activityStack == null) {
			activityStack = new Stack<Activity>();
		}
		activityStack.push(activity);
	}

	/**
	 * 退出栈中除了class为cls 以外所有的activity
	 * 
	 * @param <T>
	 * 
	 * @param cls
	 */
	public <T extends Activity> void popAllActivityExceptOne(Class<T> cls) {
		while (activityStack.size() > 1) {
			Activity activity = currentActivity();
			if (activity == null) {
				break;
			}
			if (activity.getClass().equals(cls)) {
				break;
			}
			popActivity(activity);
		}
	}

	/**
	 * 退出栈中所有的activity
	 */
	public void popAllActivity() {
		while (!activityStack.isEmpty()) {
			Activity activity = activityStack.pop();
			if (activity == null) {
				break;
			}
			activity.finish();
		}
	}

	public Activity getActivity(String name) {
		for (Activity activity : activityStack) {
			if (activity.getClass().getName().equals(name)) {
				return activity;
			}
		}
		return null;
	}

	public <T extends Activity> Activity getActivity(Class<T> cls) {
		for (Activity activity : activityStack) {
			if (activity.getClass().equals(cls)) {
				return activity;
			}
		}
		return null;
	}

	public Stack<Activity> getStack() {
		return activityStack;
	}

	public int getActivitiesSize() {
		return activityStack.size();
	}
}
