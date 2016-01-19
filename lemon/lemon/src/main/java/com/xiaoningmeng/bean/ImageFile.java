package com.xiaoningmeng.bean;

public class ImageFile {

	private String filePath;
	private boolean selected;

	public ImageFile(String filePath, boolean selectd) {
		this.filePath = filePath;
		this.selected = selectd;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

}
