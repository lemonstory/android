package com.xiaoningmeng.manager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.Header;

import com.xiaoningmeng.application.MyApplication;
import com.xiaoningmeng.bean.AppInfo;
import com.xiaoningmeng.bean.AudioDownLoad;
import com.xiaoningmeng.bean.ListenerAlbum;
import com.xiaoningmeng.db.HistoryDao;
import com.xiaoningmeng.download.DownLoadClientImpl;
import com.xiaoningmeng.http.LHttpHandler;
import com.xiaoningmeng.http.LHttpRequest;
import com.xiaoningmeng.utils.DebugUtils;

import android.annotation.SuppressLint;
import android.os.AsyncTask;


public class UploadManager {

	@SuppressLint("SimpleDateFormat")
	public static SimpleDateFormat dateTimeformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private UploadManager(){}
	
	
	private static UploadManager mInstance;
	
	public static UploadManager getInstance(){
		if(mInstance == null){
			synchronized (UploadManager.class) {
				if(mInstance == null){
					mInstance = new UploadManager();
				}
			}
		}
		return mInstance;
	}

	
	public void uploadRecord(){
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				uploadPlayRecord();
				uploadDownloadRecord();
				return null;
			}
			
		}.execute();
	}
	
	private void uploadPlayRecord() {
		final List<ListenerAlbum> albums = HistoryDao.getInstance().getHistoryAlbums();
		if(albums.size() > 0){
			StringBuffer record = new StringBuffer("[");
			for(ListenerAlbum album :albums){
				Date date = new Date(Long.parseLong(album.getUptime())*1000);
				String time = dateTimeformat.format(date);
				record.append("{\"albumid\":\""+album.getAlbumid()+"\",\"storyid\":\""+album.getPlaystoryid()+"\",\"playtimes\":"+ album.getPlaytimes()+",\"datetimes\":\""+time+"\"}");
				record.append(",");
			}
			record = record.deleteCharAt(record.length()-1);
			record.append("]");
			LHttpRequest.getInstance().addRecordReq(MyApplication.getContext(), record.toString(), new LHttpHandler<String>(MyApplication.getContext()) {

				@Override
				public void onGetDataSuccess(String data) {

					new Thread(new Runnable() {
								
								@Override
								public void run() {
									for(ListenerAlbum album :albums){
										HistoryDao.getInstance().uploadRecord(album.getAlbumid());
									}
								}
							}
					).start();
					
				}
				
				@Override
				public void onFailure(int statusCode, Header[] headers,
						String responseString, Throwable throwable) {
				}
			});
		}
	}
	
	private void uploadDownloadRecord(){
		
		final HashMap<String, AudioDownLoad> historyArray = DownLoadClientImpl.getInstance().getHistoryArray();
		final HashMap<String, AudioDownLoad> downloadArray = DownLoadClientImpl.getInstance().getDownloadArray();
		StringBuffer record = new StringBuffer("[");
		int uploadCount = 0;
		Iterator<Entry<String, AudioDownLoad>> iter = historyArray.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, AudioDownLoad> entry = (Map.Entry<String, AudioDownLoad>) iter.next(); 
			AudioDownLoad downLoad = entry.getValue();
			if(!downLoad.isUpload()){
				uploadCount++;
				record.append("{\"clientid\":\""+AppInfo.getInstance().getIMEI()+"\",\"albumid\":\""+downLoad.getAlbumid()
						+"\",\"storyid\":\""+downLoad.getStoryId()+"\",\"status\":\""+2+"\"}");
				record.append(",");
			}
		}
		Iterator<Entry<String, AudioDownLoad>> iter2 = downloadArray.entrySet().iterator();
		while (iter2.hasNext()) {
			Map.Entry<String, AudioDownLoad> entry = (Map.Entry<String, AudioDownLoad>) iter2.next(); 
			AudioDownLoad downLoad = entry.getValue();
			if(!downLoad.isUpload()){
				uploadCount++;
				record.append("{\"clientid\":\""+AppInfo.getInstance().getIMEI()+"\",\"albumid\":\""+downLoad.getAlbumid()
						+"\",\"storyid\":\""+downLoad.getStoryId()+"\",\"status\":\""+1+"\"}");
				record.append(",");
			}
		}
		record = record.deleteCharAt(record.length()-1);
		record.append("]");
		if(uploadCount > 0){
			LHttpRequest.getInstance().addDownRecordReq(MyApplication.getContext(), record.toString(), new LHttpHandler<String>(MyApplication.getContext()) {
				
				@Override
				public void onGetDataSuccess(String data) {
					updateLocalDownloadRecrod(downloadArray);
					updateLocalDownloadRecrod(historyArray);
				}
				
				@Override
				public void onFailure(int statusCode, Header[] headers,
						String responseString, Throwable throwable) {
				}
			});
			
			
		}
	}
	
	private void updateLocalDownloadRecrod(HashMap<String, AudioDownLoad> array){
		Iterator<Entry<String, AudioDownLoad>> iter = array.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, AudioDownLoad> entry = (Map.Entry<String, AudioDownLoad>) iter.next(); 
			AudioDownLoad downLoad = entry.getValue();
			if(!downLoad.isUpload()){
				downLoad.setUpload(true);
				downLoad.updateAll("audioId =?", downLoad.getAudioId());
			}
		}
	}
}
