package com.xiaoningmeng.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gaoyong on 16/12/12.
 */

public class AuthorList implements Parcelable {


    /**
     * total : 612
     * items : [{"uid":"14981","album_num":"50","listen_num":"0","add_time":"2016-12-10 14:48:56","view_order":"10000","online_status":"0","nickname":"彭绪洛","avatar":"http://a.xiaoningmeng.net/avatar/14981/0/180"}]
     */

    private int total;
    private List<Author> items;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<Author> getItems() {
        return items;
    }

    public void setItems(List<Author> items) {
        this.items = items;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.total);
        dest.writeList(this.items);
    }

    public AuthorList() {
    }

    protected AuthorList(Parcel in) {
        this.total = in.readInt();
        this.items = new ArrayList<Author>();
        in.readList(this.items, Author.class.getClassLoader());
    }

    public static final Parcelable.Creator<AuthorList> CREATOR = new Parcelable.Creator<AuthorList>() {
        @Override
        public AuthorList createFromParcel(Parcel source) {
            return new AuthorList(source);
        }

        @Override
        public AuthorList[] newArray(int size) {
            return new AuthorList[size];
        }
    };
}
