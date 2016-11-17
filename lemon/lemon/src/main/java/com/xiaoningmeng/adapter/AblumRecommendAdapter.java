package com.xiaoningmeng.adapter;

import android.net.Uri;
import android.text.Html;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.xiaoningmeng.R;
import com.xiaoningmeng.bean.AlbumRecommend;

import java.util.List;

/**
 * 今日精选-更多-专辑
 */

public class AblumRecommendAdapter extends BaseQuickAdapter<AlbumRecommend.AlbumItemBean, BaseViewHolder> {


    public AblumRecommendAdapter(List<AlbumRecommend.AlbumItemBean> data) {
        super(R.layout.item_album_recommend, data);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, AlbumRecommend.AlbumItemBean item) {

        baseViewHolder.setText(R.id.tv_album_title, item.getTitle() != null ? Html.fromHtml(item.getTitle()) : "")
                .setVisible(R.id.tv_album_listen_num, !"0".equals(item.getListennum()) && null != item.getListennum())
                .setText(R.id.tv_album_listen_num, null != item.getListennum() ? item.getListennum() : "")
                .setVisible(R.id.tv_album_recommend, !"".equals(item.getRecommenddesc()) && null != item.getRecommenddesc())
                .setText(R.id.tv_album_recommend, null != item.getRecommenddesc() ? item.getRecommenddesc() : "")
                .setVisible(R.id.tv_tag_name, !"".equals(item.getTag().getName()) && null != item.getTag().getName())
                .setText(R.id.tv_tag_name, item.getTag().getName());

        SimpleDraweeView coverImg = baseViewHolder.getView(R.id.img_album_cover);
        Uri coverImgUri = Uri.parse(item.getCover());
        coverImg.setImageURI(coverImgUri);
    }
}