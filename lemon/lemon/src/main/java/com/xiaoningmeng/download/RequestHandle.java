package com.xiaoningmeng.download;

import java.lang.ref.WeakReference;

public class RequestHandle {
	private final WeakReference<DownLoadRunnable<?>> request;

	public RequestHandle(DownLoadRunnable<?> request) {
		this.request = new WeakReference<DownLoadRunnable<?>>(request);
	}

	public boolean cancel(boolean mayInterruptIfRunning) {
		DownLoadRunnable<?> _request = request.get();
		return _request == null || _request.cancel(mayInterruptIfRunning);
	}

	public boolean pause() {
		DownLoadRunnable<?> _request = request.get();
		return _request == null || _request.pause();
	}

	public boolean isFinished() {
		DownLoadRunnable<?> _request = request.get();
		return _request == null || _request.isDone();
	}

	public boolean isCancelled() {
		DownLoadRunnable<?> _request = request.get();
		return _request == null || _request.isCancelled();
	}

	public boolean shouldBeGarbageCollected() {
		boolean should = isCancelled() || isFinished();
		if (should)
			request.clear();
		return should;
	}
}