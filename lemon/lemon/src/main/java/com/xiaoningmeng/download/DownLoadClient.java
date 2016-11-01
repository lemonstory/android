package com.xiaoningmeng.download;

public interface DownLoadClient<T> {

	void download(T file);

	void pause(T file);

	void pauseAll();

	void cancel(T file);

	void cancelAll();

	void downAll();

	int getHistoryCount(String uid);

	int getDownloadCount(String uid);

	int getCount(String uid);

	boolean isInited();

	T getDownloadBeanByIndex(String url);

	T getHistoryBeanByIndex(String url);

	void init();

	void clearAll();

}
