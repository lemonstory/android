package com.xiaoningmeng.bean;

import android.util.Log;

import java.io.Serializable;

public class ShareBean implements Serializable {

	private static final long serialVersionUID = 1L;

	public ShareBean(String title, String iconUrl, String url) {
		this.title = title;
		this.url = url;
		this.iconUrl = iconUrl;
	}



	private String url;
	private String iconUrl;
	private String title;


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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	

}
