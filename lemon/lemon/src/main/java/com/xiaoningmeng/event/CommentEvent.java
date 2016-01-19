package com.xiaoningmeng.event;

import com.xiaoningmeng.bean.AlbumInfo;

public class CommentEvent {

	public AlbumInfo albumInfo;
	public int commentCount;
	public CommentEvent(AlbumInfo albumInfo, int commentCount) {
		this.albumInfo = albumInfo;
		this.commentCount = commentCount;
	}
	
	
}
