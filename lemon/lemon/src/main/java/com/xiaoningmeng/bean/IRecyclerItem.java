package com.xiaoningmeng.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * 在RecyclerView中多行item必须实现该接口
 */

public interface IRecyclerItem extends MultiItemEntity {

    public int getSpanSize();
}
