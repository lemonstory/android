package com.xiaoningmeng.adapter;

import android.net.Uri;
import android.text.Html;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.xiaoningmeng.R;
import com.xiaoningmeng.bean.AlbumInfo;

import java.util.List;


/**
 * Created by huang on 2016/9/14.
 */

public class AlbumAdapter extends BaseQuickAdapter<AlbumInfo, BaseViewHolder> {

    public AlbumAdapter(List<AlbumInfo> data) {
        super(R.layout.item_album_info, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AlbumInfo item) {
        helper.setText(R.id.tv_album_title, item.getTitle() != null ? Html.fromHtml(item.getTitle()) : "")
                .setVisible(R.id.tv_album_listen_num, !"0".equals(item.getListennum()) && null != item.getListennum())
                .setText(R.id.tv_album_listen_num, null != item.getListennum() ? item.getListennum() : "")
                .setVisible(R.id.tv_album_recommend, !"".equals(item.getRecommenddesc()) && null != item.getRecommenddesc())
                .setText(R.id.tv_album_recommend,null != item.getRecommenddesc() ? item.getRecommenddesc() : "");

        SimpleDraweeView coverImg = helper.getView(R.id.img_album_cover);
        String albumCover = item.getCover();
        if(null != albumCover && !albumCover.equals("")) {
            Uri coverImgUri = Uri.parse(item.getCover());
            coverImg.setImageURI(coverImgUri);
        }
    }
}