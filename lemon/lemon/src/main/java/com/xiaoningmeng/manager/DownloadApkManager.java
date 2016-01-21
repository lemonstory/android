package com.xiaoningmeng.manager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import com.xiaoningmeng.utils.FileUtils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * 用于下载apk文件
 */
public class DownloadApkManager {


	private Context mContext;

    private static final String savePath = FileUtils.SDPATH()+"update/";
    
    private static final String saveFileName = savePath + "other.apk";


    private static final int DOWN_UPDATE = 1;
    
    private static final int DOWN_OVER = 2;
    
    private String apkUrl;
    
    private int progress;
    
    private Thread downLoadThread;
    
    private boolean interceptFlag = false;
    

	private DownloadApkManager(){}

	private static DownloadApkManager mInstance;

	public static DownloadApkManager getInstance(){
		if(mInstance == null){
			synchronized (DownloadApkManager.class){
				if(mInstance == null)
					mInstance = new DownloadApkManager();
			}
		}
		return  mInstance;
	}

	private Runnable mdownApkRunnable = new Runnable() {	
		@Override
		public void run() {
			try {
				URL url = new URL(apkUrl);
				HttpURLConnection conn = (HttpURLConnection)url.openConnection();
				conn.connect();
				int length = conn.getContentLength();
				InputStream is = conn.getInputStream();
				
				File file = new File(savePath);
				if(!file.exists()){
					file.mkdir();
				}
				String apkFile = saveFileName;
				File ApkFile = new File(apkFile);
				FileOutputStream fos = new FileOutputStream(ApkFile);
				
				int count = 0;
				byte buf[] = new byte[1024];
				
				do{   		   		
		    		int numread = is.read(buf);
		    		count += numread;
		    	    progress =(int)(((float)count / length) * 100);
		    	    //更新进度

		    		if(numread <= 0){	
		    			//下载完成通知安装
		    			installApk();
		    			break;
		    		}
		    		fos.write(buf,0,numread);
		    	}while(!interceptFlag);//点击取消就停止下载.
				
				fos.close();
				is.close();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch(IOException e){
				e.printStackTrace();
			}
			
		}
	};
	


	
	public void downloadApk(Context context,String apkUrl){
		this.apkUrl = apkUrl;
		this.mContext = context;
		downLoadThread = new Thread(mdownApkRunnable);
		downLoadThread.start();
	}

	private void installApk(){
		File apkfile = new File(saveFileName);
        if (!apkfile.exists()) {
            return;
        }    
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive"); 
        mContext.startActivity(i);
	
	}

}

