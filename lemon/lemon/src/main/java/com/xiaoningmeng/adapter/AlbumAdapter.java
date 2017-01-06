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

        String albumDesc = "";
        if (null != item.getRecommenddesc() && !"".equals(item.getRecommenddesc())) {
            albumDesc = item.getRecommenddesc();

        } else if (null != item.getIntro() && !"".equals(item.getIntro())) {
            albumDesc = item.getIntro();
        }

        helper.setText(R.id.tv_album_title, item.getTitle() != null ? Html.fromHtml(item.getTitle()) : "")
                .setVisible(R.id.tv_album_listen_num, !"0".equals(item.getListennum()) && null != item.getListennum())
                .setText(R.id.tv_album_listen_num, null != item.getListennum() ? item.getListennum() : "")
                .setVisible(R.id.tv_album_recommend, !"".equals(albumDesc) && null != albumDesc)
                .setText(R.id.tv_album_recommend, !"".equals(albumDesc) && null != albumDesc ? albumDesc : "");

        SimpleDraweeView coverImg = helper.getView(R.id.img_album_cover);
        String albumCover = item.getCover();
        if(null != albumCover && !albumCover.equals("")) {
            Uri coverImgUri = Uri.parse(item.getCover());
            coverImg.setImageURI(coverImgUri);
        }
    }
}