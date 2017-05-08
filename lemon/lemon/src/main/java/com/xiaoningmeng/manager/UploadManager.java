package com.xiaoningmeng.manager;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import com.orhanobut.logger.Logger;
import com.xiaoningmeng.bean.AppInfo;
import com.xiaoningmeng.bean.AudioDownLoad;
import com.xiaoningmeng.bean.ListenerAlbum;
import com.xiaoningmeng.db.HistoryDao;
import com.xiaoningmeng.download.DownLoadClientImpl;
import com.xiaoningmeng.http.JsonResponse;
import com.xiaoningmeng.http.LHttpRequest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.xiaoningmeng.http.LHttpRequest.mRetrofit;


public class UploadManager {

    @SuppressLint("SimpleDateFormat")
    public static SimpleDateFormat dateTimeformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private UploadManager() {
    }


    private static UploadManager mInstance;

    public static UploadManager getInstance() {
        if (mInstance == null) {
            synchronized (UploadManager.class) {
                if (mInstance == null) {
                    mInstance = new UploadManager();
                }
            }
        }
        return mInstance;
    }


    public void uploadRecord() {
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
        if (albums.size() > 0) {
            StringBuffer record = new StringBuffer("[");
            for (ListenerAlbum album : albums) {
                Date date = new Date(Long.parseLong(album.getUptime()) * 1000);
                String time = dateTimeformat.format(date);
                record.append("{\"albumid\":\"" + album.getAlbumid() + "\",\"storyid\":\"" + album.getPlaystoryid() + "\",\"playtimes\":" + album.getPlaytimes() + ",\"datetimes\":\"" + time + "\"}");
                record.append(",");
            }
            record = record.deleteCharAt(record.length() - 1);
            record.append("]");

            LHttpRequest.AddRecordRequest addRecordRequest = mRetrofit.create(LHttpRequest.AddRecordRequest.class);
            Call<JsonResponse<String>> call = addRecordRequest.getResult(record.toString());
            call.enqueue(new Callback<JsonResponse<String>>() {

                @Override
                public void onResponse(Call<JsonResponse<String>> call, Response<JsonResponse<String>> response) {

                    if (response.isSuccessful() && response.body().isSuccessful()) {
                        String data = response.body().getData();
                        new Thread(new Runnable() {

                            @Override
                            public void run() {
                                for (ListenerAlbum album : albums) {
                                    HistoryDao.getInstance().uploadRecord(album.getAlbumid());
                                }
                            }
                        }
                        ).start();
                    } else {
                        Logger.e(response.toString());
                    }
                }

                @Override
                public void onFailure(Call<JsonResponse<String>> call, Throwable t) {
                    Logger.e(t.toString());
                }
            });
        }
    }

    private void uploadDownloadRecord() {

        final HashMap<String, AudioDownLoad> historyArray = DownLoadClientImpl.getInstance().getHistoryArray();
        final HashMap<String, AudioDownLoad> downloadArray = DownLoadClientImpl.getInstance().getDownloadArray();
        StringBuffer record = new StringBuffer("[");
        int uploadCount = 0;
        Iterator<Entry<String, AudioDownLoad>> iter = historyArray.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, AudioDownLoad> entry = iter.next();
            AudioDownLoad downLoad = entry.getValue();
            if (!downLoad.isUpload()) {
                uploadCount++;
                record.append("{\"clientid\":\"" + AppInfo.getInstance().getIMEI() + "\",\"albumid\":\"" + downLoad.getAlbumid()
                        + "\",\"storyid\":\"" + downLoad.getStoryId() + "\",\"status\":\"" + 2 + "\"}");
                record.append(",");
            }
        }
        Iterator<Entry<String, AudioDownLoad>> iter2 = downloadArray.entrySet().iterator();
        while (iter2.hasNext()) {
            Map.Entry<String, AudioDownLoad> entry = iter2.next();
            AudioDownLoad downLoad = entry.getValue();
            if (!downLoad.isUpload()) {
                uploadCount++;
                record.append("{\"clientid\":\"" + AppInfo.getInstance().getIMEI() + "\",\"albumid\":\"" + downLoad.getAlbumid()
                        + "\",\"storyid\":\"" + downLoad.getStoryId() + "\",\"status\":\"" + 1 + "\"}");
                record.append(",");
            }
        }
        record = record.deleteCharAt(record.length() - 1);
        record.append("]");
        if (uploadCount > 0) {

            LHttpRequest.AddDownRecordRequest addDownRecordRequest = mRetrofit.create(LHttpRequest.AddDownRecordRequest.class);
            Call<JsonResponse<String>> call = addDownRecordRequest.getResult(record.toString());
            call.enqueue(new Callback<JsonResponse<String>>() {

                @Override
                public void onResponse(Call<JsonResponse<String>> call, Response<JsonResponse<String>> response) {

                    if (response.isSuccessful() && response.body().isSuccessful()) {

                        updateLocalDownloadRecrod(downloadArray);
                        updateLocalDownloadRecrod(historyArray);
                    } else {
                        Logger.e(response.toString());
                    }
                }

                @Override
                public void onFailure(Call<JsonResponse<String>> call, Throwable t) {

                    Logger.e(t.toString());
                }
            });
        }
    }

    private void updateLocalDownloadRecrod(HashMap<String, AudioDownLoad> array) {
        Iterator<Entry<String, AudioDownLoad>> iter = array.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, AudioDownLoad> entry = iter.next();
            AudioDownLoad downLoad = entry.getValue();
            if (!downLoad.isUpload()) {
                downLoad.setUpload(true);
                downLoad.updateAll("audioId =?", downLoad.getAudioId());
            }
        }
    }
}
