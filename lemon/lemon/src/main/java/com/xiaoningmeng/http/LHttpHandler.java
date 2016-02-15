package com.xiaoningmeng.http;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.xiaoningmeng.application.MyApplication;
import com.xiaoningmeng.auth.UserAuth;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.utils.DebugUtils;

import org.apache.http.Header;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class LHttpHandler<T> implements Response.Listener<String>,
		Response.ErrorListener {

	private Context mContext;
	private ILoading mLoading;

	public LHttpHandler(Context context) {

		this.mContext = context;
	}

	public LHttpHandler(Context context, ILoading loading) {

		this.mContext = context;
		if(loading != null) {
			this.mLoading = loading;
			showLoading();
		}
	}

	/**
	 * 取到返回结果的数据后调用，一般是data字段的数据
	 *
	 * @param data
	 *            从返回结果中解析的data字段的值
	 */
	public abstract void onGetDataSuccess(T data);

	@Override
	public void onResponse(String responseString) {

		int code = -1;
		try {

			DebugUtils.e(responseString);

			CookieStore store = MyApplication.getInstance().mHttpClient
					.getCookieStore();
			List<Cookie> cookies =  store.getCookies();
			MyApplication.getInstance().setClientCookieFromHttpClient();
			Gson gson = new Gson();
			JSONObject jsonObject = new JSONObject(responseString);
			Type type = getType();
			T t;
			code = jsonObject.getInt("code");
			DebugUtils.e("code = " + code);
			if (Constant.REQ_SUCCESS_STATUS == code) {
				if (jsonObject.has("data")) {
					String content = jsonObject.getString("data");
					if (type == String.class || type == Object.class) {
						t = (T) content;
					} else {
						t = gson.fromJson(content, type);
					}
				} else {
					t = null;
				}
				onGetDataSuccess(t);
			} else {
				DebugUtils.e(responseString);
				onFailure(code, null, responseString, null);
			}
		} catch (Exception e) {
			onFailure(code, null, responseString, null);
			e.printStackTrace();
		}
		onFinish();
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		try {

			if (error.toString().equals("com.android.volley.TimeoutError")) {
				onFailure(0, null, "网络超时", null);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		onFinish();
	}

	public void onFailure(int statusCode, Header[] headers,
						  String responseString, Throwable throwable) {

		if (mContext != null) {
			if (responseString != null) {
				DebugUtils.e(responseString);
				String desc = responseString;
				try {
					JSONObject jsonObject = new JSONObject(responseString);
					desc = jsonObject.getString("desc");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				showServerMsg(statusCode, desc);
			}
		}
		onFinish();
	}

	/**
	 * Toast服务器返回的错误信息 可以重载 便于ＵＩ显示不同的信息样式
	 *
	 * @param statusCode
	 * @param msg
	 */
	public void showServerMsg(int statusCode, final String msg) {
		if(mContext  instanceof Activity){
			Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
		}
	}

	public void onFinish() {

		if (mLoading != null) {
			removeLoading();
		}
	}

	/**
	 * 获取当前的类型
	 *
	 * @return
	 */
	private Type getType() {
		Type type = String.class;
		Type mySuperClass = this.getClass().getGenericSuperclass();
		if (mySuperClass instanceof ParameterizedType)
			type = ((ParameterizedType) mySuperClass).getActualTypeArguments()[0];
		return type;
	}

	/**
	 * 显示加载进度
	 */
	private synchronized void showLoading() {
		if (mLoading == null) {
			return;
		}
		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				mLoading.startLoading();
			}
		});
	}

	/**
	 * 移除加载进度
	 */
	private synchronized void removeLoading() {

		new Handler(Looper.getMainLooper()).post(new Runnable() {
			@Override
			public void run() {
				mLoading.stopLoading();
			}
		});

	}

}
