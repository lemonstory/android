package com.xiaoningmeng;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.xiaoningmeng.adapter.PhotoWallAdapter;
import com.xiaoningmeng.bean.ImageFile;
import com.xiaoningmeng.utils.FileUtils;
import com.xiaoningmeng.utils.Utility;
import com.xiaoningmeng.view.dialog.TipDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

public class PhotoWallActivity extends com.xiaoningmeng.base.BaseActivity {

    public int max_count = 3;
    public final static int EACH_COUNT = 10;
    public static final int TARGET_POS = 1;
    public AtomicInteger count = new AtomicInteger(0);
    private GridView mPhotoWall;
    private PhotoWallAdapter adapter;
    private String currentFolder = null;
    private ArrayList<ImageFile> ImageFileList;
    private String strVideoPath;
    private boolean sendToTargetFlag = true;
    private Vector<String> pathsVector;
    private String mActivityName;
    private ArrayList<String> mSelectedImageList;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            if (msg.what == EACH_COUNT) {
                if (!sendToTargetFlag) {
                    sendToTargetFlag = true;
                    if (strVideoPath != null && new File(strVideoPath).exists()) {
                        File file = new File(strVideoPath);
                        file.delete();
                    }
                    return;
                }
                if (strVideoPath != null && new File(strVideoPath).exists()) {
                    mSelectedImageList.clear();
                }
            } else if (msg.what == TARGET_POS) {

                int pos = msg.arg1;
                if (pos >= 0 && pos < ImageFileList.size()) {
                    ImageFileList.get(pos).setSelected(false);
                    int firstPos = mPhotoWall.getFirstVisiblePosition();
                    if (pos >= firstPos) {
                        ViewGroup covertView = (ViewGroup) mPhotoWall
                                .getChildAt(pos - firstPos);
                        if (covertView != null) {
                            covertView.findViewById(R.id.selected_back_view);
                            ((ViewGroup) covertView.getChildAt(0))
                                    .getChildAt(1)
                                    .setVisibility(View.INVISIBLE);
                            ((ViewGroup) covertView.getChildAt(0))
                                    .getChildAt(2)
                                    .setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_photo_wall);
        setTitleName("相册");
        //setRightHeadText("确定");
        mPhotoWall = (GridView) findViewById(R.id.photo_wall_grid);
        mSelectedImageList = ChooImagePathActivity.getInstance().mSelectedImageList;
        ImageFileList = new ArrayList<ImageFile>();
        adapter = new PhotoWallAdapter(this, ImageFileList);
        mPhotoWall.setAdapter(adapter);
        currentFolder = getIntent().getStringExtra("folderPath");
        max_count = getIntent().getIntExtra("maxCount", 1);
        mActivityName = getIntent().getStringExtra("activityName");
        getAllImageFilePathsByFolder(currentFolder);
        //Slidr.attach(this);

    }

    protected String[] getAllSelectedImagePath() {

        if (pathsVector != null && pathsVector.size() > 0) {
            String[] array = new String[pathsVector.size()];
            for (int i = 0; i < pathsVector.size(); i++) {
                array[i] = pathsVector.get(i);
            }
            return array;
        }

        return null;
    }

    protected void toClearThisImageStatus(final String filePath) {

        new Thread(new Runnable() {

            @Override
            public void run() {

                for (int i = 0; i < ImageFileList.size(); i++) {
                    if (ImageFileList.get(i).getFilePath().equals(filePath)) {

                        Message msg = new Message();
                        msg.what = TARGET_POS;
                        msg.arg1 = i;
                        handler.sendMessage(msg);
                        break;
                    }
                }

            }
        }).start();

    }

    public void dealAdd2ButtomLayout(View v) {

        int pos = (Integer) v.getTag();
        RelativeLayout layout = (RelativeLayout) v;
        View backView = layout.getChildAt(1);
        ImageView statusIV = (ImageView) layout.getChildAt(2);
        ImageFile imageFile = ImageFileList.get(pos);
        String filePath = imageFile.getFilePath();
        if (!imageFile.isSelected() && mSelectedImageList.size() >= max_count) {
            new TipDialog.Builder(this)
                    .setAutoDismiss(true)
                    .setTransparent(true)
                    .setTipText(
                            getResources().getString(R.string.pic_count_on_add))
                    .create().show();
            return;
        }
        if (mSelectedImageList.contains(filePath)) {
            imageFile.setSelected(false);
            statusIV.setVisibility(View.INVISIBLE);
            backView.setVisibility(View.INVISIBLE);
            mSelectedImageList.remove(filePath);
        } else {
            imageFile.setSelected(true);
            statusIV.setVisibility(View.VISIBLE);
            backView.setVisibility(View.VISIBLE);
            mSelectedImageList.add(filePath);
        }
        if (max_count == 1 && mSelectedImageList.size() == 1) {
            Intent i = new Intent();
            i.setClassName(this, mActivityName);
            i.putStringArrayListExtra("filePaths", mSelectedImageList);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);
        }
    }

    /**
     * 点击返回时，跳转至相册页面
     */
    private void backAction() {
        finish();
        overridePendingTransition(R.anim.main_translatexf100to0,
                R.anim.main_translatex0to100);
    }

    // 重写返回键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backAction();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    public static class ModifiedTimeComparator implements Comparator<File> {

        @Override
        public int compare(File lhs, File rhs) {
            return (int) (lhs.lastModified() - rhs.lastModified());
        }
    }

    /**
     * 获取指定路径下的所有图片文件。
     */
    private void getAllImageFilePathsByFolder(final String folderPath) {

        if (folderPath != null && !folderPath.equals("")) {

            new Thread(new Runnable() {

                @Override
                public void run() {

                    File folder = new File(folderPath);
                    String[] allFileNames = folder.list();
                    if (allFileNames == null || allFileNames.length == 0) {
                        return;
                    }

                    File subFile[] = folder.listFiles();

                    // 直接插入排序
                    for (int i = 1; i < subFile.length; i++) {
                        // 待插入元素
                        File temp = subFile[i];
                        int j;
                        for (j = i - 1; j >= 0; j--) {
                            if (subFile[j].lastModified() > temp.lastModified()) {
                                subFile[j + 1] = subFile[j];
                            } else {
                                break;
                            }
                        }
                        subFile[j + 1] = temp;
                    }

                    final ArrayList<ImageFile> list = new ArrayList<ImageFile>();

                    for (int i = subFile.length - 1; i >= 0; i--) {

                        String filePath = subFile[i].getAbsolutePath();

                        if (Utility.isImage(filePath)) {
                            if (mSelectedImageList.contains(filePath)) {
                                list.add(new ImageFile(filePath, true));
                            } else {
                                list.add(new ImageFile(filePath, false));
                            }

                        }
                    }

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            ImageFileList.addAll(list);
                            adapter.notifyDataSetChanged();

                        }
                    });
                }
            }).start();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 10) {
                byte[] b = data.getByteArrayExtra("bitmap");
                String filepath = FileUtils.getSavePath(this, b.length * 10);

                if (filepath.endsWith("/")) {
                    filepath = filepath + "DCIM/Camera";
                } else {
                    filepath = filepath + "/DCIM/Camera";
                }
            }
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_head_right:
                Intent i = new Intent();
                i.setClassName(this, mActivityName);
                i.putStringArrayListExtra("filePaths", mSelectedImageList);

                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                break;

            default:
                break;
        }
    }

}
