package com.xiaoningmeng.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gaoyong on 16/4/6.
 */
public class ShopItem implements Parcelable {

    private int proSort;
    private String proName;
    private String proRemark;

    private int cid;
    private boolean istk;
    private boolean ju_item;
    private boolean mall;
    private String open_auction_iid;
    private Number open_id;
    private String open_iid;
    private String pic_url;
    private String post_fee;
    private String price;
    private String price_wap;
    private String promoted_service;
    private String reserve_price;
    private String title;
    private String tk_rate;

    public ShopItem(int proSort, String proName, String proRemark, int cid, boolean istk, boolean ju_item, boolean mall, String open_auction_iid, Number open_id, String open_iid, String pic_url, String post_fee, String price, String price_wap, String promoted_service, String reserve_price, String title, String tk_rate) {

        this.proSort = proSort;
        this.proName = proName;
        this.proRemark = proRemark;
        this.cid = cid;
        this.istk = istk;
        this.ju_item = ju_item;
        this.mall = mall;
        this.open_auction_iid = open_auction_iid;
        this.open_id = open_id;
        this.open_iid = open_iid;
        this.pic_url = pic_url;
        this.post_fee = post_fee;
        this.price = price;
        this.price_wap = price_wap;
        this.promoted_service = promoted_service;
        this.reserve_price = reserve_price;
        this.title = title;
        this.tk_rate = tk_rate;
    }

    protected ShopItem(Parcel in) {
        proSort = in.readInt();
        proName = in.readString();
        proRemark = in.readString();
        cid = in.readInt();
        istk = in.readByte() != 0;
        ju_item = in.readByte() != 0;
        mall = in.readByte() != 0;
        open_auction_iid = in.readString();
        open_iid = in.readString();
        pic_url = in.readString();
        post_fee = in.readString();
        price = in.readString();
        price_wap = in.readString();
        promoted_service = in.readString();
        reserve_price = in.readString();
        title = in.readString();
        tk_rate = in.readString();
    }

    public static final Creator<ShopItem> CREATOR = new Creator<ShopItem>() {
        @Override
        public ShopItem createFromParcel(Parcel in) {
            return new ShopItem(in);
        }

        @Override
        public ShopItem[] newArray(int size) {
            return new ShopItem[size];
        }
    };

    public int getProSort() {
        return proSort;
    }

    public void setProSort(int proSort) {
        this.proSort = proSort;
    }

    public String getProName() {
        return proName;
    }

    public void setProName(String proName) {
        this.proName = proName;
    }

    public String getProRemark() {
        return proRemark;
    }

    public void setProRemark(String proRemark) {
        this.proRemark = proRemark;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public boolean istk() {
        return istk;
    }

    public void setIstk(boolean istk) {
        this.istk = istk;
    }

    public boolean isJu_item() {
        return ju_item;
    }

    public void setJu_item(boolean ju_item) {
        this.ju_item = ju_item;
    }

    public boolean isMall() {
        return mall;
    }

    public void setMall(boolean mall) {
        this.mall = mall;
    }

    public String getOpen_auction_iid() {
        return open_auction_iid;
    }

    public void setOpen_auction_iid(String open_auction_iid) {
        this.open_auction_iid = open_auction_iid;
    }

    public Number getOpen_id() {
        return open_id;
    }

    public void setOpen_id(Number open_id) {
        this.open_id = open_id;
    }

    public String getOpen_iid() {
        return open_iid;
    }

    public void setOpen_iid(String open_iid) {
        this.open_iid = open_iid;
    }

    public String getPic_url() {
        return pic_url;
    }

    public void setPic_url(String pic_url) {
        this.pic_url = pic_url;
    }

    public String getPost_fee() {
        return post_fee;
    }

    public void setPost_fee(String post_fee) {
        this.post_fee = post_fee;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPrice_wap() {
        return price_wap;
    }

    public void setPrice_wap(String price_wap) {
        this.price_wap = price_wap;
    }

    public String getPromoted_service() {
        return promoted_service;
    }

    public void setPromoted_service(String promoted_service) {
        this.promoted_service = promoted_service;
    }

    public String getReserve_price() {
        return reserve_price;
    }

    public void setReserve_price(String reserve_price) {
        this.reserve_price = reserve_price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTk_rate() {
        return tk_rate;
    }

    public void setTk_rate(String tk_rate) {
        this.tk_rate = tk_rate;
    }

    @Override
    public String toString() {
        return "ShopItem{" +
                "proSort=" + proSort +
                ", proName='" + proName + '\'' +
                ", proRemark='" + proRemark + '\'' +
                ", cid=" + cid +
                ", istk=" + istk +
                ", ju_item=" + ju_item +
                ", mall=" + mall +
                ", open_auction_iid='" + open_auction_iid + '\'' +
                ", open_id=" + open_id +
                ", open_iid='" + open_iid + '\'' +
                ", pic_url='" + pic_url + '\'' +
                ", post_fee='" + post_fee + '\'' +
                ", price='" + price + '\'' +
                ", price_wap='" + price_wap + '\'' +
                ", promoted_service='" + promoted_service + '\'' +
                ", reserve_price='" + reserve_price + '\'' +
                ", title='" + title + '\'' +
                ", tk_rate='" + tk_rate + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(proSort);
        dest.writeString(proName);
        dest.writeString(proRemark);
        dest.writeInt(cid);
        dest.writeByte((byte) (istk ? 1 : 0));
        dest.writeByte((byte) (ju_item ? 1 : 0));
        dest.writeByte((byte) (mall ? 1 : 0));
        dest.writeString(open_auction_iid);
        dest.writeString(open_iid);
        dest.writeString(pic_url);
        dest.writeString(post_fee);
        dest.writeString(price);
        dest.writeString(price_wap);
        dest.writeString(promoted_service);
        dest.writeString(reserve_price);
        dest.writeString(title);
        dest.writeString(tk_rate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ShopItem item = (ShopItem) o;

        return open_iid.equals(item.open_iid);

    }

    @Override
    public int hashCode() {
        return open_iid.hashCode();
    }
}
