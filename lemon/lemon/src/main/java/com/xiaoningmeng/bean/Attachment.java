package com.xiaoningmeng.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.crud.DataSupport;

/**
 * Created by gaoyong on 16/1/25.
 */
public class Attachment extends DataSupport implements Parcelable {

    private String aid;
    private String tid;
    private String pid;
    private String uid;
    private String dateline;
    private String filename;
    private String filesize;
    private String attachment;
    private String remote;
    private String description;
    private String readperm;
    private String price;
    private String isimage;
    private String width;
    private String thumb;
    private String picid;
    private String ext;
    private String imgalt;
    private String attachicon;
    private String attachsize;
    private String attachimg;
    private String payed;
    private String url;
    private String dbdateline;
    private String downloads;

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDateline() {
        return dateline;
    }

    public void setDateline(String dateline) {
        this.dateline = dateline;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilesize() {
        return filesize;
    }

    public void setFilesize(String filesize) {
        this.filesize = filesize;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getRemote() {
        return remote;
    }

    public void setRemote(String remote) {
        this.remote = remote;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReadperm() {
        return readperm;
    }

    public void setReadperm(String readperm) {
        this.readperm = readperm;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getIsimage() {
        return isimage;
    }

    public void setIsimage(String isimage) {
        this.isimage = isimage;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getPicid() {
        return picid;
    }

    public void setPicid(String picid) {
        this.picid = picid;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getImgalt() {
        return imgalt;
    }

    public void setImgalt(String imgalt) {
        this.imgalt = imgalt;
    }

    public String getAttachicon() {
        return attachicon;
    }

    public void setAttachicon(String attachicon) {
        this.attachicon = attachicon;
    }

    public String getAttachsize() {
        return attachsize;
    }

    public void setAttachsize(String attachsize) {
        this.attachsize = attachsize;
    }

    public String getAttachimg() {
        return attachimg;
    }

    public void setAttachimg(String attachimg) {
        this.attachimg = attachimg;
    }

    public String getPayed() {
        return payed;
    }

    public void setPayed(String payed) {
        this.payed = payed;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDbdateline() {
        return dbdateline;
    }

    public void setDbdateline(String dbdateline) {
        this.dbdateline = dbdateline;
    }

    public String getDownloads() {
        return downloads;
    }

    public void setDownloads(String downloads) {
        this.downloads = downloads;
    }

    public static Creator<Attachment> getCREATOR() {
        return CREATOR;
    }

    public Attachment(Parcel in) {

        aid = in.readString();
        tid = in.readString();
        pid = in.readString();
        uid = in.readString();
        dateline = in.readString();
        filename = in.readString();
        filesize = in.readString();
        attachment = in.readString();
        remote = in.readString();
        description = in.readString();
        readperm = in.readString();
        price = in.readString();
        isimage = in.readString();
        width = in.readString();
        thumb = in.readString();
        picid = in.readString();
        ext = in.readString();

        imgalt = in.readString();
        attachicon = in.readString();
        attachsize = in.readString();
        attachimg = in.readString();
        payed = in.readString();
        url = in.readString();
        dbdateline = in.readString();
        downloads = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {

        out.writeString(aid);
        out.writeString(tid);
        out.writeString(pid);
        out.writeString(uid);
        out.writeString(dateline);
        out.writeString(filename);
        out.writeString(filesize);
        out.writeString(attachment);
        out.writeString(remote);
        out.writeString(description);
        out.writeString(readperm);
        out.writeString(price);
        out.writeString(isimage);
        out.writeString(width);
        out.writeString(thumb);
        out.writeString(picid);
        out.writeString(ext);
        out.writeString(imgalt);
        out.writeString(attachicon);
        out.writeString(attachsize);
        out.writeString(attachimg);
        out.writeString(payed);
        out.writeString(url);
        out.writeString(dbdateline);
        out.writeString(downloads);
    }

    public static final Parcelable.Creator<Attachment> CREATOR = new Parcelable.Creator<Attachment>() {

        @Override
        public Attachment createFromParcel(Parcel source) {

            return new Attachment(source);
        }

        @Override
        public Attachment[] newArray(int size) {

            return new Attachment[size];
        }

    };

    public static Parcelable.Creator<Attachment> getCreator() {
        return CREATOR;
    }
}
