package com.xiaoningmeng.bean;

import java.util.List;

public class Album {

	private AlbumInfo albuminfo;
	private List<Story> storylist;
	private List<Comment> commentlist;
	private List<Tag> taglist;
	private List<AlbumInfo> recommendalbumlist;
	public AlbumInfo getAlbuminfo() {
		return albuminfo;
	}

	public void setAlbuminfo(AlbumInfo albuminfo) {
		this.albuminfo = albuminfo;
	}

	public List<Story> getStorylist() {
		return storylist;
	}

	public void setStorylist(List<Story> storylist) {
		this.storylist = storylist;
	}

	public List<Comment> getCommentlist() {
		return commentlist;
	}

	public void setCommentlist(List<Comment> commentlist) {
		this.commentlist = commentlist;
	}

	public List<Tag> getTaglist() {
		return taglist;
	}

	public void setTaglist(List<Tag> taglist) {
		this.taglist = taglist;
	}

	public List<AlbumInfo> getRecommendalbumlist() {
		return recommendalbumlist;
	}

	public void setRecommendalbumlist(List<AlbumInfo> recommendalbumlist) {
		this.recommendalbumlist = recommendalbumlist;
	}
}
