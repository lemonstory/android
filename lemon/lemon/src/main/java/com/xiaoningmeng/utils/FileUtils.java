package com.xiaoningmeng.utils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.util.EncodingUtils;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

public class FileUtils {

	public static String SDPATH() {
		return Environment.getExternalStorageDirectory() + "/";
	}

	public static String openAssetFile(Context ctx, String file_path) {

		String res = "";
		try {

			InputStream in = ctx.getResources().getAssets().open(file_path);
			int length = in.available();
			byte[] buffer = new byte[length];
			in.read(buffer);
			res = EncodingUtils.getString(buffer, "UTF-8");

		} catch (Exception e) {

			e.printStackTrace();
		}

		return res;
	}

	public static void copyFile(File src, File dst) throws IOException {
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst);

		// Transfer bytes from in to out
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}

	public static boolean deleteFile(File file) {
		boolean ret = false;
		if (file.exists()) {
			if (file.isFile()) {
				ret = file.delete();
			} else if (file.isDirectory()) {
				File files[] = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					FileUtils.deleteFile(files[i]);
				}
			}
		} else {
			ret = true;
		}
		return ret;
	}

	public static File createSDDir(String dirName) {
		File dir = new File(FileUtils.SDPATH() + dirName);
		boolean ret = dir.mkdirs();
		if (!ret) {
			return dir;
		}
		return null;
	}

	/*
	 * 保存text到手机内部存储
	 */
	public static boolean saveFileOnDeviceInternalStorage(Context context,
			String filename, String string) {
		try {
			FileOutputStream outputStream = context.openFileOutput(filename,
					Context.MODE_PRIVATE);
			outputStream.write(string.getBytes());
			outputStream.flush();
			outputStream.close();
			return true;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/*
	 * 从手机内部存储读取文本
	 */
	public static String readFileOnDeviceInternalStorage(Context context,
			String filename) {
		String content = "";
		try {
			FileInputStream inputStream = context.openFileInput(filename);
			int length = inputStream.available();
			byte[] buffer = new byte[length];
			inputStream.read(buffer);
			content = EncodingUtils.getString(buffer, "UTF-8");
			inputStream.close();
			return content;

		} catch (FileNotFoundException e) {

			e.printStackTrace();
			return null;

		} catch (IOException e) {

			e.printStackTrace();
			return null;
		}
	}

	public static byte[] readInputStream(InputStream inputStream) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;

		try {
			while ((len = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, len);
			}
			inputStream.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outputStream.toByteArray();
	}

	/**
	 * 根据byte数组，生成文件
	 */
	public static File getFile(Context context, byte[] bfile, String filePath,
			String fileName) {
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		File file = null;
		try {
			File dir = new File(filePath);
			if (!dir.exists()) {// 判断文件目录是否存在
				dir.mkdirs();
			}
			file = new File(filePath + "/" + fileName);
			// Toast.makeText(context, file.getAbsolutePath(),
			// Toast.LENGTH_LONG).show();
			if (!file.exists()) {
				file.createNewFile();
			}

			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(bfile);
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}

		return file;
	}

	public static String getFormatSize(double size) {
		double kiloByte = size / 1024;
		if (kiloByte < 1) {
			return size + "K";
		}

		double megaByte = kiloByte / 1024;
		if (megaByte < 1) {
			BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
			return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
					.toPlainString() + "K";
		}

		double gigaByte = megaByte / 1024;
		if (gigaByte < 1) {
			BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
			return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
					.toPlainString() + "M";
		}

		double teraBytes = gigaByte / 1024;
		if (teraBytes < 1) {
			BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
			return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
					.toPlainString() + "G";
		}
		BigDecimal result4 = new BigDecimal(teraBytes);
		return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
				+ "T";
	}

	/**
	 * 获取扩展SD卡存储目录
	 * 
	 * 如果有外接的SD卡，并且已挂载，则返回这个外置SD卡目录 否则：返回内置SD卡目录
	 * 
	 * @return
	 */
	public static String getSavePath(Context context, long saveSize) {
		String savePath = null;
		FilePathUtil.init(context);
		if (FilePathUtil.getExternaltStorageAvailableSpace() > saveSize) {
			savePath = FilePathUtil.getExternalStorageDirectory();
			File saveFile = new File(savePath);
			if (!saveFile.exists()) {
				saveFile.mkdirs();
			} else if (!saveFile.isDirectory()) {
				saveFile.delete();
				saveFile.mkdirs();
			}
		} else if (FilePathUtil.getSdcard2StorageAvailableSpace() > saveSize) {
			savePath = FilePathUtil.getSdcard2StorageDirectory();
			File saveFile = new File(savePath);
			if (!saveFile.exists()) {
				saveFile.mkdirs();
			} else if (!saveFile.isDirectory()) {
				saveFile.delete();
				saveFile.mkdirs();
			}
		} else if (FilePathUtil.getEmmcStorageAvailableSpace() > saveSize) {

			savePath = FilePathUtil.getEmmcStorageDirectory();
			File saveFile = new File(savePath);
			if (!saveFile.exists()) {
				saveFile.mkdirs();
			} else if (!saveFile.isDirectory()) {
				saveFile.delete();
				saveFile.mkdirs();
			}
		} else if (FilePathUtil.getOtherExternaltStorageAvailableSpace() > saveSize) {

			savePath = FilePathUtil.getOtherExternalStorageDirectory();
			File saveFile = new File(savePath);
			if (!saveFile.exists()) {
				saveFile.mkdirs();
			} else if (!saveFile.isDirectory()) {
				saveFile.delete();
				saveFile.mkdirs();
			}
		} else if (FilePathUtil.getInternalStorageAvailableSpace() > saveSize) {
			savePath = FilePathUtil.getInternalStorageDirectory()
					+ File.separator;
		} else {
			savePath = context.getCacheDir().getPath();
		}
		return savePath;
	}
}
