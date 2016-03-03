package com.xiaoningmeng.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.crud.DataSupport;

/**
 * Created by gaoyong on 16/3/3.
 */
public class ForumNotice extends DataSupport implements Parcelable {

    private String newpush;
    private String newpm;
    private String newprompt;
    private String newmypost;

    public String getNewpush() {
        return newpush;
    }

    public void setNewpush(String newpush) {
        this.newpush = newpush;
    }

    public String getNewpm() {
        return newpm;
    }

    public void setNewpm(String newpm) {
        this.newpm = newpm;
    }

    public String getNewprompt() {
        return newprompt;
    }

    public void setNewprompt(String newprompt) {
        this.newprompt = newprompt;
    }

    public String getNewmypost() {
        return newmypost;
    }

    public void setNewmypost(String newmypost) {
        this.newmypost = newmypost;
    }

    public static Creator<ForumNotice> getCREATOR() {
        return CREATOR;
    }

    protected ForumNotice(Parcel in) {

        this.newpush = in.readString();
        this.newpm = in.readString();
        this.newprompt = in.readString();
        this.newmypost = in.readString();
    }

    public static final Creator<ForumNotice> CREATOR = new Creator<ForumNotice>() {
        @Override
        public ForumNotice createFromParcel(Parcel in) {
            return new ForumNotice(in);
        }

        @Override
        public ForumNotice[] newArray(int size) {
            return new ForumNotice[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(newpush);
        dest.writeString(newpm);
        dest.writeString(newprompt);
        dest.writeString(newmypost);
    }

    @Override
    public String toString() {
        return "ForumNotice{" +
                "newpush='" + newpush + '\'' +
                ", newpm='" + newpm + '\'' +
                ", newprompt='" + newprompt + '\'' +
                ", newmypost='" + newmypost + '\'' +
                '}';
    }
}
