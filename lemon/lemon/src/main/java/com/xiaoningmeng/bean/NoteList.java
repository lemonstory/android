package com.xiaoningmeng.bean;

public class NoteList {

    private String id;
    private String uid;
    private String type;
    private String isnew;
    private String authorid;
    private String author;
    private String note;
    private String dateline;
    private String dbdateline;
    private String from_id;
    private String from_idtype;
    private String from_num;
    private String message;
    private NoteVar notevar;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIsnew() {
        return isnew;
    }

    public void setIsnew(String isnew) {
        this.isnew = isnew;
    }

    public String getAuthorid() {
        return authorid;
    }

    public void setAuthorid(String authorid) {
        this.authorid = authorid;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDateline() {
        return dateline;
    }

    public void setDateline(String dateline) {
        this.dateline = dateline;
    }

    public String getFrom_id() {
        return from_id;
    }

    public void setFrom_id(String from_id) {
        this.from_id = from_id;
    }

    public String getFrom_idtype() {
        return from_idtype;
    }

    public void setFrom_idtype(String from_idtype) {
        this.from_idtype = from_idtype;
    }

    public String getFrom_num() {
        return from_num;
    }

    public void setFrom_num(String from_num) {
        this.from_num = from_num;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NoteVar getNotevar() {
        return notevar;
    }

    public void setNotevar(NoteVar notevar) {
        this.notevar = notevar;
    }

    public String getDbdateline() {
        return dbdateline;
    }

    public void setDbdateline(String dbdateline) {
        this.dbdateline = dbdateline;
    }
}
