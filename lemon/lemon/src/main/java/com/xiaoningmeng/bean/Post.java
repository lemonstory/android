package com.xiaoningmeng.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by gaoyong on 16/1/25.
 */
public class Post extends DataSupport implements Parcelable {


    private String pid;
    private String tid;
    private String first;
    private String author;
    private String authorid;
    private String dateline;
    private String message;
    private String anonymous;
    private String attachment;
    private String status;
    private String replycredit;
    private String position;
    private String username;
    private String adminid;
    private String groupid;
    private String memberstatus;
    private String number;
    private String dbdateline;
    //附件
    private HashMap<String,Attachment> attachments;
    private ArrayList<String> imagelist;

    public ArrayList<String> getImagelist() {
        return imagelist;
    }

    public void setImagelist(ArrayList<String> imagelist) {
        this.imagelist = imagelist;
    }

    public Post(String pid, String tid, String first, String author, String authorid, String dateline, String message, String anonymous, String attachment, String status, String replycredit, String position, String username, String adminid, String groupid, String memberstatus, String number, String dbdateline, HashMap<String,Attachment> attachments, ArrayList<String> imagelist) {
        this.pid = pid;
        this.tid = tid;
        this.first = first;
        this.author = author;
        this.authorid = authorid;
        this.dateline = dateline;
        this.message = message;
        this.anonymous = anonymous;
        this.attachment = attachment;
        this.status = status;
        this.replycredit = replycredit;
        this.position = position;
        this.username = username;
        this.adminid = adminid;

        this.groupid = groupid;
        this.memberstatus = memberstatus;
        this.number = number;
        this.dbdateline = dbdateline;
        this.attachments = attachments;
        this.imagelist = imagelist;
    }


    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthorid() {
        return authorid;
    }

    public void setAuthorid(String authorid) {
        this.authorid = authorid;
    }

    public String getDateline() {
        return dateline;
    }

    public void setDateline(String dateline) {
        this.dateline = dateline;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAnonymous() {
        return anonymous;
    }

    public void setAnonymous(String anonymous) {
        this.anonymous = anonymous;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReplycredit() {
        return replycredit;
    }

    public void setReplycredit(String replycredit) {
        this.replycredit = replycredit;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAdminid() {
        return adminid;
    }

    public void setAdminid(String adminid) {
        this.adminid = adminid;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public String getMemberstatus() {
        return memberstatus;
    }

    public void setMemberstatus(String memberstatus) {
        this.memberstatus = memberstatus;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDbdateline() {
        return dbdateline;
    }

    public void setDbdateline(String dbdateline) {
        this.dbdateline = dbdateline;
    }

    public HashMap<String,Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(HashMap<String,Attachment> attachments) {
        this.attachments = attachments;
    }

    public Post(Parcel in) {

        pid = in.readString();
        tid = in.readString();
        first = in.readString();
        author = in.readString();
        authorid = in.readString();
        dateline = in.readString();
        message = in.readString();
        anonymous = in.readString();
        attachment = in.readString();
        status = in.readString();
        replycredit = in.readString();
        position = in.readString();
        username = in.readString();
        adminid = in.readString();
        groupid = in.readString();
        memberstatus = in.readString();
        number = in.readString();
        dbdateline = in.readString();
        //attachments = in.readMap();

    }

    @Override
    public int describeContents() {

        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {

        out.writeString(pid);
        out.writeString(tid);
        out.writeString(first);
        out.writeString(author);
        out.writeString(authorid);
        out.writeString(dateline);
        out.writeString(message);
        out.writeString(anonymous);
        out.writeString( attachment);
        out.writeString(status);
        out.writeString(replycredit);
        out.writeString(position);
        out.writeString(username);
        out.writeString(adminid);
        out.writeString(groupid);
        out.writeString(memberstatus);
        out.writeString(number);
        out.writeString(dbdateline);
        //out.writeMap(attachments);
    }

    public static final Parcelable.Creator<Post> CREATOR = new Parcelable.Creator<Post>() {

        @Override
        public Post createFromParcel(Parcel source) {

            return new Post(source);
        }

        @Override
        public Post[] newArray(int size) {

            return new Post[size];
        }
    };

    public static Parcelable.Creator<Post> getCreator() {
        return CREATOR;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Post && position.equals(((Post) o).position);
    }

    @Override
    public int hashCode() {
        return position.hashCode();
    }
}

