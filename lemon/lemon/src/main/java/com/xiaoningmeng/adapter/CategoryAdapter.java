package com.xiaoningmeng.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoningmeng.R;
import com.xiaoningmeng.bean.Category;
import com.xiaoningmeng.bean.IRecyclerItem;

import java.util.List;

/**
 * 全部分类-(年龄段, 一级标签, 二级标签)
 */

public class CategoryAdapter extends BaseMultiItemQuickAdapter<IRecyclerItem, BaseViewHolder> {

    public CategoryAdapter(Context context, List data) {
        super(data);
        addItemType(Category.TYPE_AGE_LEVEL, R.layout.item_category_age_level);
        addItemType(Category.TYPE_TAG, R.layout.item_category_tag);
        addItemType(Category.TYPE_SECTION, R.layout.item_category_section);
    }

    @Override
    protected void convert(BaseViewHolder helper, IRecyclerItem item) {

        switch (helper.getItemViewType()) {

            case Category.TYPE_AGE_LEVEL:
                Category.AgeLevelBean.AgeItemsBean ageItemsBean = (Category.AgeLevelBean.AgeItemsBean) item;
                helper.setText(R.id.tv_age_level_title,ageItemsBean.getTitle());
                break;

            case Category.TYPE_TAG:
                Category.TagBean.TagItemsBean.ChildItemsBean tagChildItemsBean = (Category.TagBean.TagItemsBean.ChildItemsBean) item;
                helper.setText(R.id.tv_tag_name,tagChildItemsBean.getName());
                break;

            case Category.TYPE_SECTION:
                Category.TagBean.TagItemsBean tagItemsBean = (Category.TagBean.TagItemsBean) item;
                helper.setText(R.id.tv_section_name,tagItemsBean.getName());
                break;
        }
    }
}
