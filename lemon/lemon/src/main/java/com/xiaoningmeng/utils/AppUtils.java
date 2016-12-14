
package com.xiaoningmeng.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.alibaba.baichuan.android.trade.AlibcTrade;
import com.alibaba.baichuan.android.trade.callback.AlibcTradeCallback;
import com.alibaba.baichuan.android.trade.constants.AlibcConstants;
import com.alibaba.baichuan.android.trade.model.AlibcShowParams;
import com.alibaba.baichuan.android.trade.model.AlibcTaokeParams;
import com.alibaba.baichuan.android.trade.model.OpenType;
import com.alibaba.baichuan.android.trade.model.TradeResult;
import com.alibaba.baichuan.android.trade.page.AlibcBasePage;
import com.alibaba.baichuan.android.trade.page.AlibcPage;
import com.xiaoningmeng.WebViewActivity;
import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.base.BaseFragment;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.manager.DownloadApkManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class AppUtils {

    public static int getApiLevel() {

        int level = Integer.valueOf(android.os.Build.VERSION.SDK);
        return level;
    }

    public static void exitApp(Context context) {

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);
    }

    public static void openKeyboard(Activity activity) {

        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static void hiddenKeyboard(Activity activity) {

        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        try {
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

  /*  */

    /**
     * 获取应用的UserAgent
     *
     * @param context 应用的context
     * @param appname 应用的名称
     * @return 应用的ua字符串，context为空的时候返回""
     *//*
    public static String getUAStr(Context context, String appname) {
       
        PackageManager manager = null;
        if (context != null) {
        	manager= context.getPackageManager();
        }
        LinkedHashMap<String, String> map = AppUtils.getUAMap(context);
        String UAStr = "";
        try {
            PackageInfo info;
            String version;
            if (manager != null) {
                info = manager.getPackageInfo(context.getPackageName(), 0);
                version = info.versionName;
            } else {
                version = null;
            }
            UAStr += appname+"/" + version + "/adr (";
            for (Map.Entry<String, String> entry : map.entrySet()) {
                UAStr += entry.getValue() + ",";
            }
            UAStr = UAStr.substring(0, UAStr.length() - 1) + ")";

        } catch (NameNotFoundException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return UAStr;
    }

	public static LinkedHashMap<String, String> getUAMap(Context context){
		
		LinkedHashMap<String, String> map=new LinkedHashMap<String, String>();
		
		if(context==null){
			map.put("MOBILE_TYPE", null);
			map.put("IMEI", null);
			map.put("IMSI",null);
			map.put("SDK_VERSION", null);
			map.put("DM", null+"*"+null);
			map.put("INCHES", null);// 屏幕尺寸
			map.put("DENSITY", null);// 屏幕密度（每寸像素：120/160/240/320）
			map.put("NET_TYPE", null); // 网络类型
			map.put("CHANNAL", null); // 渠道名
			map.put("LANGUAGE", null); // 语言
		}else{
			TelephonyManager manager=(TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
			DisplayMetrics dm=new DisplayMetrics();
			WindowManager winManager=(WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
			winManager.getDefaultDisplay().getMetrics(dm);
			map.put("MOBILE_TYPE", android.os.Build.MODEL);
			String deviceid = manager.getDeviceId();
			if(deviceid==null  || deviceid.trim().equals("")){
				deviceid  = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
			}
			map.put("IMEI", deviceid);
			map.put("IMSI", manager.getSubscriberId());
			map.put("SDK_VERSION", android.os.Build.VERSION.RELEASE);
			map.put("DM", dm.widthPixels+"*"+dm.heightPixels);
			map.put("INCHES", getScreenInches(context)+"");// 屏幕尺寸
			map.put("DENSITY", dm.densityDpi+"");// 屏幕密度（每寸像素：120/160/240/320）
			map.put("NET_TYPE", NetUtils.getNetWorkType(context)); // 网络类型
			map.put("CHANNAL", getMetaDate(context, "UMENG_CHANNEL")); // 渠道名
			map.put("LANGUAGE", Locale.getDefault().getLanguage()); // 语言类型
		}
		return map;
	}
    */
    public static String getMetaDate(Context context, String data_name) {
        ApplicationInfo appInfo;
        String value = "";
        try {
            appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            value = appInfo.metaData.getString(data_name);
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return value;
    }

    public static double getScreenInches(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager winManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
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

    public static void showTaobaoPage(final Activity activity, String taokeUrl) {

        //实现参考SDK,demo
        AlibcShowParams alibcShowParams;//页面打开方式，默认，H5，Native
        alibcShowParams = new AlibcShowParams(OpenType.Auto, false);
        AlibcTaokeParams alibcTaokeParams = null;//淘客参数，包括pid，unionid，subPid
        alibcTaokeParams = new AlibcTaokeParams(Constant.DEFAULT_TAOKE_PID, "", "");
        Map<String, String> exParams = new HashMap<>(); //yhhpass参数
        exParams.put(AlibcConstants.ISV_CODE, "xiaoningmeng");
        AlibcBasePage alibcBasePage = new AlibcPage(taokeUrl);
        AlibcTrade.show(activity, alibcBasePage, alibcShowParams, alibcTaokeParams, exParams, new AlibcTradeCallback() {

            @Override
            public void onTradeSuccess(TradeResult tradeResult) {
                Toast.makeText(activity, "成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(activity, "失败 " + i + s,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    public static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    public static void openLinkUrl(BaseActivity activity, BaseFragment fragment, String linkUrl) {

        if ((null != activity || null != fragment) && !TextUtils.isEmpty(linkUrl)) {
            Context context = null;
            if (null != activity) {
                context = activity;
            } else if (null != fragment) {
                context = fragment.getActivity();
            }
            Uri linkUri = Uri.parse(linkUrl);
            String linkUriFilename = linkUri.getLastPathSegment();
            String linkUriHost = linkUri.getHost();
            if (linkUriHost.contains("taobao")) {
                if (null != activity) {
                    AppUtils.showTaobaoPage(activity, linkUrl);
                } else if (null != fragment) {
                    AppUtils.showTaobaoPage(fragment.getActivity(), linkUrl);
                }
            } else if (linkUriFilename.endsWith(".apk")) {
                DownloadApkManager.getInstance().showDownloadDialog(context, linkUrl);
            } else if (linkUri.getScheme().equals(Constant.APP_SCHEME)) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(linkUrl));
                if (null != activity) {
                    activity.startActivityForNew(intent);
                } else if (null != fragment) {
                    fragment.startActivityForNew(intent);
                }
            } else {
                WebViewActivity.openWebView(context, linkUrl);
            }
        }
    }
}