package com.xiaoningmeng.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.crud.DataSupport;

/**
 * Created by gaoyong on 16/3/1.
 */
public class NoteVar extends DataSupport implements Parcelable {

    private String tid;
    private String pid;
    private String subject;
    private String actoruid;
    private String actorusername;

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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getActoruid() {
        return actoruid;
    }

    public void setActoruid(String actoruid) {
        this.actoruid = actoruid;
    }

    public String getActorusername() {
        return actorusername;
    }

    public void setActorusername(String actorusername) {
        this.actorusername = actorusername;
    }


    public static Creator<NoteVar> getCREATOR() {
        return CREATOR;
    }

    protected NoteVar(Parcel in) {

        this.tid = in.readString();
        this.pid = in.readString();
        this.subject = in.readString();
        this.actoruid = in.readString();
        this.actorusername = in.readString();
    }


    public static final Creator<NoteVar> CREATOR = new Creator<NoteVar>() {
        @Override
        public NoteVar createFromParcel(Parcel in) {
            return new NoteVar(in);
        }

        @Override
        public NoteVar[] newArray(int size) {
            return new NoteVar[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {

        out.writeString(tid);
        out.writeString(pid);
        out.writeString(subject);
        out.writeString(actoruid);
        out.writeString(actorusername);
    }


}
