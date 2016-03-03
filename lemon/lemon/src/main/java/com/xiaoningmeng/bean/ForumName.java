package com.xiaoningmeng.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gaoyong on 16/2/29.
 */
public class ForumName implements Parcelable {

    private String id;

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    public ForumName(String id,String name) {

        this.id = id;
        this.name = name;
    }

    public ForumName(Parcel in){

        this.id = in.readString();
        this.name = in.readString();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {

        out.writeString(id);
        out.writeString(name);
    }

    public static final Parcelable.Creator<ForumName> CREATOR = new Parcelable.Creator<ForumName>(){
        @Override
        public ForumName createFromParcel(Parcel source) {

            return new ForumName(source);
        }

        @Override
        public ForumName[] newArray(int size) {

            return new ForumName[size];
        }
    };
}
