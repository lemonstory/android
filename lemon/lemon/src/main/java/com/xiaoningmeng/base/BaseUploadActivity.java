/**
 * @author huangYanbin
 */
package com.xiaoningmeng.base;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.xiaoningmeng.utils.DebugUtils;
import com.xiaoningmeng.ChooImagePathActivity;
import com.xiaoningmeng.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public abstract class BaseUploadActivity extends BaseActivity {

	public static final int REQUEST_CODE_PICK_IMAGE = 5;
	public static final int REQUEST_CODE_CAPTURE_CAMEIA = 6;
	private String capturePath = null;
	private String SAVED_IMAGE_DIR_PATH = Environment
			.getExternalStorageDirectory() + "/DCIM/Camera";// 图片的存储目录
	private DialogPlus dialogPlus;

	/**
	 * 选择图片
	 * 
	 * @param selectedMaxCount
	 *            最多选择图片的数量
	 */
	protected void selectPic(final int selectedMaxCount) {

		dialogPlus = DialogPlus.newDialog(this).setGravity(Gravity.BOTTOM)
				.setContentHolder(new ViewHolder(R.layout.dialog_select_pic))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(DialogPlus dialog, View view) {
						switch (view.getId()) {
						case R.id.tv_dialog_cancel:
							dialogPlus.dismiss();
							break;
						case R.id.tv_dialog_photo:
							getImageFromCamera();
							dialogPlus.dismiss();
							break;
						case R.id.tv_dialog_pics:
							Intent i = new Intent(BaseUploadActivity.this,
									ChooImagePathActivity.class);
							i.putExtra("activityName", BaseUploadActivity.this
									.getComponentName().getClassName());
							i.putExtra("maxCount", selectedMaxCount);
							startActivityForNew(i);
							dialogPlus.dismiss();
							break;
						}
					}
				}).create();
		dialogPlus.show();
	}

	protected void getImageFromAlbum() {

		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");// 相片类型
		startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
	}

	protected void getImageFromCamera() {
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			Intent getImageByCamera = new Intent(
					"android.media.action.IMAGE_CAPTURE");
			String out_file_path = SAVED_IMAGE_DIR_PATH;
			File dir = new File(out_file_path);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			capturePath = SAVED_IMAGE_DIR_PATH + System.currentTimeMillis()+ ".jpg";
			getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(new File(capturePath)));
			getImageByCamera.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
			startActivityForResult(getImageByCamera,
					REQUEST_CODE_CAPTURE_CAMEIA);
		} else {
			Toast.makeText(getApplicationContext(), "请确认已经插入SD卡",
					Toast.LENGTH_LONG).show();
		}
	}

	protected void getCompressImageFromCamera() {

		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			Intent getImageByCamera = new Intent(
					"android.media.action.IMAGE_CAPTURE");
			startActivityForResult(getImageByCamera,
					REQUEST_CODE_CAPTURE_CAMEIA);
		} else {
			Toast.makeText(getApplicationContext(), "请确认已经插入SD卡",
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == REQUEST_CODE_PICK_IMAGE) {
			Uri uri = data.getData();
		} else if (requestCode == REQUEST_CODE_CAPTURE_CAMEIA) {
			if (capturePath != null) {
				File picFile = new File(capturePath);
				if (picFile.exists()) {
					DebugUtils.d("photo getPicture path----"
							+ picFile.getAbsolutePath());
					getPhoto(picFile.getAbsolutePath());
				} else {
					error();
				}
			} else {
				if (data != null) {
					Uri uri = data.getData();
					if (uri == null) {
						Bundle bundle = data.getExtras();
						if (bundle != null) {
							Bitmap photo = (Bitmap) bundle.get("data");
							capturePath = SAVED_IMAGE_DIR_PATH
									+ System.currentTimeMillis() + ".jpg";
							saveImage(photo, capturePath);
							File picFile = new File(capturePath);
							if (picFile.exists()) {
								DebugUtils.d("photo getPicture path----"
										+ picFile.getAbsolutePath());
								getPhoto(picFile.getAbsolutePath());
							} else {
								error();
							}
						} else {
							error();
							return;
						}
					} else {
						String[] proj = { MediaStore.Images.Media.DATA };
						Cursor actualimagecursor = managedQuery(uri, proj,
								null, null, null);
						int actual_image_column_index = actualimagecursor
								.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
						actualimagecursor.moveToFirst();
						String img_path = actualimagecursor
								.getString(actual_image_column_index);
						getPhoto(img_path);
					}
				}
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	public static boolean saveImage(Bitmap photo, String spath) {
		try {
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(spath, false));
			photo.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	protected void onNewIntent(Intent intent) {

		List<String> imgPaths = intent.getStringArrayListExtra("filePaths");
		DebugUtils.d("photo getPicture imgPaths----");
		if(imgPaths != null)
			getImages(imgPaths);
		super.onNewIntent(intent);
	}

	/**
	 * 从拍照返回照片的路径
	 * 
	 * @param filePath
	 */
	public abstract void getPhoto(String filePath);

	/**
	 * 从文件目录中选择图片返回路径集合
	 */
	public abstract void getImages(List<String> imagePaths);

	/**
	 * 返回图片发生异常
	 */
	public void error() {

		//Toast.makeText(this, "无法获取到拍照图片", Toast.LENGTH_SHORT).show();
	}
}
