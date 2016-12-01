package com.xiaoningmeng.download;

import android.util.Log;

import com.xiaoningmeng.bean.AudioDownLoad;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectHandler;
import org.apache.http.protocol.HttpContext;

import java.io.File;
import java.net.URI;

public abstract class DownLoadRunnable<T extends AudioDownLoad> implements
		Runnable {

	public static final String TAG = "DownLoadRunnable";

	protected HttpGet get;
	protected long startPos, endPos;
	protected File file;
	protected HttpClient client;
	protected T bean;

	protected boolean isCancelled = false;
	protected boolean isFinished = false;
	protected boolean isPause = false;
	protected long curPosition;

	public DownLoadRunnable(T bean) {
		this.bean = bean;
		this.get = new HttpGet(bean.getUrl());
		get.setHeader("FROM", "mobile");
		//get.setHeader("User-Agent", AppUtils.getUAStr(MyApplication.getContext(),Constant.DOMAIN));
		this.endPos = bean.getEndPos();
		this.client = getClient();
		String temp = bean.getTempPath();
		this.file = new File(temp);

		if (this.file.exists()) {
			this.startPos = this.file.length();
			bean.setStartPos(startPos);
		} else {
			this.startPos = 0;
			bean.setStartPos(startPos);
		}
	}

	private HttpClient getClient() {
		DefaultHttpClient client = HttpFactory.createHttpClient(5, 60 * 1000);
		// Redirect
		client.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS,
				true);

		client.setRedirectHandler(new DefaultRedirectHandler() {

			@Override
			public URI getLocationURI(HttpResponse response, HttpContext context)
					throws org.apache.http.ProtocolException {
				URI uri = super.getLocationURI(response, context);
				Log.d(TAG, "redictory:" + uri.toString());
				return uri;
			}
		});
		return client;
	}

	protected long getContetnLengteh(String url, HttpClient client) {
		try {
			HttpHead req = new HttpHead(url);
			HttpResponse response = client.execute(req);
			StatusLine line = response.getStatusLine();
			if (line.getStatusCode() == 200) {
				Header[] headers = response.getAllHeaders();

				for (int i = 0; i < headers.length; i++) {
					Header header = headers[i];
					if (header.getName().toLowerCase().equals("content-length")) {
						long value = Long.valueOf(header.getValue());
						return value;
					}
				}

			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return 0;
	}

	public boolean isCancelled() {
		return isCancelled;
	}

	public boolean isDone() {
		return isCancelled() || isFinished;
	}

	public boolean cancel(boolean mayInterruptIfRunning) {
		isCancelled = true;
		if (mayInterruptIfRunning && get != null && !get.isAborted()) {
			get.abort();
		}
		return isCancelled();
	}

	public boolean pause() {
		isPause = true;
		if (get != null && !get.isAborted()) {
			get.abort();
		}
		return isFinished;
	}
}
