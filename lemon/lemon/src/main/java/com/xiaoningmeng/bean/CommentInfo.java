package com.xiaoningmeng.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class CommentInfo implements Parcelable {

    private String id;
    private String uid;
    private String uname;
    private int start_level;
    private String comment;
    private String avatartime;
    private String addtime;

    public CommentInfo(String uid, String uname, int start_level, String comment,
                       String avatartime, String addtime) {
        this.uid = uid;
        this.uname = uname;
        this.start_level = start_level;
        this.comment = comment;
        this.avatartime = avatartime;
        this.addtime = addtime;
    }

    public CommentInfo(Parcel in) {
        this.id = in.readString();
        this.uid = in.readString();
        this.uname = in.readString();
        this.start_level = in.readInt();
        this.comment = in.readString();
        this.avatartime = in.readString();
        this.addtime = in.readString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public int getStart_level() {
        return start_level;
    }

    public void setStart_level(int start_level) {
        this.start_level = start_level;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getAvatartime() {
        return avatartime;
    }

    public void setAvatartime(String avatartime) {
        this.avatartime = avatartime;
    }


    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(id);
        out.writeString(uid);
        out.writeString(uname);
        out.writeInt(start_level);
        out.writeString(comment);
        out.writeString(avatartime);
        out.writeString(addtime);
    }

    public static final Parcelable.Creator<CommentInfo> CREATOR = new Creator<CommentInfo>() {
        @Override
        public CommentInfo createFromParcel(Parcel source) {

            return new CommentInfo(source);
        }

        @Override
        public CommentInfo[] newArray(int size) {

            return new CommentInfo[size];
        }
    };
}