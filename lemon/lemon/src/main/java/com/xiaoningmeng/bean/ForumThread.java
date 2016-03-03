package com.xiaoningmeng.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.crud.DataSupport;

/**
 * Created by gaoyong on 16/1/22.
 */
public class ForumThread extends DataSupport implements Parcelable {

    private String tid;
    private String fid; //mythread使用
    private String typeid;
    private String readperm;
    private String price;
    private String author;
    private String authorid;
    private String subject;
    private String dateline;
    private String lastpost;
    private String lastposter;
    private String views;
    private String replies;
    private String displayorder;
    private String digest;
    private String heats;
    private String special;
    private String attachment;
    private String recommend_add;
    private String replycredit;
    private String dbdateline;
    private String dblastpost;
    private String rushreply;
    private String recommend;
    private String heatlevel;

    public String getHeatlevel() {
        return heatlevel;
    }

    public void setHeatlevel(String heatlevel) {
        this.heatlevel = heatlevel;
    }

    public ForumThread() {

    }

    public String getHeats() {
        return heats;
    }

    public void setHeats(String heats) {
        this.heats = heats;
    }

    public static Creator<ForumThread> getCREATOR() {
        return CREATOR;
    }

    public ForumThread(String tid,String fid, String typeid, String readperm, String price, String author, String authorid, String subject, String dateline, String lastpost, String lastposter, String views, String replies, String displayorder, String digest,String heats,String heatlevel, String special, String attachment, String recommend_add, String replycredit, String dbdateline, String dblastpost, String rushreply, String recommend) {

        this.tid = tid;
        this.fid = fid;
        this.typeid = typeid;
        this.readperm = readperm;
        this.price = price;
        this.author = author;
        this.authorid = authorid;
        this.subject = subject;
        this.dateline = dateline;
        this.lastpost = lastpost;
        this.lastposter = lastposter;
        this.views = views;
        this.replies = replies;
        this.displayorder = displayorder;
        this.digest = digest;
        this.heats = heats;
        this.heatlevel = heatlevel;
        this.special = special;
        this.attachment = attachment;
        this.recommend_add = recommend_add;
        this.replycredit = replycredit;
        this.dbdateline = dbdateline;
        this.dblastpost = dblastpost;
        this.rushreply = rushreply;
        this.recommend = recommend;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public String getFid() {

        return fid;
    }

    public String getTypeid() {
        return typeid;
    }

    public void setTypeid(String typeid) {
        this.typeid = typeid;
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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDateline() {
        return dateline;
    }

    public void setDateline(String dateline) {
        this.dateline = dateline;
    }

    public String getLastpost() {
        return lastpost;
    }

    public void setLastpost(String lastpost) {
        this.lastpost = lastpost;
    }

    public String getLastposter() {
        return lastposter;
    }

    public void setLastposter(String lastposter) {
        this.lastposter = lastposter;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getReplies() {
        return replies;
    }

    public void setReplies(String replies) {
        this.replies = replies;
    }

    public String getDisplayorder() {
        return displayorder;
    }

    public void setDisplayorder(String displayorder) {
        this.displayorder = displayorder;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public String getSpecial() {
        return special;
    }

    public void setSpecial(String special) {
        this.special = special;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getRecommend_add() {
        return recommend_add;
    }

    public void setRecommend_add(String recommend_add) {
        this.recommend_add = recommend_add;
    }

    public String getReplycredit() {
        return replycredit;
    }

    public void setReplycredit(String replycredit) {
        this.replycredit = replycredit;
    }

    public String getDbdateline() {
        return dbdateline;
    }

    public void setDbdateline(String dbdateline) {
        this.dbdateline = dbdateline;
    }

    public String getDblastpost() {
        return dblastpost;
    }

    public void setDblastpost(String dblastpost) {
        this.dblastpost = dblastpost;
    }

    public String getRushreply() {
        return rushreply;
    }

    public void setRushreply(String rushreply) {
        this.rushreply = rushreply;
    }

    public String getRecommend() {
        return recommend;
    }

    public void setRecommend(String recommend) {
        this.recommend = recommend;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public ForumThread(Parcel in) {

        tid = in.readString();
        fid = in.readString();
        typeid = in.readString();
        readperm = in.readString();
        price = in.readString();
        author = in.readString();
        authorid = in.readString();
        subject = in.readString();
        dateline = in.readString();
        lastpost = in.readString();
        lastposter = in.readString();
        views = in.readString();
        replies = in.readString();
        displayorder = in.readString();
        digest = in.readString();
        heats = in.readString();
        heatlevel = in.readString();
        special = in.readString();
        attachment = in.readString();
        recommend_add = in.readString();
        replycredit = in.readString();
        dbdateline = in.readString();
        dblastpost = in.readString();
        rushreply = in.readString();
        recommend = in.readString();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(tid);
        out.writeString(fid);
        out.writeString(typeid);
        out.writeString(readperm);
        out.writeString(price);
        out.writeString(author);
        out.writeString(authorid);
        out.writeString(subject);
        out.writeString(dateline);
        out.writeString(lastpost);
        out.writeString(lastposter);
        out.writeString(views);
        out.writeString(replies);
        out.writeString(displayorder);
        out.writeString(digest);
        out.writeString(heats);
        out.writeString(heatlevel);
        out.writeString(special);
        out.writeString(attachment);
        out.writeString(recommend_add );
        out.writeString(replycredit);
        out.writeString(dbdateline);
        out.writeString(dblastpost);
        out.writeString(rushreply);
        out.writeString(recommend);
    }

    public static final Parcelable.Creator<ForumThread> CREATOR = new Parcelable.Creator<ForumThread>() {

        @Override
        public ForumThread createFromParcel(Parcel source) {

            return new ForumThread(source);
        }

        @Override
        public ForumThread[] newArray(int size) {

            return new ForumThread[size];
        }
    };

    public static Parcelable.Creator<ForumThread> getCreator() {
        return CREATOR;
    }

}
