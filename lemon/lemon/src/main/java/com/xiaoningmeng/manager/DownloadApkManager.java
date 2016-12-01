package com.xiaoningmeng.manager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.xiaoningmeng.utils.FileUtils;
import com.xiaoningmeng.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 用于下载apk文件
 */
public class DownloadApkManager {


	private Context mContext;

    private static final String savePath = FileUtils.SDPATH()+"download/";
    
    private static final String saveFileName = savePath + "other.apk";


    private static final int DOWN_UPDATE = 1;
    
    private static final int DOWN_OVER = 2;
    
    private String apkUrl;
    
    private int progress;
    
    private Thread downLoadThread;
    
    private boolean interceptFlag = false;

	private ProgressBar mProgressbar;
	private TextView mProgressTv;
	private DialogPlus downloadDialog;
    

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

	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case DOWN_UPDATE:
					mProgressbar.setProgress(progress);
					mProgressTv.setText(progress+"%");
					break;
				case DOWN_OVER:
					mProgressbar.setProgress(100);
					mProgressTv.setText("100%");
					downloadDialog.dismiss();
					installApk();
					break;
				default:
					break;
			}
		}
	};

	public void showDownloadDialog(Context context,String donwloadUrl){
		this.mContext = context;
		apkUrl = donwloadUrl;
		interceptFlag = false;
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.dialog_download_tip, null);
		mProgressbar = (ProgressBar) v.findViewById(R.id.rpb_download_progress);
		mProgressTv = (TextView)v.findViewById(R.id.tv_download_progress);
		int margin = mContext.getResources().getDimensionPixelOffset(R.dimen.dialog_margin);
		downloadDialog =  DialogPlus.newDialog(mContext)
				.setContentHolder(new ViewHolder(v))
				.setGravity(Gravity.CENTER)
				.setMargin(margin, 0, margin, 0)
//				.setIsTransparentBg(false)
				.setOnClickListener(new com.orhanobut.dialogplus.OnClickListener() {

					@Override
					public void onClick(DialogPlus dialog, View view) {
						if(view.getId() == R.id.tv_dialog_cancel){
							dialog.dismiss();
							interceptFlag = true;
						}
					}
				}).setContentBackgroundResource(R.drawable.dialog_white_bg).create();
		downloadDialog.show();
		downloadApk();
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
				ApkFile.deleteOnExit();
				FileOutputStream fos = new FileOutputStream(ApkFile);

				int count = 0;
				byte buf[] = new byte[1024];

				do{
					int numread = is.read(buf);
					count += numread;
					progress =(int)(((float)count / length) * 100);
					//更新进度
					mHandler.sendEmptyMessage(DOWN_UPDATE);
					if(numread <= 0){
						//下载完成通知安装
						mHandler.sendEmptyMessage(DOWN_OVER);
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

	private void downloadApk(){
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

