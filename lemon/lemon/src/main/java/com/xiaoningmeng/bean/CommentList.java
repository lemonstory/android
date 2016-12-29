package com.xiaoningmeng.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by gaoyong on 16/11/10.
 */

public class CommentList implements Parcelable {

    /**
     * total : 2
     * items : [{"id":"8686","uid":"10032","uname":"格格","avatar":"http://a.xiaoningmeng.net/avatar/10032/1448438048/120","start_level":"4","addtime":"2016-10-08 08:48:19","comment":"好作品，好有意思."},{"id":"8685","uid":"10011","uname":"不萌萌哒","avatar":"http://a.xiaoningmeng.net/avatar/10011/1448441203/120","start_level":"4","addtime":"2016-10-07 14:58:26","comment":"声音真好听"}]
     */

    private String total;
    private List<Comment> items;

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<Comment> getItems() {
        return items;
    }

    public void setItems(List<Comment> items) {
        this.items = items;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.total);
        dest.writeTypedList(this.items);
    }

    public CommentList() {
    }

    protected CommentList(Parcel in) {
        this.total = in.readString();
        this.items = in.createTypedArrayList(Comment.CREATOR);
    }

    public static final Parcelable.Creator<CommentList> CREATOR = new Parcelable.Creator<CommentList>() {
        @Override
        public CommentList createFromParcel(Parcel source) {
            return new CommentList(source);
        }

        @Override
        public CommentList[] newArray(int size) {
            return new CommentList[size];
        }
    };

    @Override
    public String toString() {
        return "CommentList{" +
                "total='" + total + '\'' +
                ", items=" + items +
                '}';
    }
}
