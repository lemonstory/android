package com.xiaoningmeng.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gaoyong on 16/12/8.
 */

public class Author implements Parcelable {
    /**
     * uid : 作者uid
     * avatar : 作者头像地址
     * nickname : 作者姓名
     * card : 作者一句话介绍
     * intro : 作者简介
     * album_num : 作者的专辑数量
     * wiki_url : 作者的wiki网址
     */

    private String uid;
    private String avatar;
    private String nickname;
    private String card;
    private String intro;
    private int album_num;
    private String wiki_url;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public int getAlbum_num() {
        return album_num;
    }

    public void setAlbum_num(int album_num) {
        this.album_num = album_num;
    }

    public String getWiki_url() {
        return wiki_url;
    }

    public void setWiki_url(String wiki_url) {
        this.wiki_url = wiki_url;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(this.uid);
        dest.writeString(this.avatar);
        dest.writeString(this.nickname);
        dest.writeString(this.card);
        dest.writeString(this.intro);
        dest.writeInt(this.album_num);
        dest.writeString(this.wiki_url);
    }

    public Author() {

    }

    public Author(String uid, String avatar, String nickname, String card, String intro, int album_num, String wiki_url) {
        this.uid = uid;
        this.avatar = avatar;
        this.nickname = nickname;
        this.card = card;
        this.intro = intro;
        this.album_num = album_num;
        this.wiki_url = wiki_url;
    }

    protected Author(Parcel in) {
        this.uid = in.readString();
        this.avatar = in.readString();
        this.nickname = in.readString();
        this.card = in.readString();
        this.intro = in.readString();
        this.album_num = in.readInt();
        this.wiki_url = in.readString();
    }

    public static final Parcelable.Creator<Author> CREATOR = new Parcelable.Creator<Author>() {
        @Override
        public Author createFromParcel(Parcel source) {
            return new Author(source);
        }

        @Override
        public Author[] newArray(int size) {
            return new Author[size];
        }
    };
}