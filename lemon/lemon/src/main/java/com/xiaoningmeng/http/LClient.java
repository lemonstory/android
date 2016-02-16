package com.xiaoningmeng.http;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache.Entry;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.xiaoningmeng.R;
import com.xiaoningmeng.application.MyApplication;
import com.xiaoningmeng.bean.AppInfo;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.utils.DebugUtils;
import com.xiaoningmeng.utils.NetUtils;

import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LClient {

	private static LClient mInstance;
	public RequestQueue mRequestQueue;
	private DefaultRetryPolicy retryPolicy;

	private LClient() {
		mRequestQueue = MyApplication.getInstance().getmRequestQueue();
		retryPolicy = new DefaultRetryPolicy(Constant.DEFAULT_TIMEOUT_MS,
				Constant.DEFAULT_MAX_RETRIES, Constant.DEFAULT_BACKOFF_MULT);
	}

	/**
	 * 获取client实例
	 * 
	 * @return client实例
	 */
	public static LClient getInstance() {
		if (mInstance == null) {
			mInstance = new LClient();
		}
		return mInstance;
	}

	public void getCache(Context context, String url,
			HashMap<String, String> params, LHttpHandler<?> handler) {

		String URL = getUrlWithQueryString(true, url, params);
		DebugUtils.e(URL);
		Entry cacheEntry = mRequestQueue.getCache().get(URL);

		if (cacheEntry != null) {
			String cachedResponse = new String(mRequestQueue.getCache()
					.get(URL).data);
			handler.onResponse(cachedResponse);
		} else {
			handler.onFinish();
		}
	}

	/**
	 * get请求
	 * 
	 * @param url
	 *            请求地址
	 * @param params
	 *            请求参数
	 * @param handler
	 *            响应handler
	 */
	public void get(final Context context, String url,
			HashMap<String, String> params, LHttpHandler<?> handler) {

		String URL = getUrlWithQueryString(true, url, params);
		DebugUtils.e(URL);
		Entry cacheEntry = mRequestQueue.getCache().get(URL);

		if (!NetUtils.isNetworkConnected(context)) {

			if (cacheEntry != null) {
				String cachedResponse = new String(mRequestQueue.getCache()
						.get(URL).data);
				handler.onResponse(cachedResponse);
			}else{
				handler.onFailure(0, null,context.getString(R.string.net_connect_error), null);
			}
		} else if (cacheEntry != null && !cacheEntry.isExpired()) {

			String cachedResponse = new String(mRequestQueue.getCache()
					.get(URL).data);
			handler.onResponse(cachedResponse);

		} else if (NetUtils.isNetworkConnected(context)) {
			StringRequest request = new StringRequest(URL, handler, handler) {

				@Override
				public Map<String, String> getHeaders() throws AuthFailureError {
					HashMap<String, String> headers = new HashMap<String, String>();
					headers.put("FROM", "mobile");
					headers.put("User-Agent", AppInfo.getInstance().getUAStr());
					return headers;
				}
			};
			request.setRetryPolicy(retryPolicy);
			request.setTag(context);
			mRequestQueue.add(request);
		} else {
			handler.onFailure(0, null,
					context.getString(R.string.net_connect_error), null);
		}
	}




	/**
	 * get请求
	 * 
	 * @param url
	 *            请求地址
	 * @param params
	 *            请求参数
	 * @param handler
	 *            响应handler
	 */
	public void get(final Context context, String url,
			HashMap<String, String> params, LHttpHandler<?> handler,
			boolean isrefresh) {

		if (!isrefresh) {
			get(context, url, params, handler);
		} else {
			String URL = getUrlWithQueryString(true, url, params);
			DebugUtils.e(URL);

			Entry cacheEntry = mRequestQueue.getCache().get(URL);

			if (!NetUtils.isNetworkConnected(context)) {

				if (cacheEntry != null) {
					String cachedResponse = new String(mRequestQueue.getCache()
							.get(URL).data);
					handler.onResponse(cachedResponse);
				} else {
					handler.onFailure(0, null,
							context.getString(R.string.net_connect_error), null);
				}
			} else if (NetUtils.isNetworkConnected(context)) {

				mRequestQueue.getCache().remove(URL);
				StringRequest request = new StringRequest(URL, handler, handler) {

					@Override
					public Map<String, String> getHeaders()
							throws AuthFailureError {
						HashMap<String, String> headers = new HashMap<String, String>();
						headers.put("FROM", "mobile");
						headers.put("User-Agent",AppInfo.getInstance().getUAStr());
						return headers;
					}
				};
				request.setRetryPolicy(retryPolicy);
				request.setTag(context);
				mRequestQueue.add(request);
			} else {
				handler.onFailure(0, null,
						context.getString(R.string.net_connect_error), null);
			}
		}

	}

	/**
	 * post请求
	 * 
	 * @param url
	 *            请求地址
	 * @param params
	 *            请求参数
	 * @param handler
	 *            响应handler
	 */
	// Uri.encode(comment)
	public void post(final Context context, String url,
			final HashMap<String, String> params, LHttpHandler<?> handler) {

		//String URL = getUrlWithQueryString(true, url, params);
		//DebugUtils.e(URL);
		DebugUtils.e(url);

		StringRequest request = new StringRequest(Method.POST, url, handler,
				handler) {

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				return params;
			}

			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				HashMap<String, String> headers = new HashMap<String, String>();

				headers.put("FROM", "mobile");
				headers.put("User-Agent",AppInfo.getInstance().getUAStr());
				headers.put("Content-Type", "application/x-www-form-urlencoded");
				return headers;
			}
		};
		request.setRetryPolicy(retryPolicy);
		request.setTag(context);
		mRequestQueue.add(request);

	}


	public String getUrlWithQueryString(boolean shouldEncodeUrl, String url,
			HashMap<String, String> params) {
		if (shouldEncodeUrl)
			url = url.replace(" ", "%20");
		if (params != null) {
			String paramString = getParamsList(params).trim();
			if (!paramString.equals("") && !paramString.equals("?")) {
				url += url.contains("?") ? "&" : "?";
				url += paramString;
			}
		}

		return url;
	}

	public String getParamsList(HashMap<String, String> params) {
		List<BasicNameValuePair> lparams = new LinkedList<BasicNameValuePair>();
		for (ConcurrentHashMap.Entry<String, String> entry : params.entrySet()) {
			lparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}

		return URLEncodedUtils.format(lparams, HTTP.UTF_8);
	}

	public boolean checkCacheIsExpired(String url,
			HashMap<String, String> params) {

		Boolean isExpired = false;
		String URL = getUrlWithQueryString(true, url, params);
		Entry cacheEntry = mRequestQueue.getCache().get(URL);

		if (null != cacheEntry) {
			isExpired = cacheEntry.isExpired();
		}

		return isExpired;
	}

	/**
	 * 清除所有的http Cache
	 */
	public void clearCache() {

		mRequestQueue.getCache().clear();
	}

}
