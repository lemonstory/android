package com.xiaoningmeng.bean;

import org.litepal.crud.DataSupport;

public class Story extends DataSupport {

    public String id;
    private String storyId;
    private String album_id;
    private String title;
    private String intro;
    private String times;
    private String file_size;
    private String mediapath;
    private String cover;
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
                 String times, String file_size, String mediapath, String cover, String playcover) {
        this.id = storyId;
        this.storyId = storyId;
        this.title = title;
        this.intro = intro;
        this.times = times;
        this.album_id = albumId;
        this.file_size = file_size;
        this.mediapath = mediapath;
        this.cover = cover;
        this.playcover = playcover;
    }

    public String getId() {
        if (id == null) {
            id = storyId;
        }
        return id;
    }

    public void setId(String id) {
        this.id = id;
        this.storyId = id;
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

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getStoryId() {
        if (storyId == null) {
            storyId = id;
        }
        return storyId;
    }


    public String getAlbum_id() {
        return album_id;
    }

    public void setAlbum_id(String album_id) {
        this.album_id = album_id;
    }

    public void setStoryId(String storyId) {
        this.storyId = storyId;
        this.id = storyId;
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
                ", storyId='" + storyId + '\'' +
                ", album_id='" + album_id + '\'' +
                ", title='" + title + '\'' +
                ", intro='" + intro + '\'' +
                ", times='" + times + '\'' +
                ", file_size='" + file_size + '\'' +
                ", mediapath='" + mediapath + '\'' +
                ", cover='" + cover + '\'' +
                ", playcover='" + playcover + '\'' +
                '}';
    }
}
