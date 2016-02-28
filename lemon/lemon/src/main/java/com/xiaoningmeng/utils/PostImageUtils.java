package com.xiaoningmeng.utils;

import android.app.Activity;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;

import java.util.HashMap;

/**
 * Created by gaoyong on 16/2/26.
 */
public class PostImageUtils {

    private static final int DEFAULT_POST_IMAGE_WIDTH = 640;
    private static final int DEFAULT_POST_IMAGE_HEIGHT = 640;

    public static HashMap<String,Integer> parseImageSizeWithUrl(Activity activity, String imageUrl) {

        int widthPx = DEFAULT_POST_IMAGE_WIDTH;
        int heightPx = DEFAULT_POST_IMAGE_HEIGHT;

        String[] file = imageUrl.split("\\.");
        int index = file.length - 2;
        String[] snippet = file[index].split("_");

        int snippetCount = snippet.length;
        if (snippetCount >= 3) {

            if(null != snippet[1]) {
                widthPx = Integer.parseInt(snippet[1]);
            }

            if(null != snippet[2]) {
                heightPx = Integer.parseInt(snippet[2]);
            }
        }
        WindowManager windowManager = activity.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        int screenWidth = 0;
        int screenHeight = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            display.getSize(size);
            screenWidth = size.x;
            screenHeight = size.y;
        } else {
            screenWidth = display.getWidth();
            screenHeight = display.getHeight();
        }


        if (widthPx > screenWidth) {
            widthPx = screenWidth;
        }else if (widthPx < DEFAULT_POST_IMAGE_WIDTH) {
            widthPx = DEFAULT_POST_IMAGE_WIDTH;
        }

        if (heightPx > screenHeight) {
            heightPx = screenHeight;
        }else if (heightPx < DEFAULT_POST_IMAGE_HEIGHT) {
            heightPx = DEFAULT_POST_IMAGE_HEIGHT;
        }

        HashMap<String,Integer> imageSize = new HashMap<String,Integer>();
        imageSize.put("widthPx",widthPx);
        imageSize.put("heightPx",heightPx);
        return imageSize;
    }
}
