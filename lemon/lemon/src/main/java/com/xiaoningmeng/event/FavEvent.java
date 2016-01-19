package com.xiaoningmeng.event;

import com.xiaoningmeng.bean.AlbumInfo;

public class FavEvent {

	public AlbumInfo albumInfo;
	public int fav;
	public FavEvent(AlbumInfo albumInfo, int fav) {
		this.albumInfo = albumInfo;
		this.fav = fav;
	}
	
	
}
