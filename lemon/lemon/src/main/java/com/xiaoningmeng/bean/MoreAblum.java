package com.xiaoningmeng.bean;

import java.util.List;

/**
 * Created by feishang on 2016/1/11.
 */
public class MoreAblum {

    private List<AlbumInfo> hotrecommendlist;
    private List<Tag> firsttaglist;
    private List<AlbumInfo> newonlinelist;
    private List<AlbumInfo> sameagelist;

    public List<AlbumInfo> getHotrecommendlist() {
        return hotrecommendlist;
    }

    public void setHotrecommendlist(List<AlbumInfo> hotrecommendlist) {
        this.hotrecommendlist = hotrecommendlist;
    }

    public List<Tag> getFirsttaglist() {
        return firsttaglist;
    }

    public void setFirsttaglist(List<Tag> firsttaglist) {
        this.firsttaglist = firsttaglist;
    }

    public List<AlbumInfo> getNewonlinelist() {
        return newonlinelist;
    }

    public void setNewonlinelist(List<AlbumInfo> newonlinelist) {
        this.newonlinelist = newonlinelist;
    }

    public List<AlbumInfo> getSameagelist() {
        return sameagelist;
    }

    public void setSameagelist(List<AlbumInfo> sameagelist) {
        this.sameagelist = sameagelist;
    }
}
