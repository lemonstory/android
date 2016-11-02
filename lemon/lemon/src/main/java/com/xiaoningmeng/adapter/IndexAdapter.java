package com.xiaoningmeng.adapter;

import android.net.Uri;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.xiaoningmeng.R;
import com.xiaoningmeng.bean.AlbumInfo;
import com.xiaoningmeng.bean.IRecyclerItem;
import com.xiaoningmeng.bean.Index;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huang on 2016/10/15.
 */

public class IndexAdapter extends BaseMultiItemQuickAdapter<IRecyclerItem> {


    public IndexAdapter(List<IRecyclerItem> datas) {
        super(datas);
        addItemType(Index.AGE_TYPE, R.layout.item_index_age);
        addItemType(Index.CATEGORY_TYPE, R.layout.item_index_category);
        addItemType(Index.ALBUM_MORE_TYPE,R.layout.item_home_discover_top);
        addItemType(Index.ALBUM_TYPE,R.layout.item_recommend2);
        addItemType(Index.AD_TYPE,R.layout.item_index_ad);
    }




    @Override
    protected void convert(BaseViewHolder helper, IRecyclerItem item) {
        switch (item.getItemType()){
            case Index.AGE_TYPE:
                Index.AgeLevelBean.ItemsBean ageBean = (Index.AgeLevelBean.ItemsBean) item;
                helper.setText(R.id.tv_age_classify,ageBean.getTitle())
                .addOnClickListener(R.id.ll_age_classify);
                SimpleDraweeView coverImg = helper.getView(R.id.img_age_classify);
                coverImg.setImageURI(Uri.parse(ageBean.getCover()));
                break;
            case Index.CATEGORY_TYPE:
                Index.ContentCategoryBean categoryBean = (Index.ContentCategoryBean) item;
                Index.ContentCategoryBean.ItemsBean categoryItemsBean1 = categoryBean.getItems().get(0);
                Index.ContentCategoryBean.ItemsBean categoryItemsBean2 = categoryBean.getItems().get(1);
                Index.ContentCategoryBean.ItemsBean categoryItemsBean3 = categoryBean.getItems().get(2);
                helper.setText(R.id.tv_classify1,categoryItemsBean1.getTitle())
                        .addOnClickListener(R.id.ll_classify1)
                        .setText(R.id.tv_classify2,categoryItemsBean2.getTitle())
                        .addOnClickListener(R.id.ll_classify2)
                        .setText(R.id.tv_classify3,categoryItemsBean3.getTitle())
                        .addOnClickListener(R.id.ll_classify3);
                SimpleDraweeView coverImg1 = helper.getView(R.id.img_classify1);
                coverImg1.setImageURI(Uri.parse(categoryItemsBean1.getCover()));
                SimpleDraweeView coverImg2 = helper.getView(R.id.img_classify2);
                coverImg2.setImageURI(Uri.parse(categoryItemsBean2.getCover()));
                SimpleDraweeView coverImg3 = helper.getView(R.id.img_classify3);
                coverImg3.setImageURI(Uri.parse(categoryItemsBean3.getCover()));
                break;
            case Index.ALBUM_MORE_TYPE:
                Index.AlbumSectionBean.ItemBean albumItemBean = (Index.AlbumSectionBean.ItemBean) item;
                helper.setText(R.id.tv_classify_title,albumItemBean.getTitle())
                        .addOnClickListener(R.id.tv_classify_title)
                        .addOnClickListener(R.id.tv_classify_more);
                break;
            case Index.ALBUM_TYPE:
                AlbumInfo albumInfo = (AlbumInfo) item;
                helper.setText(R.id.tv_story_cover,albumInfo.getTitle())
                        .setText(R.id.tv_story_recommend,albumInfo.getRecommenddesc())
                        .setText(R.id.tv_story_tip,albumInfo.getListennum()+"");
                SimpleDraweeView albumImg = helper.getView(R.id.img_story_cover);
                albumImg.setImageURI(Uri.parse(albumInfo.getCover()));
                break;
            case Index.AD_TYPE:
                Index.AdBean.ItemsBean itemsBean = (Index.AdBean.ItemsBean) item;
                SimpleDraweeView adImg = helper.getView(R.id. img_index_ad);
                adImg.setImageURI(Uri.parse(itemsBean.getCover()));
                helper.addOnClickListener(R.id.img_index_ad);
                break;
        }
    }
}
