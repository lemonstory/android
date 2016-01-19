package com.xiaoningmeng.bean;

import java.util.Locale;

import com.xiaoningmeng.application.MyApplication;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.utils.AppUtils;
import com.xiaoningmeng.utils.DebugUtils;
import com.xiaoningmeng.utils.NetUtils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class AppInfo {
	
	private String UA;
	private String MOBILE_TYPE;
	private String IMEI;
	private String IMSI;
	private String SDK_VERSION;
	private String DM;
	private String INCHES;
	private String DENSITY;
	private String NET_TYPE;
	private String CHANNAL;
	private String LANGUAGE;
	private String UAStr;
	
	private AppInfo(){
		init(MyApplication.getContext());
	}
	private static AppInfo mInstance = new AppInfo();
	
	public static AppInfo getInstance(){
		return mInstance;
	}
	
	public void init(Context context){
		  PackageManager manager = null;
	        if (context != null) {
	        	manager= context.getPackageManager();
	        }
	        try {
	            PackageInfo info;
	            String version;
	            if (manager != null) {
	                info = manager.getPackageInfo(context.getPackageName(), 0);
	                version = info.versionName;
	            } else {
	                version = null;
	            }
	            UA = Constant.DOMAIN+"/" + version + "/adr ";
	            initOtherParams(context);
	        } catch (NameNotFoundException e) {
	            e.printStackTrace();
	        } catch (NullPointerException e) {
	            e.printStackTrace();
	        }
	}
	
	


	public String getIMEI() {
		return IMEI;
	}

	public String getSDK_VERSION() {
		return SDK_VERSION;
	}


	private void initOtherParams(Context context) {
		
		TelephonyManager manager=(TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		DisplayMetrics dm=new DisplayMetrics();
		WindowManager winManager=(WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		winManager.getDefaultDisplay().getMetrics(dm);
		MOBILE_TYPE = android.os.Build.MODEL;
		String deviceid = manager.getDeviceId();
		if(deviceid==null  || deviceid.trim().equals("")){
			deviceid  = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
		}
		IMEI= deviceid;
		IMSI= manager.getSubscriberId();
		SDK_VERSION= android.os.Build.VERSION.RELEASE;
		DM= dm.widthPixels+"*"+dm.heightPixels;
		INCHES= AppUtils.getScreenInches(context)+"";// 屏幕尺寸
		DENSITY= dm.densityDpi+"";// 屏幕密度（每寸像素：120/160/240/320）
		NET_TYPE = NetUtils.getNetWorkType(context); // 网络类型
		CHANNAL= AppUtils.getMetaDate(context, "UMENG_CHANNEL"); // 渠道名
		LANGUAGE= Locale.getDefault().getLanguage(); // 语言类型
		
	}
	
	public String getUAStr(){
		if(UAStr == null){
			StringBuffer buffer = new StringBuffer();
			buffer.append(UA);
			buffer.append("(");
			buffer.append(MOBILE_TYPE);
			buffer.append(",");
			buffer.append(IMEI);
			buffer.append(",");
			buffer.append(IMSI);
			buffer.append(",");
			buffer.append(SDK_VERSION);
			buffer.append(",");
			buffer.append(DM);
			buffer.append(",");
			buffer.append(INCHES);
			buffer.append(",");
			buffer.append(DENSITY);
			buffer.append(",");
			buffer.append(NET_TYPE);
			buffer.append(",");
			buffer.append(CHANNAL);
			buffer.append(",");
			buffer.append(LANGUAGE);
			buffer.append(")");
			UAStr = buffer.toString();
			DebugUtils.e(UAStr);
		}
		return UAStr;
	}
}
