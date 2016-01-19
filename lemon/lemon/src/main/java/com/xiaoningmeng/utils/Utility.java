package com.xiaoningmeng.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.widget.Toast;

public class Utility {

	public static final int MIN_PIC_SIZE = 20;
	public static final int MAX_PIC_SIZE = 10 * 1024;

	/**
	 * 判断SD卡是否可用
	 */
	public static boolean isSDcardOK() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	/**
	 * 获取SD卡跟路径。SD卡不可用时，返回null
	 */
	public static String getSDcardRoot() {
		if (isSDcardOK()) {
			return Environment.getExternalStorageDirectory().getAbsolutePath();
		}

		return null;
	}

	public static void showToast(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	public static void showToast(Context context, int msgId) {
		Toast.makeText(context, msgId, Toast.LENGTH_SHORT).show();
	}

	/** 获取字符串中某个字符串出现的次数。 */
	public static int countMatches(String res, String findString) {
		if (res == null) {
			return 0;
		}

		if (findString == null || findString.length() == 0) {
			throw new IllegalArgumentException(
					"The param findString cannot be null or 0 length.");
		}

		return (res.length() - res.replace(findString, "").length())
				/ findString.length();
	}

	/** 判断该文件是否是一个有效的图片。 */
	public static boolean isImage(String fileName) {

		if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")
				|| fileName.endsWith(".png")) {

			File file = new File(fileName);
			if (file != null && file.exists()) {
				long fileSize = file.length() / 1024;
				if (fileSize > MIN_PIC_SIZE && fileSize < MAX_PIC_SIZE) {
					return true;
				}
			}
		}
		return false;
	}

	// 计算图片的缩放值
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;

		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}

	/*
	 * // 根据路径获得图片并压缩，返回bitmap用于显示 public static Bitmap getSmallBitmap(Context
	 * context, String filePath) {
	 * 
	 * String endStr =
	 * filePath.substring(filePath.lastIndexOf("."),filePath.length());
	 * 
	 * int degree = readPictureDegree(filePath);
	 * 
	 * final BitmapFactory.Options options = new BitmapFactory.Options();
	 * options.inJustDecodeBounds = true; BitmapFactory.decodeFile(filePath,
	 * options); // Calculate inSampleSize options.inSampleSize =
	 * calculateInSampleSize(options, 480, 800); // Decode bitmap with
	 * inSampleSize set options.inJustDecodeBounds = false;
	 * 
	 * Bitmap bm = BitmapFactory.decodeFile(filePath, options);
	 * 
	 * bm = rotaingImage(degree, bm);
	 * 
	 * File file = new File(context.getCacheDir() + "/temp" + endStr);
	 * 
	 * try { FileOutputStream fileOutputStream = new FileOutputStream(file);
	 * bm.compress(Bitmap.CompressFormat.JPEG, 60, fileOutputStream); } catch
	 * (FileNotFoundException e) { e.printStackTrace(); } return bm; }
	 */

	// 根据路径获得图片并压缩
	public static String getSmallImgePath(Context context, String filePath) {

		if (filePath == null || filePath.length() == 0) {
			return "";
		}
		String endStr = filePath.substring(filePath.lastIndexOf("."),
				filePath.length());

		File file = null;

		try {

			int degree = readPictureDegree(filePath);

			BitmapFactory.Options options = new BitmapFactory.Options();

			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(filePath, options);
			int inSampleSize = calculateInSampleSize(options, 480, 800);
			if (inSampleSize >= 4) {
				inSampleSize++;
			}
			options.inSampleSize = inSampleSize;
			options.inJustDecodeBounds = false;
			Bitmap bm = BitmapFactory.decodeFile(filePath, options);

			if (degree != 0) {
				bm = rotaingImage(degree, bm);
			}

			if (bm != null) {
				int width = Math.min(bm.getWidth(), bm.getHeight());
				bm = centerSquareScaleBitmap(bm, width);
				file = new File(context.getCacheDir() + "/temp"
						+ System.currentTimeMillis() + endStr);
				FileOutputStream fileOutputStream = new FileOutputStream(file);

				if (endStr.equalsIgnoreCase(".png")) {
					bm.compress(Bitmap.CompressFormat.PNG, 80, fileOutputStream);
				} else {
					bm.compress(Bitmap.CompressFormat.JPEG, 80,
							fileOutputStream);
				}

				fileOutputStream.close();
				bm.recycle();

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (file != null) {
			return file.getAbsolutePath();
		}
		return null;

	}

	/**
	 * 
	 * @param bitmap
	 *            原图
	 * @param edgeLength
	 *            希望得到的正方形部分的边长
	 * @return 缩放截取正中部分后的位图。
	 */
	public static Bitmap centerSquareScaleBitmap(Bitmap bitmap, int edgeLength) {
		if (null == bitmap || edgeLength <= 0) {
			return null;
		}

		Bitmap result = bitmap;
		int widthOrg = bitmap.getWidth();
		int heightOrg = bitmap.getHeight();

		if (widthOrg >= edgeLength && heightOrg >= edgeLength) {
			// 压缩到一个最小长度是edgeLength的bitmap
			int longerEdge = (int) (edgeLength * Math.max(widthOrg, heightOrg) / Math
					.min(widthOrg, heightOrg));
			int scaledWidth = widthOrg > heightOrg ? longerEdge : edgeLength;
			int scaledHeight = widthOrg > heightOrg ? edgeLength : longerEdge;
			Bitmap scaledBitmap;

			try {
				scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth,
						scaledHeight, true);
			} catch (Exception e) {
				return null;
			}

			// 从图中截取正中间的正方形部分。
			int xTopLeft = (scaledWidth - edgeLength) / 2;
			int yTopLeft = (scaledHeight - edgeLength) / 2;

			try {
				result = Bitmap.createBitmap(scaledBitmap, xTopLeft, yTopLeft,
						edgeLength, edgeLength);
				// scaledBitmap.recycle();
			} catch (Exception e) {
				return null;
			}
		}

		return result;
	}

	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	/*
	 * public static Bitmap adjustPhotoRotation(Bitmap bm, final int
	 * orientationDegree) {
	 * 
	 * Matrix m = new Matrix(); m.setRotate(orientationDegree, (float)
	 * bm.getWidth() / 2, (float) bm.getHeight() / 2); float targetX, targetY;
	 * if (orientationDegree == 90) { targetX = bm.getHeight(); targetY = 0; }
	 * else { targetX = bm.getHeight(); targetY = bm.getWidth(); }
	 * 
	 * final float[] values = new float[9]; m.getValues(values);
	 * 
	 * float x1 = values[Matrix.MTRANS_X]; float y1 = values[Matrix.MTRANS_Y];
	 * 
	 * m.postTranslate(targetX - x1, targetY - y1);
	 * 
	 * Bitmap bm1 = Bitmap.createBitmap(bm.getHeight(), bm.getWidth(),
	 * Bitmap.Config.ARGB_8888); Paint paint = new Paint(); Canvas canvas = new
	 * Canvas(bm1); canvas.drawBitmap(bm, m, paint);
	 * 
	 * return bm1; }
	 */

	public static Bitmap rotaingImage(int angle, Bitmap bitmap) {

		Bitmap resizedBitmap = bitmap;
		try {
			// 旋转图片 动作
			Matrix matrix = new Matrix();
			;
			matrix.postRotate(angle);

			// 创建新的图片
			resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
					bitmap.getWidth(), bitmap.getHeight(), matrix, true);

			return resizedBitmap;

		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}

		return resizedBitmap;

	}

}
