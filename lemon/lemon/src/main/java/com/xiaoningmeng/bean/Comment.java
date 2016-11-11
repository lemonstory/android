package com.xiaoningmeng.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by gaoyong on 16/11/10.
 */

public class Comment implements Parcelable {

    /**
     * total : 2
     * items : [{"id":"8686","uid":"10032","uname":"格格","avatar":"http://a.xiaoningmeng.net/avatar/10032/1448438048/120","start_level":"4","addtime":"2016-10-08 08:48:19","comment":"好作品，好有意思."},{"id":"8685","uid":"10011","uname":"不萌萌哒","avatar":"http://a.xiaoningmeng.net/avatar/10011/1448441203/120","start_level":"4","addtime":"2016-10-07 14:58:26","comment":"声音真好听"}]
     */

    private String total;
    private List<ItemsBean> items;

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<ItemsBean> getItems() {
        return items;
    }

    public void setItems(List<ItemsBean> items) {
        this.items = items;
    }

    public static class ItemsBean implements Parcelable {
        /**
         * id : 8686
         * uid : 10032
         * uname : 格格
         * avatar : http://a.xiaoningmeng.net/avatar/10032/1448438048/120
         * start_level : 4
         * addtime : 2016-10-08 08:48:19
         * comment : 好作品，好有意思.
         */

        private String id;
        private String uid;
        private String uname;
        private String avatar;
        private String start_level;
        private String addtime;
        private String comment;

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

        public String getUname() {
            return uname;
        }

        public void setUname(String uname) {
            this.uname = uname;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getStart_level() {
            return start_level;
        }

        public void setStart_level(String start_level) {
            this.start_level = start_level;
        }

        public String getAddtime() {
            return addtime;
        }

        public void setAddtime(String addtime) {
            this.addtime = addtime;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.id);
            dest.writeString(this.uid);
            dest.writeString(this.uname);
            dest.writeString(this.avatar);
            dest.writeString(this.start_level);
            dest.writeString(this.addtime);
            dest.writeString(this.comment);
        }

        public ItemsBean() {
        }

        protected ItemsBean(Parcel in) {
            this.id = in.readString();
            this.uid = in.readString();
            this.uname = in.readString();
            this.avatar = in.readString();
            this.start_level = in.readString();
            this.addtime = in.readString();
            this.comment = in.readString();
        }

        public static final Parcelable.Creator<ItemsBean> CREATOR = new Parcelable.Creator<ItemsBean>() {
            @Override
            public ItemsBean createFromParcel(Parcel source) {
                return new ItemsBean(source);
            }

            @Override
            public ItemsBean[] newArray(int size) {
                return new ItemsBean[size];
            }
        };

        @Override
        public String toString() {
            return "ItemsBean{" +
                    "id='" + id + '\'' +
                    ", uid='" + uid + '\'' +
                    ", uname='" + uname + '\'' +
                    ", avatar='" + avatar + '\'' +
                    ", start_level='" + start_level + '\'' +
                    ", addtime='" + addtime + '\'' +
                    ", comment='" + comment + '\'' +
                    '}';
        }
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

    public Comment() {
    }

    protected Comment(Parcel in) {
        this.total = in.readString();
        this.items = in.createTypedArrayList(ItemsBean.CREATOR);
    }

    public static final Parcelable.Creator<Comment> CREATOR = new Parcelable.Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel source) {
            return new Comment(source);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };

    @Override
    public String toString() {
        return "Comment{" +
                "total='" + total + '\'' +
                ", items=" + items +
                '}';
    }
}
