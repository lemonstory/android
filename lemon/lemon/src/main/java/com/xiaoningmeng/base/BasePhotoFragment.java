
package com.xiaoningmeng.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.xiaoningmeng.R;
import com.xiaoningmeng.utils.FileUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;



public class BasePhotoFragment extends BaseFragment{

    public File mCurrentPhotoFile;
    private File PHOTO_DIR = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera");// 图片的存储目录
    public IUploadCall call;
    public boolean zoom = false;
    public boolean isEditWithFilter = false;
    protected DialogInterface.OnClickListener mOptionOnClickListener;

    public static final int PHOTOHRAPH = 0;// 拍照
    public static final int PHOTOZOOM = 2; // 缩放
    public static final String IMAGE_UNSPECIFIED = "image/*";

    protected static final int REQUEST_PICK_IMAGE = 4;
    private static final int REQUEST_PICK = 0;
    private static final int REQUEST_CAPTRUE = 1;
    private static final int REQUEST_CROP = 2;
    private static final int REQUEST_FILTER = 3;
    private static final int REQUEST_CAMERA_FILTER = 4;

    private DialogPlus dialogPlus;

    /**
     * 选择图片
     *
     * @param selectedMaxCount
     *            最多选择图片的数量
     */
    protected void selectPic(IUploadCall call,boolean isCropPhoto) {
        modifyPicture(call,isCropPhoto);
        dialogPlus = DialogPlus.newDialog(getActivity()).setGravity(Gravity.BOTTOM)
                .setContentHolder(new ViewHolder(R.layout.dialog_select_pic))
                .setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        switch (view.getId()) {
                            case R.id.tv_dialog_cancel:
                                dialogPlus.dismiss();
                                break;
                            case R.id.tv_dialog_photo:
                                getPicFromCapture();
                                dialogPlus.dismiss();
                                break;
                            case R.id.tv_dialog_pics:
                                getPicFromContent();
                                dialogPlus.dismiss();
                                break;
                        }
                    }
                }).create();
        dialogPlus.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_CANCELED) {
            return;
        }
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_PICK:// 从相册取得照片

                    if (data != null) {
                        if (zoom) {
                            Uri uri = data.getData();
                            if(uri!=null){
                                doCropPhoto(data.getData());// 对照片进行裁剪
                            }else{
                                Toast.makeText(getActivity(), "获取图片失败", Toast.LENGTH_SHORT).show();
                            }
                        } else if (isEditWithFilter ) {
                            goToFilter(data.getData());
                        } else {
                            Uri uri = data.getData();
                            String path = getPath(uri);
                            File file = new File(path);
                            if (file.exists() && call != null) {
                                // file.delete();
                                call.gpuback(file);
                            }
                        }
                    }
                    break;
                case REQUEST_CAPTRUE:// 从照相机取得照片
                    if (zoom) {
                        if(mCurrentPhotoFile != null){
                            doCropPhoto(Uri.fromFile(mCurrentPhotoFile));// 对照片进行裁剪
                        }else{
                            Toast.makeText(getActivity(), "获取图片失败", Toast.LENGTH_SHORT).show();
                        }
                    } else if (isEditWithFilter) {
                        goToFilter(Uri.fromFile(mCurrentPhotoFile));
                    } else {
                        if (mCurrentPhotoFile.exists() && call != null) {
                            // file.delete();
                            call.gpuback(mCurrentPhotoFile);
                        }
                    }
                    break;
                case REQUEST_CROP:// 裁剪完照片返回
                    if (isEditWithFilter ) {
                        goToFilter(Uri.fromFile(mCurrentPhotoFile));// 调用滤镜进行编辑
                    } else {
                    	/*byte[] b = data.getByteArrayExtra("bitmap");
                    	String filepath = FileUtils.getSavePath(this,b.length*5);
                    	if(filepath.endsWith("/")){
                    		filepath = filepath+"DCIM/Camera";
                    	}else{
                    		filepath = filepath+"/DCIM/Camera";
                    	}
                    	File file = FileUtils.getFile(this,b, filepath, getPhotoFileName());*/
                        call.gpuback(mCurrentPhotoFile);
                    }
                    break;
                case REQUEST_FILTER:// 滤镜编辑完返回
                    if (data == null)
                        return;
                    Uri uri = data.getData();
                    String path = getPath(uri);
                    File file = new File(path);
                    if (file.exists() && call != null) {
                        // file.delete();
                        call.gpuback(file);
                    }
                    break;
                case REQUEST_CAMERA_FILTER://实时滤镜编辑完返回
                    if (data == null){
                        return;
                    }else {
                        Uri dataUri = data.getData();
                        String dataPath = getPath(dataUri);
                        File dataFile = new File(dataPath);
                        if (dataFile.exists() && call != null) {
                            // file.delete();
                            call.gpuback(dataFile);
                        }
                    }
                    break;
                default:
                    break;
            }

        }
    }

    public void modifyPicture(IUploadCall call) {
        this.call = call;


    }

    public void modifyPicture(IUploadCall call, boolean zoom) {
        this.call = call;
        this.zoom = zoom;
    }

    /**
     * 选择是否调用裁剪和滤镜
     *
     * @param call             回调函数
     * @param isCropPhoto      是否调用裁剪，默认不调用
     * @param isEditWithFilter 是否调用滤镜，默认不调用
     */
    public void modifyPicture(IUploadCall call, boolean isCropPhoto, boolean isEditWithFilter) {
        this.call = call;
        this.zoom = isCropPhoto;
        this.isEditWithFilter = isEditWithFilter;
    }

    /**
     * 选择是否调用裁剪和滤镜，并且自定义菜单和操作
     *
     * @param title            标题
     * @param texts            选项文本
     * @param listener         选项操作
     * @param call             选取完图片后的回调
     * @param isCropPhoto      是否裁减
     * @param isEditWithFilter 是否调用滤镜
     */
    protected void modifyPicture(String title, String[] texts, DialogInterface.OnClickListener listener,
                                 IUploadCall call, boolean isCropPhoto, boolean isEditWithFilter) {
        this.call = call;
        this.zoom = isCropPhoto;
        this.isEditWithFilter = isEditWithFilter;
        new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setItems(texts, listener)
                .show();
    }

    public interface IUploadCall {
        void gpuback(File file);
    }

    /**
     * 获取uri中的本地地址
     *
     * @param uri
     * @return
     */
    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().managedQuery(uri, projection, null, null, null);
        try {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    // 相册
    protected void getPicFromContent() {
        PHOTO_DIR = new File(FileUtils.getSavePath(getActivity(),1024*1024)+"/DCIM/Camera");
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            PHOTO_DIR.mkdir();
            mCurrentPhotoFile = new File(PHOTO_DIR, getPhotoFileName()); // 用当前时间给取得的图片命名
            try {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                getActivity().startActivityForResult(intent, REQUEST_PICK);
            } catch (Exception e) {
                Toast.makeText(getActivity(), "获取图片失败", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(getActivity(), "获取图片失败", Toast.LENGTH_SHORT).show();
        }
    }




    protected void getPicFromCapture() {
        PHOTO_DIR = new File(FileUtils.getSavePath(getActivity(),1024*1024)+"/DCIM/Camera");
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            PHOTO_DIR.mkdir();
            mCurrentPhotoFile = new File(PHOTO_DIR, getPhotoFileName()); // 用当前时间给取得的图片命名

            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(mCurrentPhotoFile));
            getActivity().startActivityForResult(intent, REQUEST_CAPTRUE);

        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.sd_disable), Toast.LENGTH_SHORT).show();
        }
    }


    private void doCropPhoto(Uri uri) {
        try {
            // 启动gallery去剪辑这个照片
            Intent intent = getCropImageIntent(uri);
            getActivity().startActivityForResult(intent, REQUEST_CROP);
        } catch (Exception e) {
            Toast.makeText(getActivity(),"获取图片失败", Toast.LENGTH_SHORT).show();
        }
    }

    protected void goToFilter(Uri uri) {

    }

    public Intent getCropImageIntent(Uri photoUri) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(photoUri, "image/*");
            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("outputX",330);
            intent.putExtra("outputY",330);
            intent.putExtra("scale",true);
            intent.putExtra("scaleUpIfNeeded", true);
            intent.putExtra("return-data", false);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(mCurrentPhotoFile));
            intent.putExtra("outputFormat",
                    Bitmap.CompressFormat.JPEG.toString());
            return intent;
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.sd_disable), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    // 用当前时间给取得的图片命名
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
        // return "1.jpg";
    }

    protected void goToCameraFilter() {
    }
}
