package com.xiaoningmeng.bean;


import java.util.List;

/**
 * Created by huang on 2016/10/15.
 */

public class Index {

    public static final int BANNER_TYPE = 0;
    public static final int CATEGORY_TYPE = 1;
    public static final int ALBUM_MORE_TYPE = 2;
    public static final int ALBUM_TYPE = 3;
    public static final int AD_TYPE = 4;
    public static final int AUTHOR_TYPE = 5;

    private FocusBean focus;
    private CategoryBean category;
    private SectionBean section;
    private AdBean ad;
//    private MoreItemBean<AuthorItemBean> author_section;

    public FocusBean getFocus() {
        return focus;
    }

    public void setFocus(FocusBean focus) {
        this.focus = focus;
    }


    public CategoryBean getCategory() {
        return category;
    }

    public void setCategory(CategoryBean category) {
        this.category = category;
    }

    public SectionBean getSection() {
        return section;
    }

    public void setSection(SectionBean section) {
        this.section = section;
    }

    public AdBean getAd() {
        return ad;
    }

    public void setAd(AdBean ad) {
        this.ad = ad;
    }

//    public MoreItemBean<AuthorItemBean> getAuthor_section() {
//        return author_section;
//    }
//
//    public void setAuthor_section(MoreItemBean<AuthorItemBean> author_section) {
//        this.author_section = author_section;
//    }

    public static class FocusBean implements IRecyclerItem {
        private int total;
        /**
         * cover : http://p.xiaoningmeng.net/focus/32.png@!640x260?v=1475734342
         * linkurl : https://item.taobao.com/item.htm?spm=a1z10.3-c.w4002-15121060416.33.P6UR58&id=538647555934
         */

        private List<ItemsBean> items;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public List<ItemsBean> getItems() {
            return items;
        }

        public void setItems(List<ItemsBean> items) {
            this.items = items;
        }


        @Override
        public int getSpanSize() {
            return 4;
        }

        @Override
        public int getItemType() {
            return BANNER_TYPE;
        }

        public static class ItemsBean {
            private String cover;
            private String linkurl;


            public String getCover() {
                return cover;
            }

            public void setCover(String cover) {
                this.cover = cover;
            }

            public String getLinkurl() {
                return linkurl;
            }

            public void setLinkurl(String linkurl) {
                this.linkurl = linkurl;
            }


        }
    }

    public static class CategoryBean {

        private int total;
        private List<ItemBean> items;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public List<ItemBean> getItems() {
            return items;
        }

        public void setItems(List<ItemBean> items) {
            this.items = items;
        }

        public static class ItemBean implements IRecyclerItem {
            private String cover;
            private String title;
            private String linkurl;

            @Override
            public int getSpanSize() {
                return 1;
            }

            @Override
            public int getItemType() {
                return CATEGORY_TYPE;
            }

            public String getCover() {
                return cover;
            }

            public void setCover(String cover) {
                this.cover = cover;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getLinkurl() {
                return linkurl;
            }

            public void setLinkurl(String linkurl) {
                this.linkurl = linkurl;
            }
        }
    }

    public static class SectionBean {


        private List<SectionItemBean> items;

        public List<SectionItemBean> getItems() {

            return items;
        }

        public void setItems(List<SectionItemBean> items) {
            this.items = items;
        }
    }

    public static class SectionItemBean<T> implements IRecyclerItem {

        private String type;
        private String title;
        private int total;
        private String linkurl;
        private List<T> items;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public List<T> getItems() {
            return items;
        }

        public void setItems(List<T> items) {
            this.items = items;
        }

        @Override
        public int getSpanSize() {
            return 4;
        }

        @Override
        public int getItemType() {
            return ALBUM_MORE_TYPE;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getLinkurl() {
            return linkurl;
        }

        public void setLinkurl(String linkurl) {
            this.linkurl = linkurl;
        }
    }

    public static class AdBean {
        private int total;

        private List<ItemsBean> items;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public List<ItemsBean> getItems() {
            return items;
        }

        public void setItems(List<ItemsBean> items) {
            this.items = items;
        }


        public static class ItemsBean implements IRecyclerItem {
            private String cover;
            private String linkurl;

            public String getCover() {
                return cover;
            }

            public void setCover(String cover) {
                this.cover = cover;
            }

            public String getLinkurl() {
                return linkurl;
            }

            public void setLinkurl(String linkurl) {
                this.linkurl = linkurl;
            }

            @Override
            public int getSpanSize() {
                return 4;
            }

            @Override
            public int getItemType() {
                return AD_TYPE;
            }
        }
    }

    public static class AuthorItemBean extends Author implements IRecyclerItem {

        @Override
        public int getSpanSize() {
            return 1;
        }

        @Override
        public int getItemType() {
            return AUTHOR_TYPE;
        }

        public AuthorItemBean(String uid, String nickname,  String avatar, String wiki_url) {

            super(uid, nickname,  avatar, wiki_url);
        }
    }
}
