package com.xiaoningmeng.player;

import java.util.ArrayList;
import java.util.List;

import com.xiaoningmeng.bean.PlayingStory;

public abstract class PlayerObservable {

	/**
	 * 用来保存注册的观察者对象
	 */
	private List<PlayObserver> list = new ArrayList<PlayObserver>();

	/**
	 * 注册观察者对象
	 * 
	 * @param observer
	 *            观察者对象
	 */
	public void register(PlayObserver observer) {
		if (observer != null) {
			list.add(observer);
		}
	}

	/**
	 * 删除观察者对象
	 * 
	 * @param observer
	 *            观察者对象
	 */
	public synchronized void unRegister(PlayObserver observer) {
		if (observer != null) {
			list.remove(observer);
		}
	}

	/**
	 * 通知所有注册的观察者对象
	 */
	public void notifyDataChanged(PlayingStory music) {

		for (PlayObserver observer : list) {
			if (observer != null)
				observer.notify(music);
		}
	}


}
