package com.xiaoningmeng.auth;

import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CookieParser {

	/**
	 * 将cookies转为json
	 * 
	 * @param cookieStore
	 * @return
	 */
	public static synchronized String parseCookies2Json(CookieStore cookieStore) {

		if (cookieStore != null) {
			StringBuffer sb = new StringBuffer();
			sb.append("[");

			/*
			 * CopyOnWriteArrayList则是ArrayList的一个"线程安全"的变体。
			 * 对于任何修改操作，该集合类会在内部将其内容复制到一个新数组中，
			 * 所以当读用户访问数组的内容时不会招致任何同步开销(因为它们没有对可变数据进行操作)。
			 */
			CopyOnWriteArrayList<Cookie> cookies = new CopyOnWriteArrayList<Cookie>();
			cookies.addAll(cookieStore.getCookies());
			for (int i = 0; i < cookies.size(); i++) {
				Cookie cookie = cookies.get(i);
				sb.append("{");
				appendIntegerJson(sb, "version", cookie.getVersion());
				appendStringJson(sb, "name", cookie.getName());
				appendStringJson(sb, "value", cookie.getValue());
				appendStringJson(sb, "path", cookie.getPath());
				appendStringJson(sb, "domain", cookie.getDomain());
				appendLongJson(sb, "expiry",
						(cookie.getExpiryDate() == null) ? 0 : cookie
								.getExpiryDate().getTime());
				sb = sb.deleteCharAt(sb.length() - 1);
				sb.append("}");
				sb.append(",");
			}
			sb = sb.deleteCharAt(sb.length() - 1);
			sb.append("]");
			return sb.toString();
		}
		return null;
	}

	/**
	 * 将json转为cookies
	 * 
	 * @param cookieJson
	 * @return
	 * @throws JSONException
	 */
	public static CookieStore pareseJson2Cookies(String cookieJson)
			throws JSONException {
		BasicCookieStore cookieStore = new BasicCookieStore();
		if (cookieJson != null) {
			JSONArray array = new JSONArray(cookieJson);
			for (int i = 0; i < array.length(); i++) {
				JSONObject jsonObject = array.getJSONObject(i);
				String name = jsonObject.getString("name");
				String value = jsonObject.getString("value");
				int version = jsonObject.getInt("version");
				String path = jsonObject.getString("path");
				String domain = jsonObject.getString("domain");
				long expiry = jsonObject.getLong("expiry");
				BasicClientCookie cookie = new BasicClientCookie(name, value);
				cookie.setExpiryDate(expiry == 0 ? null : new Date(expiry));
				cookie.setDomain(domain);
				cookie.setPath(path);
				cookie.setVersion(version);
				cookieStore.addCookie(cookie);
			}
			return cookieStore;
		}
		return null;
	}

	/*
	 * 拼接String json
	 */
	public static void appendStringJson(StringBuffer sb, String key,
			String value) {
		sb.append("\"").append(key).append("\":");
		sb.append("\"").append(value).append("\"");
		sb.append(",");
	}

	/*
	 * 拼接Integer json
	 */
	public static void appendIntegerJson(StringBuffer sb, String key, int value) {
		sb.append("\"").append(key).append("\":");
		sb.append(value);
		sb.append(",");
	}

	/**
	 * 拼接Long json
	 * 
	 * @param sb
	 * @param key
	 * @param value
	 */
	public static void appendLongJson(StringBuffer sb, String key, long value) {
		sb.append("\"").append(key).append("\":");
		sb.append(value);
		sb.append(",");
	}
}
