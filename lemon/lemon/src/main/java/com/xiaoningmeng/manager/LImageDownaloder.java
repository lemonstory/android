package com.xiaoningmeng.manager;

import android.content.Context;

import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.xiaoningmeng.bean.AppInfo;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.utils.AppUtils;

import java.io.IOException;
import java.net.HttpURLConnection;

public class LImageDownaloder extends BaseImageDownloader{

    public LImageDownaloder(Context context) {
        super(context);
    }

    public LImageDownaloder(Context context, int connectTimeout, int readTimeout) {
        super(context, connectTimeout, readTimeout);
    }


    
    protected HttpURLConnection createConnection(String url, Object extra) throws IOException {
		
   
    	 HttpURLConnection conn = super.createConnection(url,extra);
    	 conn.setRequestProperty("FROM", "mobile");
    	 conn.setRequestProperty("User-Agent", AppInfo.getInstance().getUAStr());
         conn.setConnectTimeout(connectTimeout);
         conn.setReadTimeout(readTimeout);
         return conn;
	}
}
