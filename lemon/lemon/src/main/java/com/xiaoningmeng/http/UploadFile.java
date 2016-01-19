package com.xiaoningmeng.http;

import java.io.File;
import java.io.FileNotFoundException;

import com.alibaba.sdk.android.oss.OSSService;
import com.alibaba.sdk.android.oss.callback.SaveCallback;
import com.alibaba.sdk.android.oss.storage.OSSBucket;
import com.alibaba.sdk.android.oss.storage.OSSFile;
import com.xiaoningmeng.application.MyApplication;
import com.xiaoningmeng.constant.Constant;


public class UploadFile {

    private OSSService ossService;
    private OSSBucket bucket;
    private static UploadFile mInstance;
    
    
    private UploadFile(){
    	  ossService = OSSAuth.ossService;
          bucket = ossService.getOssBucket(Constant.bucketName); // 替换为你的bucketName
    }
    
    public static UploadFile getInstance(){
    	if(mInstance == null){
    		synchronized (UploadFile.class) {
    			if(mInstance == null){
    				mInstance = new UploadFile();
    			}
			}
    	}
    	return mInstance;
    }

    // 断点上传
    public void asyncUpload(String  filePath,SaveCallback callBack) {
        OSSFile bigfFile = ossService.getOssFile(bucket, MyApplication.getInstance().getUid());
        try {
            bigfFile.setUploadFilePath(filePath, "application/octet-stream");
            bigfFile.ResumableUploadInBackground(callBack);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
