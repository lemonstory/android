package com.xiaoningmeng.bean;

import java.io.Serializable;

public class ShareBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private String url;
	private String iconUrl;
	private String title;
	private String text;
	private String musicUrl;

	public ShareBean(String title,String text, String iconUrl,String musicUrl, String url) {
		this.title = title;
		this.text = text;
		this.url = url;
		this.iconUrl = iconUrl;
		this.musicUrl = musicUrl;
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

	public String getMusicUrl() {
		return musicUrl;
	}

	public void setMusicUrl(String musicUrl) {
		this.musicUrl = musicUrl;
	}

	@Override
	public String toString() {
		return "ShareBean{" +
				"url='" + url + '\'' +
				", iconUrl='" + iconUrl + '\'' +
				", musicUrl='" + musicUrl + '\'' +
				", title='" + title + '\'' +
				", text='" + text + '\'' +
				'}';
	}
}
