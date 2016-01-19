package com.xiaoningmeng.utils;

import java.io.File;

import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;

public class AudioUtils {

	public static Md5FileNameGenerator mGenerator = new Md5FileNameGenerator();

	public static boolean renameAndMoveFile(Context context, File srcFile,
			String videoUrl) {

		return srcFile.renameTo(new File(getVideoAbsoluteFileNameByUrl(context,
				videoUrl)));
	}

	/**
	 * 通过VideoUrl 得到视频的绝对路径
	 */
	public static final String getVideoAbsoluteFileNameByUrl(Context context,
			String videoUrl) {

		String fileName = mGenerator.generate(videoUrl);
		return getVideoFilePathByName(context, fileName);
	}

	/**
	 * 得到视频的绝对路径 通过FileName
	 * 
	 * @param fileName
	 * @return
	 */
	public static final String getVideoFilePathByName(Context context,
			String fileName) {

		return FileUtil.getSaveDir(context) + File.separator + fileName
				+ ".mp3";
	}

	public static final String generateByUrl(String videoUrl) {

		return mGenerator.generate(videoUrl);
	}

}
