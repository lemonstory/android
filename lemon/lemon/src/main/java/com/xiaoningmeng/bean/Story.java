package com.xiaoningmeng.bean;

import org.litepal.crud.DataSupport;

public class Story extends DataSupport {

    public String id;
    private String album_id;
    private String title;
    private String intro;
    private String times;
    private String file_size;
    private String mediapath;
    private String view_order;
    private String playcover;
    private static Story mInstance;

    public static Story getInstance() {
        if (mInstance == null) {
            synchronized (Story.class) {
                if (mInstance == null) {
                    mInstance = new Story();
                }
            }
        }
        return mInstance;
    }


    public Story() {

    }

    public Story(String storyId, String albumId, String title, String intro,
                 String times, String file_size, String mediapath, String view_order, String playcover) {
        this.id = storyId;
        this.title = title;
        this.intro = intro;
        this.times = times;
        this.album_id = albumId;
        this.file_size = file_size;
        this.mediapath = mediapath;
        this.view_order = view_order;
        this.playcover = playcover;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    public String getFile_size() {
        return file_size;
    }

    public void setFile_size(String file_size) {
        this.file_size = file_size;
    }

    public String getMediapath() {
        return mediapath;
    }

    public void setMediapath(String mediapath) {
        this.mediapath = mediapath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum_id() {
        return album_id;
    }

    public void setAlbum_id(String album_id) {
        this.album_id = album_id;
    }

    public String getView_order() {
        return view_order;
    }

    public void setView_order(String view_order) {
        this.view_order = view_order;
    }

    public String getPlaycover() {
        return playcover;
    }

    public void setPlaycover(String playcover) {
        this.playcover = playcover;
    }

    @Override
    public String toString() {
        return "Story{" +
                "id='" + id + '\'' +
                ", album_id='" + album_id + '\'' +
                ", title='" + title + '\'' +
                ", intro='" + intro + '\'' +
                ", times='" + times + '\'' +
                ", file_size='" + file_size + '\'' +
                ", mediapath='" + mediapath + '\'' +
                ", view_order='" + view_order + '\'' +
                ", playcover='" + playcover + '\'' +
                '}';
    }
}
