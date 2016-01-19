package com.xiaoningmeng.event;

import com.xiaoningmeng.bean.ListenerAlbum;

public class HistoryEvent {

	
	public ListenerAlbum listenerAlbum; 
	public String albumId;
	public String storyId;

	public HistoryEvent(ListenerAlbum listenerAlbum, String albumId,String storyId) {
		this.listenerAlbum = listenerAlbum;
		this.albumId = albumId;
		this.storyId = storyId;
	}
	public HistoryEvent(String albumId, String storyId) {
		this.albumId = albumId;
		this.storyId = storyId;
	}
	
	
}
