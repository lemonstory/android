package com.xiaoningmeng.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.crud.DataSupport;

/**
 * Created by gaoyong on 16/3/2.
 */
public class ForumLoginVar extends DataSupport implements Parcelable {

  private String cookiepre;
  private String auth;
  private String saltkey;
  private String member_uid;
  private String member_username;
  private String member_avatar;
  private String groupid;
  private String formhash;
  private String ismoderator;
  private String readaccess;
  private String loginUrl;
  private ForumNotice notice;

  public String getCookiepre() {
    return cookiepre;
  }

  public void setCookiepre(String cookiepre) {
    this.cookiepre = cookiepre;
  }

  public String getAuth() {
    return auth;
  }

  public void setAuth(String auth) {
    this.auth = auth;
  }

  public String getSaltkey() {
    return saltkey;
  }

  public void setSaltkey(String saltkey) {
    this.saltkey = saltkey;
  }

  public String getMember_uid() {
    return member_uid;
  }

  public void setMember_uid(String member_uid) {
    this.member_uid = member_uid;
  }

  public String getMember_username() {
    return member_username;
  }

  public void setMember_username(String member_username) {
    this.member_username = member_username;
  }

  public String getMember_avatar() {
    return member_avatar;
  }

  public void setMember_avatar(String member_avatar) {
    this.member_avatar = member_avatar;
  }

  public String getGroupid() {
    return groupid;
  }

  public void setGroupid(String groupid) {
    this.groupid = groupid;
  }

  public String getFormhash() {
    return formhash;
  }

  public void setFormhash(String formhash) {
    this.formhash = formhash;
  }

  public String getIsmoderator() {
    return ismoderator;
  }

  public void setIsmoderator(String ismoderator) {
    this.ismoderator = ismoderator;
  }

  public String getReadaccess() {
    return readaccess;
  }

  public void setReadaccess(String readaccess) {
    this.readaccess = readaccess;
  }

  public String getLoginUrl() {
    return loginUrl;
  }

  public void setLoginUrl(String loginUrl) {
    this.loginUrl = loginUrl;
  }

  public ForumNotice getNotice() {
    return notice;
  }

  public void setNotice(ForumNotice notice) {
    this.notice = notice;
  }

  public static Creator<ForumLoginVar> getCREATOR() {
    return CREATOR;
  }

  protected ForumLoginVar(Parcel in) {

    this.cookiepre = in.readString();
    this.auth = in.readString();
    this.saltkey = in.readString();
    this.member_uid = in.readString();
    this.member_username = in.readString();
    this.member_avatar = in.readString();
    this.groupid = in.readString();
    this.formhash = in.readString();
    this.ismoderator = in.readString();
    this.readaccess = in.readString();
    this.loginUrl = in.readString();
    this.notice = in.readParcelable(ForumNotice.class.getClassLoader());
  }

  public static final Creator<ForumLoginVar> CREATOR = new Creator<ForumLoginVar>() {
    @Override
    public ForumLoginVar createFromParcel(Parcel in) {
      return new ForumLoginVar(in);
    }

    @Override
    public ForumLoginVar[] newArray(int size) {
      return new ForumLoginVar[size];
    }
  };

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {

    dest.writeString(cookiepre);
    dest.writeString(auth);
    dest.writeString(saltkey);
    dest.writeString(member_uid);
    dest.writeString(member_username);
    dest.writeString(member_avatar);
    dest.writeString(groupid);
    dest.writeString(formhash);
    dest.writeString(ismoderator);
    dest.writeString(readaccess);
    dest.writeString(loginUrl);
    dest.writeParcelable(notice, flags);
  }

  @Override
  public String toString() {
    return "ForumLoginVar{" +
            "cookiepre='" + cookiepre + '\'' +
            ", auth='" + auth + '\'' +
            ", saltkey='" + saltkey + '\'' +
            ", member_uid='" + member_uid + '\'' +
            ", member_username='" + member_username + '\'' +
            ", member_avatar='" + member_avatar + '\'' +
            ", groupid='" + groupid + '\'' +
            ", formhash='" + formhash + '\'' +
            ", ismoderator='" + ismoderator + '\'' +
            ", readaccess='" + readaccess + '\'' +
            ", loginUrl='" + loginUrl + '\'' +
            ", notice=" + notice +
            '}';
  }
}
