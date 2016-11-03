package com.xiaoningmeng.bean;

import java.util.List;

public class Album {

	private AlbumInfo albumInfo;
	private StoryListBean storyList;
	private List<Comment> commentlist;
	private List<Tag> tagList;
	private List<AlbumInfo> recommendAlbumList;
	public AlbumInfo getAlbumInfo() {
		return albumInfo;
	}

	public void setAlbumInfo(AlbumInfo albumInfo) {
		this.albumInfo = albumInfo;
	}

	public StoryListBean getStoryList() {
		return storyList;
	}

	public void setStoryList(StoryListBean storyList) {
		this.storyList = storyList;
	}

	public List<Story> getStoryListItems() {
		return this.storyList.getItems();
	}

	public List<Comment> getCommentlist() {
		return commentlist;
	}

	public void setCommentlist(List<Comment> commentlist) {
		this.commentlist = commentlist;
	}

	public List<Tag> getTagList() {
		return tagList;
	}

	public void setTagList(List<Tag> tagList) {
		this.tagList = tagList;
	}

	public List<AlbumInfo> getRecommendAlbumList() {
		return recommendAlbumList;
	}

	public void setRecommendAlbumList(List<AlbumInfo> recommendAlbumList) {
		this.recommendAlbumList = recommendAlbumList;
	}

	public static class StoryListBean  {
		private int total;
		private List<Story> items;

		public int getTotal() {
			return total;
		}

		public void setTotal(int total) {
			this.total = total;
		}

		public List<Story> getItems() {
			return items;
		}

		public void setItems(List<Story> items) {
			this.items = items;
		}
	}
}
