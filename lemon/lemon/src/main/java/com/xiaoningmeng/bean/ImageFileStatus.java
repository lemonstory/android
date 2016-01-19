package com.xiaoningmeng.bean;

public class ImageFileStatus {
	/* 圖片文件地址 */
	private String filePath;
	/* 圖片文件被選擇的次數 */
	private int selectedCount = 0;

	public ImageFileStatus(String filePath, int count) {
		this.filePath = filePath;
		this.selectedCount = count;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public int getSelectedCount() {
		return selectedCount;
	}

	public void setSelectedCount(int selectedCount) {
		this.selectedCount = selectedCount;
	}

}
