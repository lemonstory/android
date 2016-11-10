package com.xiaoningmeng.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by gaoyong on 16/11/10.
 */

public class Comment {
    /**
     * code : 10000
     * data : {"total":"57","items":[{"id":"8344","uid":"14634","uname":"罄葶靛雨","avatar":"http://a.xiaoningmeng.net/avatar/14634/1470577091/120","start_level":"5","addtime":"2016-09-17 13:06:59","comment":"666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666"},{"id":"8326","uid":"14449","uname":"王子昊","avatar":"http://a.xiaoningmeng.net/avatar/14449/1468207681/120","start_level":"5","addtime":"2016-07-28 12:35:25","comment":"6666666666666666666666666666666666636666666666666666666666666666666666669666666666666636633663333333333336666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666636666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666336666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666663363663666666666666666666666666666666666666"},{"id":"8319","uid":"14543","uname":"≮无哈哈哈≯","avatar":"http://a.xiaoningmeng.net/avatar/14543/1469249782/120","start_level":"5","addtime":"2016-07-23 12:56:31","comment":"666666666666"},{"id":"8310","uid":"13792","uname":"超人","avatar":"http://a.xiaoningmeng.net/avatar/13792/1466298891/120","start_level":"0","addtime":"2016-07-16 20:36:34","comment":"66666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"},{"id":"8301","uid":"13232","uname":"蓝慧","avatar":"http://a.xiaoningmeng.net/avatar/13232/1459820352/120","start_level":"0","addtime":"2016-07-04 13:55:31","comment":"66666669966666666666969696666666"},{"id":"8288","uid":"13792","uname":"超人","avatar":"http://a.xiaoningmeng.net/avatar/13792/1466298891/120","start_level":"5","addtime":"2016-06-24 22:26:43","comment":"66666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666＝！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！"},{"id":"8279","uid":"13836","uname":"YAN","avatar":"http://a.xiaoningmeng.net/avatar/13836/1466333801/120","start_level":"5","addtime":"2016-06-19 18:58:55","comment":"666666"},{"id":"8278","uid":"13836","uname":"YAN","avatar":"http://a.xiaoningmeng.net/avatar/13836/1466333801/120","start_level":"0","addtime":"2016-06-19 18:58:36","comment":"66666"},{"id":"8275","uid":"13769","uname":"熊熊山火不燃万年冰山","avatar":"http://a.xiaoningmeng.net/avatar/13769/1466257921/120","start_level":"5","addtime":"2016-06-19 13:47:30","comment":"66666666666666666666666666666666666666666"},{"id":"8274","uid":"13769","uname":"熊熊山火不燃万年冰山","avatar":"http://a.xiaoningmeng.net/avatar/13769/1466257921/120","start_level":"5","addtime":"2016-06-19 13:46:52","comment":"66666666666666666666666666666666666666666666666666"},{"id":"8264","uid":"13724","uname":"小宇妈妈（已）","avatar":"http://a.xiaoningmeng.net/avatar/13724/1466215812/120","start_level":"5","addtime":"2016-06-18 11:37:36","comment":"非常不错！宝宝很喜欢听！"},{"id":"8261","uid":"13693","uname":"天使的眼泪","avatar":"http://a.xiaoningmeng.net/avatar/13693/1466168061/120","start_level":"0","addtime":"2016-06-17 20:54:55","comment":"好好好好好好"},{"id":"8210","uid":"13195","uname":"℡安于现状℃","avatar":"http://a.xiaoningmeng.net/avatar/13195/1459601542/120","start_level":"5","addtime":"2016-04-11 12:53:43","comment":"很喜欢!"},{"id":"8205","uid":"13050","uname":"玲_13050","avatar":"http://a.xiaoningmeng.net/avatar/13050/1458476401/120","start_level":"5","addtime":"2016-04-06 21:13:27","comment":""},{"id":"8184","uid":"12879","uname":"雨打竹林","avatar":"http://a.xiaoningmeng.net/avatar/12879/1457138032/120","start_level":"0","addtime":"2016-03-27 11:44:23","comment":"额戶邋（︶︿︶）=凸兤这衍排"},{"id":"8183","uid":"12879","uname":"雨打竹林","avatar":"http://a.xiaoningmeng.net/avatar/12879/1457138032/120","start_level":"0","addtime":"2016-03-27 11:41:17","comment":"一人簿综孩"},{"id":"8181","uid":"12879","uname":"雨打竹林","avatar":"http://a.xiaoningmeng.net/avatar/12879/1457138032/120","start_level":"5","addtime":"2016-03-27 11:37:03","comment":"\u2026\u2026\u2026\u2026\u2026\u2026\u2026\u2026\u2026\u2026\u2026\u2026～\u2026\u2026；\u201c\u201d\u201d\u2026\u2026\u2026\u2026\u2026\u2026\u2026\u2026\u2026\u2026\u2026\u2026：：：：.ԅ(¯ㅂ¯ԅ)_(:з」∠)_ԅ(¯ㅂ¯ԅ)(๑>؂<๑）(●\u2014●)(づ ●─● )づ（｡ò ∀ ó｡）（｡ò ∀ ó｡）ԅ(¯ㅂ¯ԅ)(●\u2014●)ԅ(¯ㅂ¯ԅ)（｡ò ∀ ó｡）( ｰ̀εｰ́ )(๑>؂<๑）"},{"id":"8180","uid":"12879","uname":"雨打竹林","avatar":"http://a.xiaoningmeng.net/avatar/12879/1457138032/120","start_level":"0","addtime":"2016-03-27 11:34:34","comment":"方法一个哥哥一个丰富的-不v发广告"},{"id":"8179","uid":"12879","uname":"雨打竹林","avatar":"http://a.xiaoningmeng.net/avatar/12879/1457138032/120","start_level":"5","addtime":"2016-03-26 22:49:27","comment":"逃跑"},{"id":"8177","uid":"12879","uname":"雨打竹林","avatar":"http://a.xiaoningmeng.net/avatar/12879/1457138032/120","start_level":"5","addtime":"2016-03-26 22:42:17","comment":"(\u203a´ω`\u2039 )"}]}
     */

    private int code;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * total : 57
         * items : [{"id":"8344","uid":"14634","uname":"罄葶靛雨","avatar":"http://a.xiaoningmeng.net/avatar/14634/1470577091/120","start_level":"5","addtime":"2016-09-17 13:06:59","comment":"666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666"},{"id":"8326","uid":"14449","uname":"王子昊","avatar":"http://a.xiaoningmeng.net/avatar/14449/1468207681/120","start_level":"5","addtime":"2016-07-28 12:35:25","comment":"6666666666666666666666666666666666636666666666666666666666666666666666669666666666666636633663333333333336666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666636666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666336666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666663363663666666666666666666666666666666666666"},{"id":"8319","uid":"14543","uname":"≮无哈哈哈≯","avatar":"http://a.xiaoningmeng.net/avatar/14543/1469249782/120","start_level":"5","addtime":"2016-07-23 12:56:31","comment":"666666666666"},{"id":"8310","uid":"13792","uname":"超人","avatar":"http://a.xiaoningmeng.net/avatar/13792/1466298891/120","start_level":"0","addtime":"2016-07-16 20:36:34","comment":"66666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"},{"id":"8301","uid":"13232","uname":"蓝慧","avatar":"http://a.xiaoningmeng.net/avatar/13232/1459820352/120","start_level":"0","addtime":"2016-07-04 13:55:31","comment":"66666669966666666666969696666666"},{"id":"8288","uid":"13792","uname":"超人","avatar":"http://a.xiaoningmeng.net/avatar/13792/1466298891/120","start_level":"5","addtime":"2016-06-24 22:26:43","comment":"66666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666＝！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！"},{"id":"8279","uid":"13836","uname":"YAN","avatar":"http://a.xiaoningmeng.net/avatar/13836/1466333801/120","start_level":"5","addtime":"2016-06-19 18:58:55","comment":"666666"},{"id":"8278","uid":"13836","uname":"YAN","avatar":"http://a.xiaoningmeng.net/avatar/13836/1466333801/120","start_level":"0","addtime":"2016-06-19 18:58:36","comment":"66666"},{"id":"8275","uid":"13769","uname":"熊熊山火不燃万年冰山","avatar":"http://a.xiaoningmeng.net/avatar/13769/1466257921/120","start_level":"5","addtime":"2016-06-19 13:47:30","comment":"66666666666666666666666666666666666666666"},{"id":"8274","uid":"13769","uname":"熊熊山火不燃万年冰山","avatar":"http://a.xiaoningmeng.net/avatar/13769/1466257921/120","start_level":"5","addtime":"2016-06-19 13:46:52","comment":"66666666666666666666666666666666666666666666666666"},{"id":"8264","uid":"13724","uname":"小宇妈妈（已）","avatar":"http://a.xiaoningmeng.net/avatar/13724/1466215812/120","start_level":"5","addtime":"2016-06-18 11:37:36","comment":"非常不错！宝宝很喜欢听！"},{"id":"8261","uid":"13693","uname":"天使的眼泪","avatar":"http://a.xiaoningmeng.net/avatar/13693/1466168061/120","start_level":"0","addtime":"2016-06-17 20:54:55","comment":"好好好好好好"},{"id":"8210","uid":"13195","uname":"℡安于现状℃","avatar":"http://a.xiaoningmeng.net/avatar/13195/1459601542/120","start_level":"5","addtime":"2016-04-11 12:53:43","comment":"很喜欢!"},{"id":"8205","uid":"13050","uname":"玲_13050","avatar":"http://a.xiaoningmeng.net/avatar/13050/1458476401/120","start_level":"5","addtime":"2016-04-06 21:13:27","comment":""},{"id":"8184","uid":"12879","uname":"雨打竹林","avatar":"http://a.xiaoningmeng.net/avatar/12879/1457138032/120","start_level":"0","addtime":"2016-03-27 11:44:23","comment":"额戶邋（︶︿︶）=凸兤这衍排"},{"id":"8183","uid":"12879","uname":"雨打竹林","avatar":"http://a.xiaoningmeng.net/avatar/12879/1457138032/120","start_level":"0","addtime":"2016-03-27 11:41:17","comment":"一人簿综孩"},{"id":"8181","uid":"12879","uname":"雨打竹林","avatar":"http://a.xiaoningmeng.net/avatar/12879/1457138032/120","start_level":"5","addtime":"2016-03-27 11:37:03","comment":"\u2026\u2026\u2026\u2026\u2026\u2026\u2026\u2026\u2026\u2026\u2026\u2026～\u2026\u2026；\u201c\u201d\u201d\u2026\u2026\u2026\u2026\u2026\u2026\u2026\u2026\u2026\u2026\u2026\u2026：：：：.ԅ(¯ㅂ¯ԅ)_(:з」∠)_ԅ(¯ㅂ¯ԅ)(๑>؂<๑）(●\u2014●)(づ ●─● )づ（｡ò ∀ ó｡）（｡ò ∀ ó｡）ԅ(¯ㅂ¯ԅ)(●\u2014●)ԅ(¯ㅂ¯ԅ)（｡ò ∀ ó｡）( ｰ̀εｰ́ )(๑>؂<๑）"},{"id":"8180","uid":"12879","uname":"雨打竹林","avatar":"http://a.xiaoningmeng.net/avatar/12879/1457138032/120","start_level":"0","addtime":"2016-03-27 11:34:34","comment":"方法一个哥哥一个丰富的-不v发广告"},{"id":"8179","uid":"12879","uname":"雨打竹林","avatar":"http://a.xiaoningmeng.net/avatar/12879/1457138032/120","start_level":"5","addtime":"2016-03-26 22:49:27","comment":"逃跑"},{"id":"8177","uid":"12879","uname":"雨打竹林","avatar":"http://a.xiaoningmeng.net/avatar/12879/1457138032/120","start_level":"5","addtime":"2016-03-26 22:42:17","comment":"(\u203a´ω`\u2039 )"}]
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
             * id : 8344
             * uid : 14634
             * uname : 罄葶靛雨
             * avatar : http://a.xiaoningmeng.net/avatar/14634/1470577091/120
             * start_level : 5
             * addtime : 2016-09-17 13:06:59
             * comment : 666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666
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
    }


    @Override
    public String toString() {
        return "Comment{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }
}
