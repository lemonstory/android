package com.xiaoningmeng.bean;

/**
 * Created by feishang on 2016/1/28.
 */
public class TagAlbum {

    private String id;
    private String tagid;
    private String albumid;
    private AlbumInfo albuminfo;

    public AlbumInfo getAlbuminfo() {
        return albuminfo;
    }

    public void setAlbuminfo(AlbumInfo albuminfo) {
        this.albuminfo = albuminfo;
    }

    public String getAlbumid() {
        return albumid;
    }

    public void setAlbumid(String albumid) {
        this.albumid = albumid;
    }

    public String getTagid() {
        return tagid;
    }

    public void setTagid(String tagid) {
        this.tagid = tagid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
