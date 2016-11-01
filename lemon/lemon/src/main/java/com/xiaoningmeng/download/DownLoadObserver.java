package com.xiaoningmeng.download;

import com.xiaoningmeng.bean.AudioDownLoad;

public interface DownLoadObserver<T extends AudioDownLoad> {

	void notifyData(T t);

	void notifyCancel(T t);

}
