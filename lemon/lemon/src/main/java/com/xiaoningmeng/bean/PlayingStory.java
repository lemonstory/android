package com.xiaoningmeng.bean;

import android.graphics.Bitmap;

import com.xiaoningmeng.player.PlayerManager.PlayState;
import com.xiaoningmeng.player.PlayerManager.PlayType;

import java.lang.ref.SoftReference;

public class PlayingStory {

	public String storyId;
	public String title;
	public String intro;
	public int times;
	public String file_size;
	public String mediapath;
	public String cover;
	public int current;
	public int buffer;
	public String albumid;
	public PlayType playType;
	public int albumSource;
	public String playcover;
	public PlayState playState = PlayState.STOP;
	public AlbumInfo albumInfo;

	public void setStory(Story story) {
		this.storyId = story.getStoryId();
		this.title = story.getTitle();
		this.intro = story.getIntro();
		this.times = Integer.parseInt(story.getTimes());
		this.cover = story.getCover();
		this.file_size = story.getFile_size();
		this.albumid = story.getAlbum_id();
		this.mediapath = story.getMediapath();
		this.playcover = story.getPlaycover();
	}





	public Story getStory() {
		return new Story(storyId, albumid, title, intro, times + "", file_size,
				mediapath, cover,playcover);
	}


}