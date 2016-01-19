package com.xiaoningmeng.download;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import android.util.Log;

/**
 * 线程池相关，默认维护一个单例线程池，可创建新的线程池
 * 
 */
public class ThreadPoolFactory extends ThreadPoolExecutor {

	private static final String TAG = "ThreadPoolFactory";
	private static final int POOL_SIZE = 2;

	/** corePoolSize + poolSize = max thread */
	private static final int poolSize = 3;

	/** threads to keep in the pool */
	private static final int corePoolSize = Runtime.getRuntime()
			.availableProcessors() * POOL_SIZE + poolSize;

	/** 当线程池中数量大于常驻线程时，超过waitTime 秒池中线程开始销毁至线程池中corePoolSize数量 */
	private static final int waitTime = 10;

	/** 缓冲任务队列数 */
	private static final int queueSize = 10;

	private ReentrantLock pauseLock = new ReentrantLock();
	private Condition unpaused = pauseLock.newCondition();
	private boolean isPaused = false;
	private boolean isStarted = false;
	private static ThreadPoolFactory instance = null;

	private ThreadPoolFactory(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory,
			RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
				threadFactory, handler);
	}

	public synchronized static ThreadPoolFactory defaultInstance() {
		if (instance == null) {
			instance = createInstance();
		}
		return instance;
	}

	public synchronized static ThreadPoolFactory createSingleInstance() {
		if (instance == null) {
			instance = createSingleThreadPool();
		}
		return instance;
	}

	public synchronized static void resetInstance(int corePoolSize,
			int maximumPoolSize) {
		instance.removeAllTask();
		instance = createInstance(corePoolSize, maximumPoolSize);
	}

	/**
	 * 创建常驻多任务单线程线程池 优点 线程不会频繁创建销毁 任务为队列形式不停执行
	 * 
	 * @return
	 */
	private static ThreadPoolFactory createSingleThreadPool() {
		ThreadPoolFactory threadPool = null;
		// 修改成多任务下载
		threadPool = new ThreadPoolFactory(poolSize, poolSize * POOL_SIZE,
				waitTime, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>(),
				Executors.defaultThreadFactory(),
				new ThreadPoolExecutor.DiscardOldestPolicy());
		return threadPool;
	}

	private static ThreadPoolFactory createInstance() {
		Log.d(TAG, "thread-corepollsize:" + corePoolSize);
		ThreadPoolFactory threadPool = null;
		threadPool = new ThreadPoolFactory(corePoolSize, corePoolSize
				+ poolSize, waitTime, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>(queueSize),
				Executors.defaultThreadFactory(),
				new ThreadPoolExecutor.DiscardOldestPolicy());
		return threadPool;
	}

	private static ThreadPoolFactory createInstance(int corePoolSize,
			int maximumPoolSize) {
		Log.d(TAG, "thread-corepollsize:" + corePoolSize);
		ThreadPoolFactory threadPool = null;
		threadPool = new ThreadPoolFactory(corePoolSize, maximumPoolSize,
				waitTime, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(
						queueSize), Executors.defaultThreadFactory(),
				new ThreadPoolExecutor.DiscardOldestPolicy());
		return threadPool;
	}

	/**
	 * 暂停线程池中工作线程
	 */
	public void pause() {
		pauseLock.lock();
		try {
			isPaused = true;
		} finally {
			pauseLock.unlock();
		}
	}

	/**
	 * 激活工作线程
	 */
	public void resume() {
		pauseLock.lock();
		try {
			isPaused = false;
			unpaused.signalAll();
		} finally {
			pauseLock.unlock();
		}
	}

	/**
	 * 移除没有被线程在执行的任务
	 */
	public void removeAllTask() {
		Log.d(TAG, "Thread pool :thread size:" + instance.getPoolSize()
				+ ",Completed Task size:" + instance.getCompletedTaskCount()
				+ ",Task size:" + instance.getTaskCount());
		Log.d(TAG, "BlockingQueue Runnable :" + instance.getQueue().size());
		try {
			this.purge();
			Log.d(TAG, "clearTask , runnable : " + instance.getQueue().size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void beforeExecute(Thread t, Runnable r) {
		super.beforeExecute(t, r);
		Log.d(TAG, "start Thread=======>:" + r.hashCode());
		pauseLock.lock();
		try {
			while (isPaused)
				unpaused.await();
		} catch (InterruptedException ie) {
			t.interrupt();
		} finally {
			pauseLock.unlock();
		}
		if (isStarted == false) {
			isStarted = true;
		}
	}

	@Override
	protected void afterExecute(Runnable r, Throwable t) {
		Log.d(TAG, "Thread excute over =======>:" + r.hashCode());
		super.afterExecute(r, t);
	}
}
