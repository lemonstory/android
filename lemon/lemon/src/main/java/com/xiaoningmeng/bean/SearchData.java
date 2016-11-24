package com.xiaoningmeng.bean;

import java.util.List;

public class SearchData {

    private List<Story> storylist;
    private int storycount;
    private List<AlbumInfo> albumlist;
    private int albumcount;

    public List<Story> getStorylist() {
        return storylist;
    }

    public void setStorylist(List<Story> storylist) {
        this.storylist = storylist;
    }

    public int getStorycount() {
        return storycount;
    }

    public void setStorycount(int storycount) {
        this.storycount = storycount;
    }

    public List<AlbumInfo> getAlbumlist() {
        return albumlist;
    }

    public void setAlbumlist(List<AlbumInfo> albumlist) {
        this.albumlist = albumlist;
    }

    public int getAlbumcount() {
        return albumcount;
    }

    public void setAlbumcount(int albumcount) {
        this.albumcount = albumcount;
    }


}
