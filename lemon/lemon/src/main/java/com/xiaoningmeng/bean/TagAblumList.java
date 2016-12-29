package com.xiaoningmeng.bean;

import java.util.List;

/**
 * Created by feishang on 2016/1/6.
 */
public class TagAblumList {

    private String selectfirsttagid;
    private String selectsecondtagid;
    private List<Tag> firsttaglist;
    private List<Tag> secondtaglist;
    private List<TagAlbum> tagalbumlist;
    private List<Special> specialtaglist;

    public String getSelectfirsttagid() {
        return selectfirsttagid;
    }

    public void setSelectfirsttagid(String selectfirsttagid) {
        this.selectfirsttagid = selectfirsttagid;
    }

    public String getSelectsecondtagid() {
        return selectsecondtagid;
    }

    public void setSelectsecondtagid(String selectsecondtagid) {
        this.selectsecondtagid = selectsecondtagid;
    }

    public List<Tag> getFirsttaglist() {
        return firsttaglist;
    }

    public void setFirsttaglist(List<Tag> firsttaglist) {
        this.firsttaglist = firsttaglist;
    }

    public List<Tag> getSecondtaglist() {
        return secondtaglist;
    }

    public void setSecondtaglist(List<Tag> secondtaglist) {
        this.secondtaglist = secondtaglist;
    }

    public List<TagAlbum> getTagalbumlist() {
        return tagalbumlist;
    }

    public void setTagalbumlist(List<TagAlbum> tagalbumlist) {
        this.tagalbumlist = tagalbumlist;
    }

    public List<Special> getSpecialtaglist() {
        return specialtaglist;
    }

    public void setSpecialtaglist(List<Special> specialtaglist) {
        this.specialtaglist = specialtaglist;
    }
}
