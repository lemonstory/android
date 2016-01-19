package com.xiaoningmeng.bean;

public class ListenerAlbum {

	private String uimid;
	private String playstoryid;
	private String albumid;
	private String uptime;
	private int playtimes;
	protected AlbumInfo albuminfo;
	private Story storyinfo;
	
	

	public ListenerAlbum(String uid, String storyid, String albumid,
			String uptime,int playtimes, AlbumInfo albuminfo) {
		this.uimid = uid;
		this.playstoryid = storyid;
		this.albumid = albumid;
		this.uptime = uptime;
		this.albuminfo = albuminfo;
		this.playtimes = playtimes;
	}

	
	public ListenerAlbum() {
	}


	public String getUid() {
		return uimid;
	}

	public void setUid(String uid) {
		this.uimid = uid;
	}



	public String getPlaystoryid() {
		return playstoryid;
	}


	public void setPlaystoryid(String playstoryid) {
		this.playstoryid = playstoryid;
	}


	public String getAlbumid() {
		return albumid;
	}

	public void setAlbumid(String albumid) {
		this.albumid = albumid;
	}

	public String getUptime() {
		return uptime;
	}

	public void setUptime(String uptime) {
		this.uptime = uptime;
	}

	public AlbumInfo getAlbuminfo() {
		return albuminfo;
	}

	public void setAlbuminfo(AlbumInfo albuminfo) {
		this.albuminfo = albuminfo;
	}

	
	public int getPlaytimes() {
		return playtimes;
	}


	public void setPlaytimes(int playtimes) {
		this.playtimes = playtimes;
	}
}
