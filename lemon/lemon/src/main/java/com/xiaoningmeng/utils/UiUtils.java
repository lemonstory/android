/**
 * @author  chenxiaodong
 * @Email   chenxiaodong@gexing.com
 * @version 创建时间： 2013-4-3 下午3:35:14
 * @Introduction
 */
package com.xiaoningmeng.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class UiUtils {

	private static DisplayMetrics mDisplayMetrics;
	private static UiUtils utils = null;

	private UiUtils() {
	}

	public static UiUtils getInstance(Context mContext) {
		if (utils == null)
			utils = new UiUtils();
		if (mContext != null) {
			mDisplayMetrics = new DisplayMetrics();
			WindowManager mWindowManager = ((WindowManager) mContext
					.getSystemService(Context.WINDOW_SERVICE));
			mWindowManager.getDefaultDisplay().getMetrics(mDisplayMetrics);
		}
		return utils;
	}

	/**
	 * 
	 * 获取屏幕密度 // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
	 */
	public float getDensity() {
		return mDisplayMetrics.density;
	}

	/**
	 * 
	 * 获取屏幕密度 // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
	 */
	public float getDensityDpi() {
		return mDisplayMetrics.densityDpi;
	}

	/**
	 * 
	 * 获取屏幕宽度
	 */
	public int getmScreenWidth() {
		return mDisplayMetrics.widthPixels;
	}

	/**
	 * 
	 * 获取屏幕高度
	 */
	public int getmScreenHeight() {
		return mDisplayMetrics.heightPixels;
	}

	/**
	 * dip变像素
	 * 
	 * @param dpValue
	 * @return
	 */
	public int DipToPixels(float dpValue) {
		final float scale = getDensity();
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 像素变dip
	 * 
	 * @param pxValue
	 * @return
	 */
	public int PixelsToDip(float pxValue) {
		final float scale = getDensity();
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 sp
	 */

	public static int px2sp(float pxValue, Context context) {

		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);

	}

	public static int dp2px(Context context, int dp) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();

		DisplayMetrics displaymetrics = new DisplayMetrics();
		display.getMetrics(displaymetrics);

		return (int) (dp * displaymetrics.density + 0.5f);
	}

	public static int px2dp(Context context, int px) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();

		DisplayMetrics displaymetrics = new DisplayMetrics();
		display.getMetrics(displaymetrics);

		return (int) (px / displaymetrics.density + 0.5f);
	}

	/**
	
	
	     */

	public static int sp2px(float spValue, Context context) {

		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;

		return (int) (spValue * fontScale + 0.5f);

	}

	public static double getInches(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager winManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		winManager.getDefaultDisplay().getMetrics(dm);

		int width = dm.widthPixels;
		int height = dm.heightPixels;
		int dens = dm.densityDpi;
		double wi = (double) width / (double) dens;
		double hi = (double) height / (double) dens;
		double x = Math.pow(wi, 2);
		double y = Math.pow(hi, 2);
		double screenInches = Math.sqrt(x + y);

		return screenInches;
	}

	/**
	 * 获取屏幕
	 */
	public static double getScreen(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		int width = dm.widthPixels;
		int height = dm.heightPixels;
		int dens = dm.densityDpi;
		double wi = (double) width / (double) dens;
		double hi = (double) height / (double) dens;
		double x = Math.pow(wi, 2);
		double y = Math.pow(hi, 2);
		return Math.sqrt(x + y);
	}

}
