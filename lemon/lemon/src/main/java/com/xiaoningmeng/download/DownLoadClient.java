package com.xiaoningmeng.download;

public interface DownLoadClient<T> {

	public void download(T file);

	public void pause(T file);

	public void pauseAll();

	public void cancel(T file);

	public void cancelAll();

	public void downAll();

	public int getHistoryCount(String uid);

	public int getDownloadCount(String uid);

	public int getCount(String uid);

	public boolean isInited();

	public T getDownloadBeanByIndex(String url);

	public T getHistoryBeanByIndex(String url);

	public void init();

	public void clearAll();

}
