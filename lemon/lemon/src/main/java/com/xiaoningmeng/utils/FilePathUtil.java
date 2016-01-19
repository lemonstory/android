package com.xiaoningmeng.utils;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.os.StatFs;
import android.text.TextUtils;
import android.widget.Toast;

public class FilePathUtil {
	static Context context;

	static void init(Context cxt) {
		context = cxt;
	}

	/** get external Storage available space */
	public static long getExternaltStorageAvailableSpace() {
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return 0;
		}
		File path = Environment.getExternalStorageDirectory();
		StatFs statfs = new StatFs(path.getPath());
		long blockSize = statfs.getBlockSize();
		long availableBlocks = statfs.getAvailableBlocks();
		return blockSize * availableBlocks;
	}

	/** get external Storage available space */
	public static long getExternaltStorageTotalSpace() {
		File path = Environment.getExternalStorageDirectory();
		StatFs statfs = new StatFs(path.getPath());
		long blockSize = statfs.getBlockSize();
		long totalBlocks = statfs.getBlockCount();
		return blockSize * totalBlocks;
	}

	/** get sdcard2 external Storage available space */
	public static long getSdcard2StorageAvailableSpace() {
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return 0;
		}
		String path = getSdcard2StorageDirectory();
		File file = new File(path);
		if (!file.exists())
			return 0;
		StatFs statfs = new StatFs(path);
		long blockSize = statfs.getBlockSize();
		long availableBlocks = statfs.getAvailableBlocks();
		return blockSize * availableBlocks;
	}

	/** get sdcard2 external Storage total space */
	public static long getSdcard2StorageTotalSpace() {
		String path = getSdcard2StorageDirectory();
		File file = new File(path);
		if (!file.exists())
			return 0;
		StatFs statfs = new StatFs(path);
		long blockSize = statfs.getBlockSize();
		long totalBlocks = statfs.getAvailableBlocks();
		return blockSize * totalBlocks;
	}

	/** get EMMC internal Storage available space */
	public static long getEmmcStorageAvailableSpace() {
		String path = getEmmcStorageDirectory();
		File file = new File(path);
		if (!file.exists())
			return 0;
		StatFs statfs = new StatFs(path);
		long blockSize = statfs.getBlockSize();
		long availableBlocks = statfs.getAvailableBlocks();

		return blockSize * availableBlocks;
	}

	/** get EMMC internal Storage available space */
	public static long getEmmcStorageTotalSpace() {
		String path = getEmmcStorageDirectory();
		File file = new File(path);
		if (!file.exists())
			return 0;
		StatFs statfs = new StatFs(path);
		long blockSize = statfs.getBlockSize();
		long totalBlocks = statfs.getBlockCount();

		return blockSize * totalBlocks;
	}

	static FstabReader fsReader = null;

	/** get other external Storage available space */
	public static long getOtherExternaltStorageAvailableSpace() {
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return 0;
		}
		if (otherExternalStorageState == kOtherExternalStorageStateUnable)
			return 0;
		if (otherExternalStorageDirectory == null) {
			getOtherExternalStorageDirectory();
		}
		if (otherExternalStorageDirectory == null)
			return 0;
		StatFs statfs = new StatFs(otherExternalStorageDirectory);
		long blockSize = statfs.getBlockSize();
		long availableBlocks = statfs.getAvailableBlocks();
		return blockSize * availableBlocks;
	}

	private static String otherExternalStorageDirectory = null;
	private static int kOtherExternalStorageStateUnknow = -1;
	private static int kOtherExternalStorageStateUnable = 0;
	private static int kOtherExternalStorageStateIdle = 1;
	private static int otherExternalStorageState = kOtherExternalStorageStateUnknow;

	public static String getOtherExternalStorageDirectory() {
		if (otherExternalStorageState == kOtherExternalStorageStateUnable)
			return null;
		if (otherExternalStorageState == kOtherExternalStorageStateUnknow) {
			FstabReader fsReader = new FstabReader();
			if (fsReader.size() <= 0) {
				otherExternalStorageState = kOtherExternalStorageStateUnable;
				return null;
			}
			List<StorageInfo> storages = fsReader.getStorages();
			/* 对于可用空间小于100M的挂载节点忽略掉 */
			long availableSpace = 100 << (20);
			String path = null;
			for (int i = 0; i < storages.size(); i++) {
				StorageInfo info = storages.get(i);
				if (info.getAvailableSpace() > availableSpace) {
					availableSpace = info.getAvailableSpace();
					path = info.getPath();
				}
			}
			otherExternalStorageDirectory = path;
			if (otherExternalStorageDirectory != null) {
				otherExternalStorageState = kOtherExternalStorageStateIdle;
			} else {
				otherExternalStorageState = kOtherExternalStorageStateUnable;
			}
		}
		return otherExternalStorageDirectory;
	}

	public static long getInternalStorageAvailableSpace() {
		long blockSize = 0;
		long availableBlocks = 0;
		try {
			String path = getInternalStorageDirectory();
			StatFs stat = new StatFs(path);
			blockSize = stat.getBlockSize();
			availableBlocks = stat.getAvailableBlocks();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return blockSize * availableBlocks;
	}

	/**
	 * 获取手机内部总的存储空间
	 * 
	 * @return
	 */
	public static long getInternalStorageTotalSpace() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return totalBlocks * blockSize;
	}

	public final static String getExternalStorageDirectory() {
		return Environment.getExternalStorageDirectory() + File.separator + "";
	}

	public final static String getExternalStoragePublicDirectory(String type) {
		return Environment.getExternalStoragePublicDirectory(type)
				.getAbsolutePath();
	}

	public final static String getSdcard2StorageDirectory() {
		return "/mnt/sdcard2";
	}

	public final static String getEmmcStorageDirectory() {
		return "/mnt/emmc";
	}

	private static String externalStoragePrivateDirectory;

	static String getExternalPrivateFilesDirectory() {
		if (externalStoragePrivateDirectory == null)
			externalStoragePrivateDirectory = context.getExternalFilesDir(null)
					.getAbsolutePath();
		return externalStoragePrivateDirectory;
	}

	private static String internalStorageDirectory = "/mnt/internal";

	public final static String getInternalStorageDirectory() {
		try {

			if (TextUtils.isEmpty(internalStorageDirectory)) {
				File file = context.getFilesDir();
				if (file != null) {
					internalStorageDirectory = file.getAbsolutePath();
					if (!file.exists())
						file.mkdirs();
					String shellScript = "chmod 705 "
							+ internalStorageDirectory;
					runShellScriptForWait(shellScript);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return internalStorageDirectory;
	}

	public static boolean isInternalStoragePath(String path) {
		String rootPath = getInternalStorageDirectory();
		if (path != null && path.startsWith(rootPath))
			return true;
		return false;
	}

	public static String getFileName(String file) {
		if (file == null)
			return null;
		int index = file.lastIndexOf("/");
		return file.substring(index + 1);
	}

	public static boolean runShellScriptForWait(final String cmd)
			throws SecurityException {
		ShellThread thread = new ShellThread(cmd);
		thread.setDaemon(true);
		thread.start();
		int k = 0;
		while (!thread.isReturn() && k++ < 20) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (k >= 20) {
			thread.interrupt();
		}
		return thread.isSuccess();
	}

	/** 用于执行shell脚本的线程 */
	private static class ShellThread extends Thread {
		private boolean isReturn;
		private boolean isSuccess;
		private String cmd;

		public boolean isReturn() {
			return isReturn;
		}

		public boolean isSuccess() {
			return isSuccess;
		}

		/**
		 * @param cmd
		 *            shell命令内容
		 * @param isReturn
		 *            线程是否已经返回
		 * @param isSuccess
		 *            Process是否执行成功
		 */
		public ShellThread(String cmd) {
			this.cmd = cmd;
		}

		@Override
		public void run() {
			try {
				Runtime runtime = Runtime.getRuntime();
				Process proc;
				try {
					proc = runtime.exec(cmd);
					isSuccess = (proc.waitFor() == 0);
				} catch (IOException e) {
					e.printStackTrace();
				}
				isSuccess = true;
			} catch (InterruptedException e) {
			}
			isReturn = true;
		}

	}

	public static class FstabReader {
		public FstabReader() {
			init();
		}

		public int size() {
			return storages == null ? 0 : storages.size();
		}

		public List<StorageInfo> getStorages() {
			return storages;
		}

		final List<StorageInfo> storages = new ArrayList<StorageInfo>();

		public void init() {
			File file = new File("/system/etc/vold.fstab");
			if (file.exists()) {
				FileReader fr = null;
				BufferedReader br = null;
				try {
					fr = new FileReader(file);
					if (fr != null) {
						br = new BufferedReader(fr);
						String s = br.readLine();
						while (s != null) {
							if (s.startsWith("dev_mount")) {
								/* "\s"转义符匹配的内容有：半/全角空格 */
								String[] tokens = s.split("\\s");
								String path = tokens[2]; // mount_point
								StatFs stat = new StatFs(path);

								if (null != stat
										&& stat.getAvailableBlocks() > 0) {

									long availableSpace = stat
											.getAvailableBlocks()
											* stat.getBlockSize();
									long totalSpace = stat.getBlockCount()
											* stat.getBlockSize();
									StorageInfo storage = new StorageInfo(path,
											availableSpace, totalSpace);
									storages.add(storage);
								}
							}
							s = br.readLine();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (fr != null)
						try {
							fr.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					if (br != null)
						try {
							br.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}
			}
		}
	}

	static class StorageInfo implements Comparable<StorageInfo> {
		private String path;
		private long availableSpace;
		private long totalSpace;

		StorageInfo(String path, long availableSpace, long totalSpace) {
			this.path = path;
			this.availableSpace = availableSpace;
			this.totalSpace = totalSpace;
		}

		@Override
		public int compareTo(StorageInfo another) {
			if (null == another)
				return 1;

			return this.totalSpace - another.totalSpace > 0 ? 1 : -1;
		}

		long getAvailableSpace() {
			return availableSpace;
		}

		long getTotalSpace() {
			return totalSpace;
		}

		String getPath() {
			return path;
		}
	}

}
