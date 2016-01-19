package com.xiaoningmeng.download;

import java.lang.reflect.Constructor;

import com.xiaoningmeng.bean.AudioDownLoad;

import android.util.SparseArray;

@SuppressWarnings("rawtypes")
public class DownloaderFactory {
	private SparseArray<Class<? extends DownLoadRunnable>> downloadClazz;
	private static Class<? extends DownLoadRunnable> DEFAULT = null;

	private static DownloaderFactory INSTANCE = null;
	public static final int First_Version = 6;

	private DownloaderFactory() {
		downloadClazz = new SparseArray<Class<? extends DownLoadRunnable>>();
		downloadClazz.put(First_Version, DownLoadRunnableImpl.class);

		/**
		 * downloadClazz.put(N, XXX.class);
		 * DownloaderFactory.getInstance().getDownloader(N);
		 * 
		 */

		DEFAULT = DownLoadRunnableImpl.class;

		for (int i = 0; i < downloadClazz.size(); i++) {
			Class<? extends DownLoadRunnable> clazz = downloadClazz
					.get(downloadClazz.keyAt(i));
			try {
				Class.forName(clazz.getName());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	public synchronized static DownloaderFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new DownloaderFactory();
		}
		return INSTANCE;
	}

	/**
	 * 根据版本获取下载器
	 * 
	 * @param <T>
	 * @param version
	 * @param bean
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends AudioDownLoad> DownLoadRunnable<T> getDownloader(
			int version, T bean) {
		try {
			DownLoadRunnable<T> client;
			Class<? extends DownLoadRunnable> clazz = downloadClazz
					.get(version);
			if (clazz == null) {
				clazz = DEFAULT;
			}
			Constructor[] constructors = clazz.getConstructors();
			client = (DownLoadRunnable<T>) constructors[0].newInstance(bean);
			return client;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
