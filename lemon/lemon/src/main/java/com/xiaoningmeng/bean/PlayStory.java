package com.xiaoningmeng.bean;

import org.litepal.crud.DataSupport;

public class PlayStory extends DataSupport {

	private long addTime;
	private String title;
	private String intro;
	private String times;
	private String file_size;
	private String mediapath;
	private String cover;
	private String storyId;
	private String albumid;
	public boolean isError;
	private String playcover;

	public PlayStory() {

	}

	public PlayStory(Story story) {
		this.storyId = story.getStoryId();
		this.title = story.getTitle();
		this.intro = story.getIntro();
		this.times = story.getTimes();
		this.cover = story.getCover();
		this.albumid = story.getAlbum_id();
		this.file_size = story.getFile_size();
		this.mediapath = story.getMediapath();
		this.addTime = System.currentTimeMillis();
		this.playcover = story.getPlaycover();
	}

	public long getAddTime() {
		return addTime;
	}

	public void setAddTime(long addTime) {
		this.addTime = addTime;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getTimes() {
		return times;
	}

	public void setTimes(String times) {
		this.times = times;
	}

	public String getFile_size() {
		return file_size;
	}

	public void setFile_size(String file_size) {
		this.file_size = file_size;
	}

	public String getMediapath() {
		return mediapath;
	}

	public void setMediapath(String mediapath) {
		this.mediapath = mediapath;
	}

	public String getStoryId() {
		return storyId;
	}

	public void setStoryId(String storyId) {
		this.storyId = storyId;
	}

	public String getAlbumid() {
		return albumid;
	}

	public void setAlbumid(String albumid) {
		this.albumid = albumid;
	}
	
	

	public String getPlaycover() {
		return playcover;
	}

	public void setPlaycover(String playcover) {
		this.playcover = playcover;
	}

	public Story getStory() {
		return new Story(storyId, albumid, title, intro, times, file_size,
				mediapath, cover,playcover);
	}

	public boolean isError() {
		return isError;
	}

	public void setError(boolean isError) {
		this.isError = isError;
	}
	
	
}
