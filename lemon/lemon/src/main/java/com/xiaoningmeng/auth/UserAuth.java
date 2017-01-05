package com.xiaoningmeng.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.analytics.MobclickAgent;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaoningmeng.LoginActivity;
import com.xiaoningmeng.R;
import com.xiaoningmeng.application.MyApplication;
import com.xiaoningmeng.bean.UserInfo;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.event.LoginEvent;
import com.xiaoningmeng.utils.AppUtils;
import com.xiaoningmeng.utils.DebugUtils;
import com.xiaoningmeng.utils.PreferenceUtil;

import org.litepal.crud.DataSupport;

import java.util.List;

import de.greenrobot.event.EventBus;
import okhttp3.Cookie;

public class UserAuth {

	private static UserAuth userAuth;
	private static final String UID = "ningmeng_uid";
	private static final String isFirst = "isFirst";

	private UserAuth() {

	}

	public static UserAuth getInstance() {
		if (userAuth == null) {
			synchronized (UserAuth.class) {
				if (userAuth == null) {
					userAuth = new UserAuth();
				}
			}
		}
		return userAuth;
	}


	public boolean isFirst(){
		boolean isfirst = PreferenceUtil.getBoolean(isFirst,true);
		if(isfirst){
			PreferenceUtil.putBoolean(isFirst,false);
		}
		return isfirst;

	}
	/**
	 * 验证用户是否登录
	 * 这里只对本地cookie的有效性做了检查,没有做Refresh Token的操作.QQ是否支持Refresh Token尚不确定.
	 * umeng,和QQ文档中目前并无说明
	 * 参考文档：
	 *	http://dev.umeng.com/social/android/login-page
	 *	http://wiki.connect.qq.com/android_sdk%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E
	 */
	public boolean isLogin(Context context) {

		boolean isLogin = false;
		SharedPrefsCookiePersistor sharedPrefsCookiePersistor = new SharedPrefsCookiePersistor(context);
		List<Cookie> cookieList = sharedPrefsCookiePersistor.loadAll();

		String cookieAl = Constant.COOKIE_AL;
		String cookieUi = Constant.COOKIE_UI;
		String cookieUs = Constant.COOKIE_US;

		boolean isCookieAlExpired = true;
		boolean isCookieUiExpired = true;
		boolean isCookieUsExpired = true;

		for (Cookie cookie : cookieList) {

			boolean isCookieExpired = AppUtils.isCookieExpired(cookie);
			DebugUtils.d("cookie.name = " + cookie.name() + ", cookie.value = " + cookie.value() + ", isCookieExpired = " + isCookieExpired);

			if (cookie.name().equals(cookieAl) && !isCookieExpired) {

				isCookieAlExpired = false;
			} else if (cookie.name().equals(cookieUi) && !isCookieExpired) {

				isCookieUiExpired = false;
			} else if (cookie.name().equals(cookieUs) && !isCookieExpired) {

				isCookieUsExpired = false;
			}
		}

		if (!isCookieAlExpired && !isCookieUiExpired && !isCookieUsExpired) {
			isLogin = true;
		}
		return isLogin;
	}

	/**
	 * 保存登录用户信息
	 *
	 * @param context
	 * @param userInfo
	 */
	public boolean saveLoginUserInfo(Context context, UserInfo userInfo) {

		boolean isSaved = false;
		PreferenceUtil.putString(UID, userInfo.getUid());
		DataSupport.deleteAll(UserInfo.class);
		isSaved = userInfo.save();
		if (isSaved) {
			MyApplication.getInstance().setLoginUserInfo(userInfo);
			MiPushClient.setUserAccount(context, userInfo.getUid(), null);
			EventBus.getDefault().post(new LoginEvent(userInfo));
			MobclickAgent.onEvent(context, "event_login");
			CrashReport.setUserId(userInfo.getUid());
		}
		return isSaved;
	}


	/**
	 * 清除登录用户信息
	 *
	 * @param context
	 */
	public void clearLoginUserInfo(Context context) {

		String uid = PreferenceUtil.getString(UID);
		MiPushClient.unsetUserAccount(context,uid, null);
		PreferenceUtil.removeString(UID);
		UserInfo.deleteAll(UserInfo.class);
		MyApplication.getInstance().setLoginUserInfo(null);
	}

	/**
	 * 获取登录用户信息
	 * @return
	 */
	public UserInfo getLoginUserInfo(Context context) {

		UserInfo userInfo = null;
		if (UserAuth.getInstance().isLogin(context)) {
			String uid = PreferenceUtil.getString(UID);
			if (uid != null) {
				List<UserInfo> userList = DataSupport.where("uid = ?",
						uid == null ? "" : uid).find(UserInfo.class);
				if (userList != null && userList.size() > 0) {
					userInfo = userList.get(0);
				} else {
//					DebugUtils.d("getLoginUserInfo -------> userInfo = null");
				}
			}
		}
		return userInfo;
	}

	public interface OnAuthFailListener {

		void onFail();
	}

	/**
	 * 重新启动应用
	 */
	private void restartApplication(Context context) {
		if (context != null) {
			final Intent intent = context.getPackageManager()
					.getLaunchIntentForPackage(context.getPackageName());
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			context.startActivity(intent);
		}
	}

	public static void startLoginActivity(Context context) {

		Intent intent = new Intent(context,
				LoginActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context
				.startActivity(intent);
		((Activity)context).overridePendingTransition(R.anim.main_translatey100to0,
				R.anim.main_translatey0tof100);
	}
}
