package com.xiaoningmeng.bean;

import java.io.Serializable;

public class ShareBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private String url;
	private String iconUrl;
	private String title;
	private String text;

	public ShareBean(String title,String text, String iconUrl, String url) {
		this.title = title;
		this.text = text;
		this.url = url;
		this.iconUrl = iconUrl;
	}



	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "ShareBean{" +
				"url='" + url + '\'' +
				", iconUrl='" + iconUrl + '\'' +
				", title='" + title + '\'' +
				", text='" + text + '\'' +
				'}';
	}
}
