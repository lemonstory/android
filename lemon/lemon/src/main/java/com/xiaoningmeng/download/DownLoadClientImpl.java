package com.xiaoningmeng.download;

import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.xiaoningmeng.application.MyApplication;
import com.xiaoningmeng.bean.AlbumInfo;
import com.xiaoningmeng.bean.AudioDownLoad;
import com.xiaoningmeng.utils.FileUtil;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class DownLoadClientImpl extends
		DownloadObservable<DownLoadObserver<AudioDownLoad>> implements
		DownLoadClient<AudioDownLoad> {

	private final Map<AudioDownLoad, RequestHandle> mHashMap;
	private HashMap<String, AlbumInfo> albumArray;// 下载中的专辑
	private HashMap<String, AudioDownLoad> downloadArray;// 下载记录
	private HashMap<String, AudioDownLoad> historyArray;// 历史记录
	public LinkedHashMap<String, List<AudioDownLoad>> mDownloadMap;
	public LinkedHashMap<String, List<AudioDownLoad>> mHistoryMap;
	public List<AlbumInfo> mDownloadAlbumList;
	public List<AlbumInfo> mHistoryAlbumList;
	private static final int UNINIT = 0;
	private static final int INITING = -1;
	private static final int INITED = -2;
	private AtomicInteger initStatus = new AtomicInteger(UNINIT);
	private long preTime = 0;
	private static final int UPDATE_TIME = 200;
	private int downloadCount = 0;
	private OnDownloadCountChangedListener mOnDownloadCountChangedListener;

	private Handler mHandler = new Handler(Looper.getMainLooper()) {
		public void handleMessage(Message msg) {
			handleMsg(msg);
		}
	};

	private static DownLoadClientImpl impl;

	private DownLoadClientImpl() {

		mHashMap = new WeakHashMap<AudioDownLoad, RequestHandle>();
		downloadArray = new HashMap<String, AudioDownLoad>();
		historyArray = new HashMap<String, AudioDownLoad>();
		albumArray = new HashMap<String, AlbumInfo>();
		mDownloadAlbumList = new ArrayList<>();
		mHistoryAlbumList = new ArrayList<>();
		mDownloadMap = new LinkedHashMap<>();
		mHistoryMap = new LinkedHashMap<>();
		init();
	}

	public static DownLoadClientImpl getInstance() {
		return impl == null ? impl = new DownLoadClientImpl() : impl;
	}

	// 保存专辑之后开始下载
	public void download(AudioDownLoad video, AlbumInfo albumInfo) {

		addAlbum(albumInfo);
		download(video);
	}

	/**
	 * 下载 该方法必须确定数据库中有该专辑的数据才能调用
	 */
	@Override
	public void download(AudioDownLoad video) {
		if (video != null) {
			String url = video.getUrl();
			boolean pos = downloadArray.containsKey(url);
			if (!pos) {// 下载队列中没有此对象
				RequestHandle handler = mHashMap.get(video);
				if (handler != null && !handler.isFinished()) {
					handler.cancel(true);
					mHashMap.remove(video);
					// 下载
					start(video);
				} else {
					// 该对象对应的线程已经结束或者尚未开始
					boolean hisPos = historyArray.containsKey(video.getUrl());
					if (!hisPos) { // 历史记录中也没有此对象
						// 下载
						start(video);
					} else {
						/**
						 * 历史记录有此对象 就检查对象路径所对应的文件是否被删除 若被删除 则重新下载
						 */
						AudioDownLoad temp = historyArray.get(video.getUrl());
						File file = new File(temp.getTempPath());
						if (file.exists()) {
							AudioDownLoad downLoad = historyArray.remove(video.getUrl());
							removeDownload(downLoad, true);
							notifyCountData();
							video = temp;
							start(video);
						} else {
							file = new File(video.getRealPath());
							if (!file.exists()) {
								AudioDownLoad downLoad = historyArray.remove(video.getUrl());
								removeDownload(downLoad, true);
								
								video = temp;
								start(video);
							}
						}
					}
				}
			} else {// 如果下载队列中有此对象 就将队列中的对象赋值给此对象
				video = downloadArray.get(video.getUrl());
				// 该对象对应的线程已经结束或者尚未开始
				RequestHandle handler = mHashMap.get(video);
				if (handler == null || handler.isFinished()) {
					// 下载
					start(video);
				}
			}
		}
	}

	// 开始下载
	private void start(AudioDownLoad video) {
		DownLoadRunnable<AudioDownLoad> request = DownloaderFactory
				.getInstance().getDownloader(DownloaderFactory.First_Version,
						video);
		if (request == null) {
			notifyData(video, DownLoadState.DOWN_UNKOWNVERSION);
		} else {
			// 插入数据或者更新数据
			// getDbHelper().inserOrUpdateVideoBean(video);
			Cursor cursor = DataSupport
					.findBySQL("select * from AudioDownLoad where audioId ='"
							+ video.getAudioId() + "'");
			if (cursor.moveToFirst()) {
				video.updateAll("audioId =?", video.getAudioId());
			} else {
				video.save();
				addDownload(video, false,false);
			}
			notifyData(video, DownLoadState.DOWN_START);
			ThreadPoolFactory.createSingleInstance().submit(request);

			RequestHandle requestHandle = new RequestHandle(request);
			mHashMap.put(video, requestHandle);
		}

		checkRemove();
	}

	private void checkRemove() {
		Set<Entry<AudioDownLoad, RequestHandle>> mSet = mHashMap.entrySet();
		Iterator<Entry<AudioDownLoad, RequestHandle>> iterator = mSet
				.iterator();
		while (iterator.hasNext()) {
			Entry<AudioDownLoad, RequestHandle> entry = iterator.next();
			RequestHandle handler = entry.getValue();
			if (handler.shouldBeGarbageCollected()) {
				iterator.remove();
			}
		}
	}

	@Override
	public boolean isInited() {
		return initStatus.get() == INITED;
	}

	@Override
	public void init() {
		if (!isInited()) {
			String videoPath = FileUtil.getSaveDir(MyApplication.getInstance()
					.getApplicationContext());
			if (videoPath != null && !"".equals(videoPath)) {
				File file = new File(videoPath);
				if (!file.exists())
					file.mkdirs();
			}
			initStatus.set(INITING);
			String uid = MyApplication.getInstance().getUid() == null ? "" 
					: MyApplication.getInstance().getUid();
			try {
				List<AlbumInfo> albumInfos = DataSupport
						.findAll(AlbumInfo.class);
				if (albumInfos != null && albumInfos.size() > 0) {
					for (AlbumInfo albumInfo : albumInfos) {
						albumArray.put(albumInfo.getId(), albumInfo);
					}
				}
				List<AudioDownLoad> mList = DataSupport.findAll(AudioDownLoad.class);
				if (mList != null && mList.size() > 0) {
					for (AudioDownLoad videoBean : mList) {
						if (videoBean.getStatus() == DownLoadState.DOWN_SUC) {
							historyArray.put(videoBean.getUrl(), videoBean);
							addDownload(videoBean, true,true);
						} else {
							downloadArray.put(videoBean.getUrl(), videoBean);
							addDownload(videoBean, true,false);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			initStatus.set(INITED);
		}
	}

	private void addDownload(AudioDownLoad downLoad, boolean isBeign,boolean isSuc) {

		String albumId = downLoad.getAlbumid();
		AlbumInfo albumInfo = getAlbum(albumId);

		if (isSuc) {
			if (mHistoryMap.containsKey(albumId)) {
				mHistoryMap.get(albumId).add(downLoad);
			} else {
				List<AudioDownLoad> downLoads = new ArrayList<>();
				downLoads.add(downLoad);
				mHistoryAlbumList.add(albumInfo);
				mHistoryMap.put(albumId, downLoads);
			}
			if(isBeign){
				downloadCount++;
				notifyCountData();
			}
		} else {
			if (mDownloadMap.containsKey(albumId)) {
				mDownloadMap.get(albumId).add(downLoad);
			} else {
				List<AudioDownLoad> downLoads = new ArrayList<>();
				downLoads.add(downLoad);
				mDownloadAlbumList.add(albumInfo);
				mDownloadMap.put(albumId, downLoads);
				
			}
			downloadCount++;
			notifyCountData();
		}
	}

	private void removeDownload(AudioDownLoad downLoad, boolean isSuc) {

		String albumId = downLoad.getAlbumid();
		AlbumInfo albumInfo = getAlbum(albumId);
		if (isSuc) {
			if (mHistoryMap.containsKey(albumId)) {
				List<AudioDownLoad> downLoads = mHistoryMap.get(albumId);
				downLoads.remove(downLoad);
				if (downLoads.size() == 0) {
					mHistoryMap.remove(albumId);
					mHistoryAlbumList.remove(albumInfo);
				}
			}
		} else {
			if (mDownloadMap.containsKey(albumId)) {
				List<AudioDownLoad> downLoads = mDownloadMap.get(albumId);
				downLoads.remove(downLoad);
				if (downLoads.size() == 0) {
					mDownloadMap.remove(albumId);
					mDownloadAlbumList.remove(albumInfo);
				}
			}
		}
	}

	protected boolean abortRequest(AudioDownLoad file) {
		RequestHandle request = mHashMap.get(file);
		mHashMap.remove(file);
		if (request != null) {
			return request.cancel(true);
		}
		return false;
	}

	// 停止下载
	@Override
	public void pause(AudioDownLoad file) {
		RequestHandle request = mHashMap.get(file);
		mHashMap.remove(file);
		if (request != null) {
			request.pause();
			notifyData(file, DownLoadState.DOWN_PAUSE);
		}
	}

	/**
	 * 停止所有下载
	 */
	@Override
	public void pauseAll() {
		for (int i = 0; i < downloadArray.size(); i++) {
			for (String key : downloadArray.keySet()) {
				AudioDownLoad t = downloadArray.get(key);
				pause(t);
			}
		}
	}

	// 取消下载
	@Override
	public void cancel(AudioDownLoad file) {
		if (file != null) {
			DataSupport.deleteAll(AudioDownLoad.class, "audioId= ? ",
					file.getAudioId());
			abortRequest(file);
			String url = file.getUrl();
			boolean posDownload = downloadArray.containsKey(url);
			if (posDownload) {
				AudioDownLoad t = downloadArray.remove(url);
				removeDownload(t, false);
			} else {
				AudioDownLoad t = historyArray.remove(url);
				removeDownload(t, true);
			}

			notifyCancel(file);
			deleteFile(file);
		}
	}

	private void deleteFile(AudioDownLoad file) {
		File tempFile = new File(file.getTempPath());
		File realFile = new File(file.getRealPath());
		if (tempFile.exists()) {
			tempFile.delete();
		}
		if (realFile.exists()) {
			realFile.delete();
		}
	}

	/**
	 * 取消所有下载 并从数据库中删除
	 */
	@Override
	public void cancelAll() {
		if (downloadArray != null && downloadArray.size() > 0) {
			for (String key : downloadArray.keySet()) {
				AudioDownLoad t = downloadArray.get(key);
				DataSupport.deleteAll(AudioDownLoad.class, "audioId= ? ",
						t.getAudioId());
				abortRequest(t);
			}
			downloadArray.clear();
			mDownloadMap.clear();
			downloadCount = 0;
			notifyCountData();
			mDownloadAlbumList.clear();
		}
	}

	@Override
	public void downAll() {
		if (downloadArray != null && downloadArray.size() > 0) {
			for (String key : downloadArray.keySet()) {
				AudioDownLoad t = downloadArray.get(key);
				download(t);
			}
		}
	}

	@Override
	public int getHistoryCount(String uid) {
		return historyArray.size();
	}

	@Override
	public int getDownloadCount(String uid) {
		return downloadArray.size();
	}

	@Override
	public int getCount(String uid) {
		return DataSupport.count(AudioDownLoad.class);
	}

	@Override
	public AudioDownLoad getDownloadBeanByIndex(String url) {
		return downloadArray.get(url);
	}

	@Override
	public AudioDownLoad getHistoryBeanByIndex(String url) {
		return historyArray.get(url);
	}

	/**
	 * 提供一个方法供外部调用 通知更新
	 * 
	 * @param video
	 * @param state
	 */
	public void notifyData(AudioDownLoad video, int state) {
		long time = System.currentTimeMillis() - preTime;
		if (state == DownLoadState.DOWN_UPDATE) {
			video.setStatus(state);
			if (time > UPDATE_TIME) {
				mHandler.obtainMessage(state, video).sendToTarget();
				preTime = System.currentTimeMillis();
			}
		} else {
			if (state != video.getStatus()) {
				video.setStatus(state);
				if (Looper.myLooper() != null) {
					handleMsg(mHandler.obtainMessage(state, video));
				} else {
					mHandler.obtainMessage(state, video).sendToTarget();
				}
			}
		}
	}

	private void notifyData(AudioDownLoad t) {
		for (DownLoadObserver<AudioDownLoad> observer : mObservers) {
			observer.notifyData(t);
		}
	}

	private void notifyCountData() {
		if(mOnDownloadCountChangedListener != null){
			mOnDownloadCountChangedListener.notifyDownloadCountChanged(downloadCount);
		}
	}

	private void notifyCancel(AudioDownLoad t) {
		for (DownLoadObserver<AudioDownLoad> observer : mObservers) {
			observer.notifyCancel(t);
		}
	}

	private void handleMsg(Message msg) {
		AudioDownLoad video = (AudioDownLoad) msg.obj;
		String url = video.getUrl();
		boolean pos = downloadArray.containsKey(url);
		switch (msg.what) {
		case DownLoadState.DOWN_SUC:
			if (pos) {
				AudioDownLoad t = downloadArray.remove(url);
				removeDownload(t, false);
				historyArray.put(url, video);
				addDownload(video,false, true);
			}
			break;
		case DownLoadState.DOWN_START:
			downloadArray.put(video.getUrl(), video);
			break;
		// case DownState.DOWN_CANCEL:
		//
		// break;
		default:
			break;
		}
		notifyData(video);
	}

	public static void clearStance() {
		impl = null;
	}

	@Override
	public void clearAll() {

		new Thread() {
			@Override
			public void run() {
				DataSupport.deleteAll(AudioDownLoad.class);
				String videoPath = FileUtil.getSaveDir(MyApplication
						.getInstance().getApplicationContext());
				File vidoPathfile = new File(videoPath);
				if (vidoPathfile.exists()) {
					File[] listFile = vidoPathfile.listFiles();
					for (File file : listFile) {
						if (!file.isDirectory()) {
							file.delete();
						}
					}
				}
			}
		}.start();
	}

	public int getDownloadSize() {

		int totalLength = 0;
		String videoPath = FileUtil.getSaveDir(MyApplication.getInstance()
				.getApplicationContext());
		File vidoPathfile = new File(videoPath);
		if (vidoPathfile.exists()) {
			File[] listFile = vidoPathfile.listFiles();
			for (File file : listFile) {
				if (file.isFile()) {
					totalLength += file.length();
				}
			}
		}
		return totalLength;
	}

	public int getDownloadStatus(String url) {
		if (downloadArray.containsKey(url)) {
			return 0;
		}
		if (historyArray.containsKey(url)) {
			return 1;
		}
		return -1;
	}

	public void addAlbum(AlbumInfo albumInfo) {

		if (!albumArray.containsKey(albumInfo.getId())) {
			albumInfo.save();
			albumArray.put(albumInfo.getId(), albumInfo);
		}
	}

	public AlbumInfo getAlbum(String albumId) {
		return albumArray.get(albumId);
	}

	public List<AudioDownLoad> getAllDownLoads() {
		String uid = MyApplication.getInstance().getUid();
		List<AudioDownLoad> mList = DataSupport.where("uid = ?",
				uid == null ? "" : uid).find(AudioDownLoad.class);
		return mList;
	}

	public int getDownloadCount() {
		return downloadCount;
	}

	public HashMap<String, AudioDownLoad> getDownloadArray() {
		return downloadArray;
	}

	public HashMap<String, AudioDownLoad> getHistoryArray() {
		return historyArray;
	}

	public void setOnDownloadCountChangedListener(OnDownloadCountChangedListener mOnDownloadCountChangedListener) {
		this.mOnDownloadCountChangedListener = mOnDownloadCountChangedListener;
	}

	
}
