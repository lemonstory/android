package com.xiaoningmeng.bean;

import java.util.List;

/**
 * Created by gaoyong on 16/11/16.
 */

public class AlbumRecommend {
    
    /**
     * age_level : {"total":4,"items":[{"age_level_str":"全部","min_age":0,"max_age":14,"album_num":434,"selected":1},{"age_level_str":"0-2岁","min_age":0,"max_age":2,"album_num":33,"selected":0},{"age_level_str":"3-6岁","min_age":3,"max_age":6,"album_num":93,"selected":0},{"age_level_str":"7-10岁","min_age":7,"max_age":10,"album_num":9,"selected":0}]}
     * total : 20
     * items : [{"id":"3894","title":"夏洛的网","cover":"http://p.xiaoningmeng.net/album/2016/11/15/5c8e07660e3000f141dda8b83107ed6e.png@!460x460?v=1479178446","listennum":"49","recommenddesc":"关于爱，关于友情，关于生死","age_str":"(3-10)岁","tag":{"id":"2","name":"故事"}},{"id":"4007","title":"发脾气的枕头","cover":"http://p.xiaoningmeng.net/album/2016/11/15/ab8df9f7733db5611fd53748f1db282e.png@!460x460?v=1479176716","listennum":"50","recommenddesc":"己所不欲，勿施于人","age_str":"(0-14)岁","tag":{"id":"2","name":"故事"}},{"id":"14120","title":"洋葱头历险记","cover":"http://p.xiaoningmeng.net/album/2016/11/14/0a343d089f3bd7ea28315af8b4370155.png@!460x460?v=1479111079","listennum":"288万","recommenddesc":"蔬菜的国度里洋葱的故事","age_str":"(7-10)岁","tag":{"id":"2","name":"故事"}},{"id":"14273","title":"披着被单的国王","cover":"http://p.xiaoningmeng.net/album/2016/11/14/f531dc20fde20b7adf3a73f52b71d0af.jpg@!460x460?v=1479112204","listennum":"96万","recommenddesc":"做一枚快乐的小栗子","age_str":"(3-6)岁","tag":{"id":"2","name":"故事"}},{"id":"4202","title":"鸡蛋哥哥","cover":"http://p.xiaoningmeng.net/album/2016/11/10/7d265aa7147bd3913fb84c7963a209d1.jpg@!460x460?v=1478772821","listennum":"560","recommenddesc":"蛋壳外的世界一样精彩","age_str":"(3-6)岁","tag":{"id":"2","name":"故事"}},{"id":"4326","title":"小馒头历险记","cover":"http://p.xiaoningmeng.net/album/2016/11/08/903356ae8ff820105d57f744c8a24396.jpg@!460x460?v=1478571678","listennum":"201","recommenddesc":"7岁女孩儿原创童话","age_str":"(0-14)岁","tag":{"id":"2","name":"故事"}},{"id":"4362","title":"奥蕊儿与发条娃娃","cover":"http://p.xiaoningmeng.net/album/2016/09/28/52c670999cdef4b09eb656850da777c4.jpg@!460x460?v=1475042668","listennum":"314","recommenddesc":"一个小女孩的奇幻历险故事","age_str":"(7-14)岁","tag":{"id":"2","name":"故事"}},{"id":"11057","title":"鳄鱼和长颈鹿","cover":"http://p.xiaoningmeng.net/album/2016/09/21/ca3f4ad48be4dc9ef210f96327e1331b.jpg@!460x460?v=1474444393","listennum":"180","recommenddesc":"鳄鱼先生爱上了长颈鹿","age_str":"(3-6)岁","tag":{"id":"2","name":"故事"}},{"id":"12711","title":"波普先生的企鹅","cover":"http://p.xiaoningmeng.net/album/2016/11/12/e18b6f179b6a5a068a01655542f9b6de.png@!460x460?v=1478947897","listennum":"100","recommenddesc":" 纽伯瑞儿童文学奖作品","age_str":"(7-10)岁","tag":{"id":"2","name":"故事"}},{"id":"13703","title":"列那狐的故事","cover":"http://p.xiaoningmeng.net/album/2016/11/11/967162a0b2dc0483ad3d0f47dfb1e9ac.jpg@!460x460?v=1478855980","listennum":"73","recommenddesc":"法国最经典的讽刺幽默作品","age_str":"(3-10)岁","tag":{"id":"2","name":"故事"}},{"id":"14082","title":"五毛钱的愿望","cover":"http://p.xiaoningmeng.net/album/2016/11/07/ee0f827fe45c91c956bacfd78d91d47b.png@!460x460?v=1478488559","listennum":"600万","recommenddesc":"只要花五毛钱就能买到愿望","age_str":"(0-14)岁","tag":{"id":"2","name":"故事"}},{"id":"14213","title":"糊涂村的糊涂猪","cover":"http://p.xiaoningmeng.net/album/2016/11/07/5a20abd7831fa1ebd199086e4a69a49a.png@!460x460?v=1478489430","listennum":"144万","recommenddesc":"糊涂猪遇到了许多奇怪的事","age_str":"(0-14)岁","tag":{"id":"2","name":"故事"}},{"id":"14312","title":"被拍卖的城堡","cover":"http://p.xiaoningmeng.net/album/2016/11/05/ef6fe50336005eee1e8e9093bfe576ae.jpg@!460x460?v=1478314661","listennum":"109","recommenddesc":"梦幻公主城堡的神奇体验","age_str":"(0-14)岁","tag":{"id":"2","name":"故事"}},{"id":"14411","title":"哎呀疼医生","cover":"http://p.xiaoningmeng.net/album/2016/11/05/e620a25ae37b9a8ba0362d5f02343161.png@!460x460?v=1478312630","listennum":"37671","recommenddesc":"唉呀疼医生会给小动物看病","age_str":"(0-14)岁","tag":{"id":"2","name":"故事"}},{"id":"14983","title":"蛋壳里出来的奶奶","cover":"http://p.xiaoningmeng.net/album/2016/11/04/ce6f311f2548c0e40fbd9beaaa288d32.png@!460x460?v=1478225376","listennum":"138万","recommenddesc":"从蛋中孵出一位微型奶奶","age_str":"(7-10)岁","tag":{"id":"2","name":"故事"}},{"id":"14997","title":"水孩子","cover":"http://p.xiaoningmeng.net/album/2016/11/04/ebd74b9b3bfd11deb539e4242d95078b.png@!460x460?v=1478227151","listennum":"92万","recommenddesc":"扫烟囱的孩子经历各种奇遇","age_str":"(7-10)岁","tag":{"id":"2","name":"故事"}},{"id":"15434","title":"听说蝴蝶飞过了云端","cover":"http://p.xiaoningmeng.net/album/2016/10/26/aaf9290b7570c56dd784f192425658d4.jpg@!460x460?v=1477417205","listennum":"1747","recommenddesc":"优雅地直面成长吧","age_str":"(3-10)岁","tag":{"id":"2","name":"故事"}},{"id":"15449","title":"白兔兔与灰兔兔","cover":"http://p.xiaoningmeng.net/album/2016/11/01/3a6dbc006a825f31dc1c530797798e43.png@!460x460?v=1477963194","listennum":"9571","recommenddesc":"魔鬼森林里最真诚的小动物","age_str":"(3-6)岁","tag":{"id":"2","name":"故事"}},{"id":"15450","title":"帽子里的秘密","cover":"http://p.xiaoningmeng.net/album/2016/10/31/90e2217cd253377081205243ac24b03a.png@!460x460?v=1477909726","listennum":"4368","recommenddesc":"木青在理发店里认识了很多好朋友","age_str":"(7-10)岁","tag":{"id":"2","name":"故事"}},{"id":"15469","title":"七月、七星和会说话的机器猫","cover":"http://p.xiaoningmeng.net/album/2016/10/29/8003c8cdbcb8ca55652d4b2c5569d748.jpg@!460x460?v=1477718294","listennum":"7825","recommenddesc":"七星与七月兄妹在舅舅家开始了新的生活","age_str":"(3-6)岁","tag":{"id":"2","name":"故事"}}]
     */

    private AgeLevelBean age_level;
    private String total;
    private List<AlbumInfo> items;

    public AgeLevelBean getAge_level() {
        return age_level;
    }

    public void setAge_level(AgeLevelBean age_level) {
        this.age_level = age_level;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<AlbumInfo> getItems() {
        return items;
    }

    public void setItems(List<AlbumInfo> items) {
        this.items = items;
    }

    public static class AgeLevelBean {
        /**
         * total : 4
         * items : [{"age_level_str":"全部","min_age":0,"max_age":14,"album_num":434,"selected":1},{"age_level_str":"0-2岁","min_age":0,"max_age":2,"album_num":33,"selected":0},{"age_level_str":"3-6岁","min_age":3,"max_age":6,"album_num":93,"selected":0},{"age_level_str":"7-10岁","min_age":7,"max_age":10,"album_num":9,"selected":0}]
         */

        private String total;
        private List<AgeLevelItemBean> items;

        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
        }

        public List<AgeLevelItemBean> getItems() {
            return items;
        }

        public void setItems(List<AgeLevelItemBean> items) {
            this.items = items;
        }

        public static class AgeLevelItemBean {
            /**
             * age_level_str : 全部
             * min_age : 0
             * max_age : 14
             * album_num : 434
             * selected : 1
             */

            private String age_level_str;
            private String min_age;
            private String max_age;
            private String album_num;
            private int selected;

            public String getAge_level_str() {
                return age_level_str;
            }

            public void setAge_level_str(String age_level_str) {
                this.age_level_str = age_level_str;
            }

            public String getMin_age() {
                return min_age;
            }

            public void setMin_age(String min_age) {
                this.min_age = min_age;
            }

            public String getMax_age() {
                return max_age;
            }

            public void setMax_age(String max_age) {
                this.max_age = max_age;
            }

            public String getAlbum_num() {
                return album_num;
            }

            public void setAlbum_num(String album_num) {
                this.album_num = album_num;
            }

            public int getSelected() {
                return selected;
            }

            public void setSelected(int selected) {
                this.selected = selected;
            }
        }
    }
}
