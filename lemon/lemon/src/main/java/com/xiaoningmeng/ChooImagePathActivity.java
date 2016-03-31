package com.xiaoningmeng;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.xiaoningmeng.adapter.PhotoAlbumLVAdapter;
import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.bean.PhotoAlbumLVItem;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.utils.Utility;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * 在图片文件夹中选择图片
 */

public class ChooImagePathActivity extends BaseActivity {

    public ArrayList<String> mSelectedImageList = new ArrayList<String>();
    private static ChooImagePathActivity mInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mInstance = this;
        Fresco.initialize(this);
        setContentView(R.layout.activity_photo_album);
        setTitleName("目录选择");
        if (!Utility.isSDcardOK()) {
            Utility.showToast(this,
                    getResources().getString(R.string.sd_disable));
            return;
        }
        mSelectedImageList.clear();
        ListView listView = (ListView) findViewById(R.id.select_img_listView);
        // 使用ContentProvider
        final ArrayList<PhotoAlbumLVItem> list = new ArrayList<PhotoAlbumLVItem>();
        // 相册
        list.addAll(getImagePathsByContentProvider());

        PhotoAlbumLVAdapter adapter = new PhotoAlbumLVAdapter(this, list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Intent intent = new Intent(ChooImagePathActivity.this,
                        PhotoWallActivity.class);
                intent.putExtra("folderPath", list.get(position).getPathName());
                intent.putExtra("activityName",
                        getIntent().getStringExtra("activityName"));
                intent.putExtra("maxCount",
                        getIntent().getIntExtra("maxCount", 1));
                startActivity(intent);
                overridePendingTransition(R.anim.main_translatex100to0,
                        R.anim.main_translatex0tof100);
            }
        });
        //Slidr.attach(this);
    }

    /**
     * 获取目录中图片的个数。
     */
    private int getImageCount(File folder) {
        int count = 0;

        if (folder == null) {
            return 0;
        }
        File[] files = folder.listFiles();

        if (files == null) {
            return 0;
        }
        for (File file : files) {
            if (file != null && Utility.isImage(file.getAbsolutePath())) {
                count++;
            }
        }
        return count;
    }

    /**
     * 获取目录中最新的一张图片的绝对路径。
     */
    private String getFirstImagePath(File folder) {

        if (folder == null) {
            return null;
        }
        File[] files = folder.listFiles();

        if (files == null) {
            return null;
        }
        for (int i = files.length - 1; i >= 0; i--) {
            File file = files[i];
            if (Utility.isImage(file.getAbsolutePath())) {
                return file.getAbsolutePath();
            }
        }

        return null;
    }

    /**
     * 使用ContentProvider读取SD卡所有图片。
     */
    private ArrayList<PhotoAlbumLVItem> getImagePathsByContentProvider() {
        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String key_MIME_TYPE = MediaStore.Images.Media.MIME_TYPE;
        String key_DATA = MediaStore.Images.Media.DATA;

        ContentResolver mContentResolver = getContentResolver();

        Cursor cursor = mContentResolver.query(mImageUri,
                new String[]{key_DATA}, key_MIME_TYPE + "=? or "
                        + key_MIME_TYPE + "=? or " + key_MIME_TYPE + "=?",
                new String[]{"image/jpg", "image/jpeg", "image/png"},
                MediaStore.Images.Media.DATE_MODIFIED);

        ArrayList<PhotoAlbumLVItem> list = new ArrayList<PhotoAlbumLVItem>();
        if (cursor != null) {
            if (cursor.moveToLast()) {
                // 路径缓存，防止多次扫描同一目录
                HashSet<String> cachePath = new HashSet<String>();

                while (true) {
                    // 获取图片的路径
                    String imagePath = cursor.getString(0);

                    if (imagePath == null) {
                        continue;
                    }
                    File file = new File(imagePath);
                    if (file != null) {

                        File parentFile = file.getParentFile();

                        if (parentFile != null) {
                            String parentPath = parentFile.getAbsolutePath();

                            // 不扫描重复路径
                            if (!cachePath.contains(parentPath)) {

                                PhotoAlbumLVItem albumLVItem = new PhotoAlbumLVItem(
                                        parentPath, getImageCount(parentFile),
                                        getFirstImagePath(parentFile));

                                if (albumLVItem.getFileCount() != 0
                                        && !albumLVItem.getPathName().contains(
                                        Constant.FILE_DIR_NAME)) {
                                    list.add(albumLVItem);
                                }
                                cachePath.add(parentPath);
                            }
                        }

                        if (!cursor.moveToPrevious()) {
                            break;
                        }

                    }

                }
            }

            cursor.close();
        }

        return list;
    }

    // 重写返回键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    public static ChooImagePathActivity getInstance() {
        return mInstance;
    }
}
