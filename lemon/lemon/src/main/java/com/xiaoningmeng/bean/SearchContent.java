package com.xiaoningmeng.bean;

public class SearchContent {
	
	private String searchcontent;
	private long uptime;
	private int count;
	
	
	
	public SearchContent(String searchcontent) {

		this.searchcontent = searchcontent;
	}
	
	public SearchContent() {
		
	}

	public long getUptime() {
		return uptime;
	}
	public void setUptime(long uptime) {
		this.uptime = uptime;
	}

	public String getSearchcontent() {
		return searchcontent;
	}

	public void setSearchcontent(String searchcontent) {
		this.searchcontent = searchcontent;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	
	

}
