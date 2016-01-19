package com.xiaoningmeng.auth;

import java.util.Date;
import java.util.List;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.json.JSONException;
import org.litepal.crud.DataSupport;

import com.orhanobut.dialogplus.DialogPlus;
import com.xiaoningmeng.LoginActivity;
import com.xiaoningmeng.R;
import com.xiaoningmeng.application.ActivityManager;
import com.xiaoningmeng.application.MyApplication;
import com.xiaoningmeng.bean.UserInfo;
import com.xiaoningmeng.event.LoginEvent;
import com.xiaoningmeng.utils.PreferenceUtil;
import com.xiaoningmeng.view.dialog.TipDialog;
import com.ypy.eventbus.EventBus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

public class UserAuth {

	private static UserAuth userAuth;
	private static final String UID = "ningmeng_uid";
	private static final String TOKEN = "ningmeng_token";
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
	 * 验证用户信息
	 */
	public boolean authUser(Context context) {

		String uid = PreferenceUtil.getString(UID);
		String token = PreferenceUtil.getString(TOKEN);
		if (uid != null && token != null) {
			List<UserInfo> userList = DataSupport.where("uid = ?",
					uid == null ? "" : uid).find(UserInfo.class);
			if (userList != null && userList.size() > 0) {
				UserInfo userInfo = userList.get(0);
				return checkAuthTokenValid(context, userInfo, token);
			}

		}
		return false;
	}

	/**
	 * 登录
	 *
	 * @param context
	 * @param userInfo
	 */
	public void login(Context context, UserInfo userInfo) {

		CookieStore store = MyApplication.getInstance().mHttpClient
				.getCookieStore();
		String authToken = CookieParser.parseCookies2Json(store);
		PreferenceUtil.putString(TOKEN, authToken);
		PreferenceUtil.putString(UID, userInfo.getUid());
		DataSupport.deleteAll(UserInfo.class);
		userInfo.save();
		loadUserInfo(userInfo, store);
		EventBus.getDefault().post(new LoginEvent(userInfo));
	}



	/**
	 * 强制用户身份失效
	 *
	 * @param context
	 */
	public void invinvalidateUserIdentity(Context context) {

		PreferenceUtil.removeString(UID);
		PreferenceUtil.removeString(TOKEN);
		UserInfo.deleteAll(UserInfo.class);
		MyApplication.getInstance().removeClientCookieFromHttpClient();
		MyApplication.getInstance().setIsLogin(false);
		MyApplication.getInstance().setUserInfo(null);
		ActivityManager.getScreenManager().popAllActivity();
		// restartApplication(MyApplication.getInstance().getApplicationContext());

	}

	/**
	 * 检查cookie
	 *
	 * @param context
	 * @param authToken
	 * @return
	 */
	private boolean checkAuthTokenValid(Context context,
										final UserInfo userinfo, String authToken) {

		try {
			CookieStore store = CookieParser.pareseJson2Cookies(authToken);
			if (store != null) {
				/*
				 * //判断是否有网 没有网不检查 if(!NetworkUtils.hasNetWork(context)){ return
				 * true; }
				 */
				List<Cookie> cookies = store.getCookies();
				// 检查cookie的有效时间
				for (Cookie cookie : cookies) {
					/*
					 * if(cookie.getDomain().contains("223.6.253.158")){ return
					 * false; }
					 */
					if ("al".equals(cookie.getName())) {
						if (!cookie.isExpired(new Date())) {
							loadUserInfo(userinfo, store);
							return true;
						}
						return false;
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 加载用户信息
	 *
	 * @param userInfo
	 */
	private void loadUserInfo(UserInfo userInfo, CookieStore cookieStore) {

		MyApplication.getInstance().setUserInfo(userInfo);

		if (cookieStore == null) {
			MyApplication.getInstance().setClientCookieFromHttpClient();

		} else {
			MyApplication.getInstance().setCookieFromToken(cookieStore);

		}
		MyApplication.getInstance().setIsLogin(true);

	}

	// 审核用户权限
	public static boolean auditUser(final Context context,String tip) {

		if (!MyApplication.getInstance().isIsLogin()
				|| MyApplication.getInstance().userInfo == null) {

			/*new TipDialog.Builder(context)
					.setHasBtn(true)
					.setTipText(tip == null ?"您还未登录，请登录" : tip)
					.setTransparent(false)
					.setOnClickListener(
							new com.orhanobut.dialogplus.OnClickListener() {

								@Override
								public void onClick(DialogPlus dialog, View view) {
									switch (view.getId()) {
									case R.id.tv_dialog_enter:*/
			Intent intent = new Intent(context,
					LoginActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			((Activity) context)
					.startActivity(intent);
			((Activity)context).overridePendingTransition(R.anim.main_translatey100to0,
					R.anim.main_translatey0tof100);
										/*dialog.dismiss();
										break;
									case R.id.tv_dialog_cancel:
										dialog.dismiss();
										break;
									}
								}
							}).create().show();*/
			return false;
		}
		return true;
	}

	public interface OnAuthFailListener {

		public void onFail();
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
}
