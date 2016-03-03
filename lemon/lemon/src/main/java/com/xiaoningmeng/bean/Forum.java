package com.xiaoningmeng.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.crud.DataSupport;

/**
 * Created by gaoyong on 16/1/20.
 */
public class Forum extends DataSupport implements Parcelable {

    private String fid;
    private String name;
    private String threads;
    private String posts;
    private String todayposts;
    private String description;
    private String icon;

    public Forum(String fid,String name) {

        this.fid = fid;
        this.name = name;
        this.threads = "";
        this.posts = "";
        this.todayposts = "";
        this.description = "";
        this.icon = "";
    }


    public Forum(String fid,String name,String threads,String posts,String todayposts,String description,String icon) {

        this.fid = fid;
        this.name = name;
        this.threads = threads;
        this.posts = posts;
        this.todayposts = todayposts;
        this.description = description;
        this.icon = icon;
    }

    public Forum(Parcel in) {
        fid = in.readString();
        name = in.readString();
        threads = in.readString();
        posts = in.readString();
        todayposts = in.readString();
        description = in.readString();
        icon = in.readString();
    }

    public String getFId() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getThreads() {
        return threads;
    }

    public void setThreads(String threads) {
        this.fid = threads;
    }
    public String getPosts() {
        return posts;
    }

    public void setPosts(String posts) {
        this.fid = posts;
    }
    public String getTodayposts() {
        return todayposts;
    }

    public void setTodayposts(String todayposts) {
        this.todayposts = todayposts;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(fid);
        out.writeString(name);
        out.writeString(threads);
        out.writeString(posts);
        out.writeString(todayposts);
        out.writeString(description);
        out.writeString(icon);
    }

    public static final Parcelable.Creator<Forum> CREATOR = new Parcelable.Creator<Forum>() {

        @Override
        public Forum createFromParcel(Parcel source) {

            return new Forum(source);
        }

        @Override
        public Forum[] newArray(int size) {

            return new Forum[size];
        }

    };

    public static Parcelable.Creator<Forum> getCreator() {
        return CREATOR;
    }


}
