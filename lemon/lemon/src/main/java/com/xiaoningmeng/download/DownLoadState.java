package com.xiaoningmeng.download;

/**
 * 下载状态
 * */
public class DownLoadState {
	/* 下载中 */
	public static final int DOWN_UPDATE = 0x1;
	/* 准备网络连接 */
	public static final int DOWN_CONNECT = 0x2;
	/* 开始 */
	public static final int DOWN_START = 0x3;
	/* 停止 */
	public static final int DOWN_PAUSE = 0x4;
	/* 下载出错 */
	public static final int DOWN_ERROR = 0x5;
	/* 取消下载 */
	public static final int DOWN_CANCEL = 0x6;
	/* 不支持的文件类型 */
	public static final int DOWN_UNKOWNVERSION = 0x7;
	/* 文件被删除 */
	public static final int FILE_DELETE = 0x8;
	/* 成功 */
	public static final int DOWN_SUC = 0x9;
}
