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

    private FocusPicBean focus_pic;
    private ContentCategoryBean content_category;
    private AlbumSectionBean album_section;
    private AdBean ad;

    public FocusPicBean getFocus_pic() {
        return focus_pic;
    }

    public void setFocus_pic(FocusPicBean focus_pic) {
        this.focus_pic = focus_pic;
    }


    public ContentCategoryBean getContent_category() {
        return content_category;
    }

    public void setContent_category(ContentCategoryBean content_category) {
        this.content_category = content_category;
    }

    public AlbumSectionBean getAlbum_section() {
        return album_section;
    }

    public void setAlbum_section(AlbumSectionBean album_section) {
        this.album_section = album_section;
    }

    public AdBean getAd() {
        return ad;
    }

    public void setAd(AdBean ad) {
        this.ad = ad;
    }

    public static class FocusPicBean implements IRecyclerItem {
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

    public static class ContentCategoryBean {

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

    public static class AlbumSectionBean {


        private List<ItemBean> items;

        public List<ItemBean> getItems() {
            return items;
        }

        public void setItems(List<ItemBean> items) {
            this.items = items;
        }


        public static class ItemBean implements IRecyclerItem {

            private List<AlbumInfo> items;
            private String tag_id;
            private String title;
            private String linkurl;


            public List<AlbumInfo> getItems() {
                return items;
            }

            public void setItems(List<AlbumInfo> items) {
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

            public String getTag_id() {
                return tag_id;
            }

            public void setTag_id(String tag_id) {
                this.tag_id = tag_id;
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
}
