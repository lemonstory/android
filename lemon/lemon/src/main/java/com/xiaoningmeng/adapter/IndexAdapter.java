package com.xiaoningmeng.adapter;

import android.net.Uri;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.xiaoningmeng.bean.AlbumInfo;
import com.xiaoningmeng.bean.IRecyclerItem;
import com.xiaoningmeng.bean.Index;
import com.xiaoningmeng.R;

import java.util.List;


/**
 * Created by huang on 2016/10/15.
 */

public class IndexAdapter extends BaseMultiItemQuickAdapter<IRecyclerItem, BaseViewHolder> {


    public IndexAdapter(List<IRecyclerItem> datas) {

        super(datas);
        addItemType(Index.CATEGORY_TYPE, R.layout.item_index_category);
        addItemType(Index.ALBUM_MORE_TYPE, R.layout.item_index_more);
        addItemType(Index.ALBUM_TYPE, R.layout.item_album_info);
        addItemType(Index.AD_TYPE, R.layout.item_index_ad);
    }


    @Override
    protected void convert(BaseViewHolder helper, IRecyclerItem item) {

        switch (item.getItemType()) {

            case Index.CATEGORY_TYPE:
                Index.ContentCategoryBean.ItemBean categoryItemBean = (Index.ContentCategoryBean.ItemBean) item;
                helper.setText(R.id.tv_tag_name, categoryItemBean.getTitle());
                SimpleDraweeView coverImg = helper.getView(R.id.img_tag_cover);
                coverImg.setImageURI(Uri.parse(categoryItemBean.getCover()));
                break;

            case Index.ALBUM_MORE_TYPE:
                Index.AlbumSectionBean.ItemBean albumItemBean = (Index.AlbumSectionBean.ItemBean) item;
                helper.setText(R.id.tv_tag_title, albumItemBean.getTitle())
                        .addOnClickListener(R.id.tv_tag_more);
                break;

            case Index.ALBUM_TYPE:
                AlbumInfo albumInfo = (AlbumInfo) item;
                helper.setText(R.id.tv_album_title, albumInfo.getTitle())
                        .setText(R.id.tv_album_recommend, albumInfo.getRecommenddesc())
                        .setText(R.id.tv_album_listen_num, albumInfo.getListennum() + "");
                SimpleDraweeView albumImg = helper.getView(R.id.img_album_cover);
                albumImg.setImageURI(Uri.parse(albumInfo.getCover()));
                break;

            case Index.AD_TYPE:
                Index.AdBean.ItemsBean itemsBean = (Index.AdBean.ItemsBean) item;
                SimpleDraweeView adImg = helper.getView(R.id.img_index_ad);
                adImg.setImageURI(Uri.parse(itemsBean.getCover()));
                helper.addOnClickListener(R.id.img_index_ad);
                break;
        }
    }
}
