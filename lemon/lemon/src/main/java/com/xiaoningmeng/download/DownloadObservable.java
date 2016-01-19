package com.xiaoningmeng.download;

import android.database.Observable;

public class DownloadObservable<T> extends Observable<T> {

	public void registerObserver(T observer) {
		if (observer == null) {
			throw new IllegalArgumentException("The observer is null.");
		}
		synchronized (mObservers) {
			if (!mObservers.contains(observer)) {
				mObservers.add(observer);
			}
		}
	}

	public void unregisterObserver(T observer) {
		if (observer == null) {
			throw new IllegalArgumentException("The observer is null.");
		}
		synchronized (mObservers) {
			int index = mObservers.indexOf(observer);
			if (index != -1) {
				mObservers.remove(index);
			}
		}
	}
}
