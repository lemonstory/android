package com.xiaoningmeng.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by feishang on 2016/1/5.
 */
public class Tag implements Parcelable {


    /**
     * id : 1
     * pid : 0
     * name : 儿歌
     * cover :
     * ordernum : 100
     * addtime : 2016-01-04 11:43:42
     */

    private String id;
    private String pid;
    private String name;
    private String cover;
    private String ordernum;
    private String addtime;

    public void setId(String id) {
        this.id = id;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public void setOrdernum(String ordernum) {
        this.ordernum = ordernum;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public String getId() {
        return id;
    }

    public String getPid() {
        return pid;
    }

    public String getName() {
        return name;
    }

    public String getCover() {
        return cover;
    }

    public String getOrdernum() {
        return ordernum;
    }

    public String getAddtime() {
        return addtime;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.pid);
        dest.writeString(this.name);
        dest.writeString(this.cover);
        dest.writeString(this.ordernum);
        dest.writeString(this.addtime);
    }

    public Tag() {
    }

    protected Tag(Parcel in) {
        this.id = in.readString();
        this.pid = in.readString();
        this.name = in.readString();
        this.cover = in.readString();
        this.ordernum = in.readString();
        this.addtime = in.readString();
    }

    public static final Parcelable.Creator<Tag> CREATOR = new Parcelable.Creator<Tag>() {
        public Tag createFromParcel(Parcel source) {
            return new Tag(source);
        }

        public Tag[] newArray(int size) {
            return new Tag[size];
        }
    };
}
