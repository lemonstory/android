package com.xiaoningmeng.download;

import com.xiaoningmeng.bean.AudioDownLoad;

public interface DownLoadObserver<T extends AudioDownLoad> {

	public void notifyData(T t);

	public void notifyCancel(T t);

}
