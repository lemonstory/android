package com.xiaoningmeng.bean;

import java.util.List;

/**
 * Created by gaoyong on 16/11/25.
 */

public class StoryList {

    private String total;
    private List<Story> items;

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<Story> getItems() {
        return items;
    }

    public void setItems(List<Story> items) {
        this.items = items;
    }

}
