package com.xiaoningmeng.download;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xiaoningmeng.DownloadStoryActivity;
import com.xiaoningmeng.R;
import com.xiaoningmeng.application.MyApplication;
import com.xiaoningmeng.bean.AlbumInfo;
import com.xiaoningmeng.bean.AudioDownLoad;

import com.xiaoningmeng.reminder.RemindBroadcastReceiver;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

public class DownloadNotificationManager implements DownLoadObserver<AudioDownLoad>{

	private NotificationManager mNotificationManager;

	private Context mContext;
	private static DownloadNotificationManager mInstance;
	public Map<String,DownloadNotification> notificationMap;
	private static int NOTIFY_ID = 10000;
	
	public static DownloadNotificationManager getInstance() {
		if (mInstance == null) {
			synchronized (DownloadNotificationManager.class) {
				if (mInstance == null) {
					mInstance = new DownloadNotificationManager();
				}
			}
		}
		return mInstance;
	}
	
	private DownloadNotificationManager() {
		
		mContext = MyApplication.getContext();
		mNotificationManager = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationMap = new HashMap<>();
		DownLoadClientImpl.getInstance().registerObserver(this);
	}

	
	private static class DownloadNotification{
		Notification notification;
		RemoteViews notifyView;
		int notifyId;
		Map<String,AudioDownLoad> downloadMap;
		AlbumInfo albumInfo;
		public DownloadNotification(AlbumInfo albumInfo,int notifyId,Notification notification,
				RemoteViews notifyView) {
			this.notification = notification;
			this.notifyView = notifyView;
			this.notifyId = notifyId;
			this.albumInfo = albumInfo;
			downloadMap = new HashMap<>();
		}
	}

	@SuppressWarnings("deprecation")
	public void notifyData(AudioDownLoad download){

		Notification notification = null;
		RemoteViews notifyView = null;
		int notifyId = 0;
		AlbumInfo albumInfo;
		String albumId = download.getAlbumid();
		DownloadNotification downloadNotification;
		if(notificationMap.containsKey(download.getAlbumid())){
			downloadNotification = notificationMap.get(albumId);
			notification = downloadNotification.notification;
			notifyView = downloadNotification.notifyView;
			notifyId = downloadNotification.notifyId;
			albumInfo = downloadNotification.albumInfo;
		}else{
			notification = new Notification(R.drawable.logo,mContext.getString(R.string.app_name), System.currentTimeMillis());
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			albumInfo = DownLoadClientImpl.getInstance().getAlbum(albumId);
			notifyView= new RemoteViews(mContext.getPackageName(),R.layout.notity_download);
			notification.contentView = notifyView;
			NOTIFY_ID++;
			notifyId = NOTIFY_ID;
			downloadNotification = new DownloadNotification(albumInfo,notifyId, notification, notifyView);
			notificationMap.put(albumId, downloadNotification);
		}
		int type = 0;
		
		if(download.getStatus() == DownLoadState.DOWN_SUC && !DownLoadClientImpl.getInstance().mDownloadMap.containsKey(albumId)){
			notifyView.setTextViewText(R.id.notify_download_title, "下载完成："+albumInfo.getTitle());
			List<AudioDownLoad> downLoads = DownLoadClientImpl.getInstance().mHistoryMap.get(albumId);
			notifyView.setTextViewText(R.id.notify_download_tip, "本次累计下载故事"+downLoads.size()+"个");
			notifyView.setViewVisibility(R.id.rpb_notify_progress, View.GONE);
			notifyView.setImageViewResource(R.id.notify_logo, R.drawable.notice_icon_download_complete);
			notifyView.setViewVisibility(R.id.notify_download_tip, View.VISIBLE);
			notifyView.setViewVisibility(R.id.notify_download_see, View.VISIBLE);
			type = DownloadStoryActivity.HISTORY_TYPE;
			
		}else{
			String storyId = download.getStoryId();
			if(download.getStatus() == DownLoadState.DOWN_START){
				downloadNotification.downloadMap.put(storyId, download);
			}
			if(download.getStatus() == DownLoadState.DOWN_CANCEL){
				downloadNotification.downloadMap.remove(storyId);
			}
			int currentLength = 0;
			int allLength = 0;
			for (AudioDownLoad value : downloadNotification.downloadMap.values()) {
			   currentLength += value.getCurPos();
			   allLength += value.getEndPos();
			}
			int progress = allLength != 0 ? currentLength * 100 /allLength :0;
			notifyView.setTextViewText(R.id.notify_download_title, "正在下载："+albumInfo.getTitle());
			notifyView.setProgressBar(R.id.rpb_notify_progress, 100, progress, false);
			notifyView.setImageViewResource(R.id.notify_logo, R.drawable.notice_icon_download);
			notifyView.setViewVisibility(R.id.rpb_notify_progress, View.VISIBLE);
			notifyView.setViewVisibility(R.id.notify_download_tip, View.GONE);
			notifyView.setViewVisibility(R.id.notify_download_see, View.GONE);
			type = DownloadStoryActivity.DOWNLOAD_TYPE;
		}
		
		Intent i = new Intent(RemindBroadcastReceiver.NOTIFICATION_DOWNLOAD);
		i.putExtra("type",type);
		i.putExtra("album", albumInfo);
		PendingIntent contentIntent = PendingIntent.getBroadcast(mContext,0,i,PendingIntent.FLAG_UPDATE_CURRENT);
		notification.contentIntent = contentIntent;
		mNotificationManager.notify(notifyId,notification);
	}

	public void cancel() {
		
		mNotificationManager.cancelAll();
		DownLoadClientImpl.getInstance().unregisterObserver(this);
	}


	@Override
	public void notifyCancel(AudioDownLoad t) {
	
		
	}

}
