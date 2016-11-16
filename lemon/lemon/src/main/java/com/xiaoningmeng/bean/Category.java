package com.xiaoningmeng.bean;

import java.util.List;

/**
 * 全部分类
 */
public class Category {

    public static final int TYPE_AGE_LEVEL = 0;
    public static final int TYPE_SECTION = 1;
    public static final int TYPE_TAG = 2;


    /**
     * age_level : {"total":4,"items":[{"title":"0-2岁","cover":"http://p.xiaoningmeng.net/age_level/0-2.png","link":"xnm://api.xiaoningmeng.net/default/v2.6/age_level_list.php?min_age=0&min_age=2"},{"title":"3-6岁","cover":"http://p.xiaoningmeng.net/age_level/3-6.png","link":"xnm://api.xiaoningmeng.net/default/v2.6/age_level_list.php?min_age=3&min_age=6"},{"title":"7-10岁","cover":"http://p.xiaoningmeng.net/age_level/7-10.png","link":"xnm://api.xiaoningmeng.net/default/v2.6/age_level_list.php?min_age=7&min_age=10"},{"title":"11-14岁","cover":"http://p.xiaoningmeng.net/age_level/11-14.png","link":"xnm://api.xiaoningmeng.net/default/v2.6/age_level_list.php?min_age=11&min_age=14"}]}
     * tag : {"total":8,"items":[{"id":"2","name":"故事","child_total":6,"child_items":[{"id":"23","name":"睡前故事","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=23"},{"id":"158","name":"童话寓言","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=158"},{"id":"27","name":"女孩故事","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=27"},{"id":"25","name":"男孩故事","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=25"},{"id":"156","name":"神话传说","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=156"},{"id":"29","name":"动物故事","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=29"}]},{"id":"1","name":"儿歌","child_total":3,"child_items":[{"id":"13","name":"经典儿歌","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=13"},{"id":"18","name":"韵律童谣","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=18"},{"id":"20","name":"绕口令","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=20"}]},{"id":"5","name":"国学","child_total":4,"child_items":[{"id":"45","name":"古诗精选","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=45"},{"id":"46","name":"宋词精选","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=46"},{"id":"47","name":"国学启蒙","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=47"},{"id":"49","name":"传统经典","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=49"}]},{"id":"4","name":"英语","child_total":7,"child_items":[{"id":"42","name":"英语儿歌","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=42"},{"id":"138","name":"英语绘本","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=138"},{"id":"44","name":"英语故事","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=44"},{"id":"40","name":"英语童谣","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=40"},{"id":"41","name":"英语启蒙","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=41"},{"id":"43","name":"趣味单词","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=43"},{"id":"129","name":"儿童英语","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=129"}]},{"id":"137","name":"绘本","child_total":4,"child_items":[{"id":"133","name":"绘本故事","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=133"},{"id":"139","name":"爱与情感","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=139"},{"id":"140","name":"生活成长","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=140"},{"id":"141","name":"获奖经典","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=141"}]},{"id":"3","name":"音乐","child_total":3,"child_items":[{"id":"35","name":"睡前音乐","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=35"},{"id":"36","name":"音乐启蒙","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=36"},{"id":"34","name":"古典音乐","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=34"}]},{"id":"9","name":"科普","child_total":8,"child_items":[{"id":"115","name":"百科知识","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=115"},{"id":"116","name":"生活常识","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=116"},{"id":"117","name":"自然知识","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=117"},{"id":"118","name":"科学知识","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=118"},{"id":"119","name":"安全知识","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=119"},{"id":"127","name":"儿童读物","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=127"},{"id":"128","name":"儿童学习","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=128"},{"id":"130","name":"儿童科普","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=130"}]},{"id":"153","name":"育儿","child_total":3,"child_items":[{"id":"131","name":"儿童教育","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=131"},{"id":"163","name":"育儿宝典","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=163"},{"id":"181","name":"怀孕安产","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=181"}]}]}
     */

    private AgeLevelBean age_level;
    private TagBean tag;

    public AgeLevelBean getAge_level() {
        return age_level;
    }

    public void setAge_level(AgeLevelBean age_level) {
        this.age_level = age_level;
    }

    public TagBean getTag() {
        return tag;
    }

    public void setTag(TagBean tag) {
        this.tag = tag;
    }

    public static class AgeLevelBean {
        /**
         * total : 4
         * items : [{"title":"0-2岁","cover":"http://p.xiaoningmeng.net/age_level/0-2.png","link":"xnm://api.xiaoningmeng.net/default/v2.6/age_level_list.php?min_age=0&min_age=2"},{"title":"3-6岁","cover":"http://p.xiaoningmeng.net/age_level/3-6.png","link":"xnm://api.xiaoningmeng.net/default/v2.6/age_level_list.php?min_age=3&min_age=6"},{"title":"7-10岁","cover":"http://p.xiaoningmeng.net/age_level/7-10.png","link":"xnm://api.xiaoningmeng.net/default/v2.6/age_level_list.php?min_age=7&min_age=10"},{"title":"11-14岁","cover":"http://p.xiaoningmeng.net/age_level/11-14.png","link":"xnm://api.xiaoningmeng.net/default/v2.6/age_level_list.php?min_age=11&min_age=14"}]
         */

        private String total;
        private List<AgeItemsBean> items;

        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
        }

        public List<AgeItemsBean> getItems() {
            return items;
        }

        public void setItems(List<AgeItemsBean> items) {
            this.items = items;
        }


        public static class AgeItemsBean implements IRecyclerItem {
            /**
             * title : 0-2岁
             * cover : http://p.xiaoningmeng.net/age_level/0-2.png
             * link : xnm://api.xiaoningmeng.net/default/v2.6/age_level_list.php?min_age=0&min_age=2
             */

            private String title;
            private String cover;
            private String link;

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getCover() {
                return cover;
            }

            public void setCover(String cover) {
                this.cover = cover;
            }

            public String getLink() {
                return link;
            }

            public void setLink(String link) {
                this.link = link;
            }

            @Override
            public int getSpanSize() {
                return 1;
            }

            @Override
            public int getItemType() {
                return Category.TYPE_AGE_LEVEL;
            }
        }

        public AgeLevelBean() {

        }
    }

    public static class TagBean {
        /**
         * total : 8
         * items : [{"id":"2","name":"故事","child_total":6,"child_items":[{"id":"23","name":"睡前故事","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=23"},{"id":"158","name":"童话寓言","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=158"},{"id":"27","name":"女孩故事","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=27"},{"id":"25","name":"男孩故事","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=25"},{"id":"156","name":"神话传说","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=156"},{"id":"29","name":"动物故事","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=29"}]},{"id":"1","name":"儿歌","child_total":3,"child_items":[{"id":"13","name":"经典儿歌","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=13"},{"id":"18","name":"韵律童谣","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=18"},{"id":"20","name":"绕口令","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=20"}]},{"id":"5","name":"国学","child_total":4,"child_items":[{"id":"45","name":"古诗精选","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=45"},{"id":"46","name":"宋词精选","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=46"},{"id":"47","name":"国学启蒙","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=47"},{"id":"49","name":"传统经典","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=49"}]},{"id":"4","name":"英语","child_total":7,"child_items":[{"id":"42","name":"英语儿歌","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=42"},{"id":"138","name":"英语绘本","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=138"},{"id":"44","name":"英语故事","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=44"},{"id":"40","name":"英语童谣","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=40"},{"id":"41","name":"英语启蒙","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=41"},{"id":"43","name":"趣味单词","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=43"},{"id":"129","name":"儿童英语","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=129"}]},{"id":"137","name":"绘本","child_total":4,"child_items":[{"id":"133","name":"绘本故事","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=133"},{"id":"139","name":"爱与情感","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=139"},{"id":"140","name":"生活成长","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=140"},{"id":"141","name":"获奖经典","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=141"}]},{"id":"3","name":"音乐","child_total":3,"child_items":[{"id":"35","name":"睡前音乐","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=35"},{"id":"36","name":"音乐启蒙","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=36"},{"id":"34","name":"古典音乐","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=34"}]},{"id":"9","name":"科普","child_total":8,"child_items":[{"id":"115","name":"百科知识","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=115"},{"id":"116","name":"生活常识","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=116"},{"id":"117","name":"自然知识","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=117"},{"id":"118","name":"科学知识","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=118"},{"id":"119","name":"安全知识","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=119"},{"id":"127","name":"儿童读物","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=127"},{"id":"128","name":"儿童学习","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=128"},{"id":"130","name":"儿童科普","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=130"}]},{"id":"153","name":"育儿","child_total":3,"child_items":[{"id":"131","name":"儿童教育","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=131"},{"id":"163","name":"育儿宝典","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=163"},{"id":"181","name":"怀孕安产","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=181"}]}]
         */

        private String total;
        private List<TagItemsBean> items;

        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
        }

        public List<TagItemsBean> getItems() {
            return items;
        }

        public void setItems(List<TagItemsBean> items) {
            this.items = items;
        }

        public static class TagItemsBean implements IRecyclerItem {
            /**
             * id : 2
             * name : 故事
             * child_total : 6
             * child_items : [{"id":"23","name":"睡前故事","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=23"},{"id":"158","name":"童话寓言","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=158"},{"id":"27","name":"女孩故事","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=27"},{"id":"25","name":"男孩故事","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=25"},{"id":"156","name":"神话传说","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=156"},{"id":"29","name":"动物故事","link":"xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=29"}]
             */

            private String id;
            private String name;
            private int child_total;
            private List<ChildItemsBean> child_items;


            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getChild_total() {
                return child_total;
            }

            public void setChild_total(int child_total) {
                this.child_total = child_total;
            }

            public List<ChildItemsBean> getChild_items() {
                return child_items;
            }

            public void setChild_items(List<ChildItemsBean> child_items) {
                this.child_items = child_items;
            }

            @Override
            public int getSpanSize() {
                return 4;
            }

            @Override
            public int getItemType() {
                return Category.TYPE_SECTION;
            }


            public static class ChildItemsBean implements IRecyclerItem {
                /**
                 * id : 23
                 * name : 睡前故事
                 * link : xnm://api.xiaoningmeng.net/default/v2.6/tag_album_list.php?tag_id=23
                 */

                private String id;
                private String name;
                private String linkurl;
                private String cover;

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getLinkurl() {
                    return linkurl;
                }

                public void setLinkurl(String linkurl) {
                    this.linkurl = linkurl;
                }

                public String getCover() {
                    return cover;
                }

                public void setCover(String cover) {
                    this.cover = cover;
                }

                @Override
                public int getSpanSize() {
                    return 2;
                }

                @Override
                public int getItemType() {
                    return Category.TYPE_TAG;
                }
            }
        }

        public TagBean() {
        }

    }

    public Category() {

    }
}
