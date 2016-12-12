package com.xiaoningmeng.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.webkit.URLUtil;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.xiaoningmeng.R;
import com.xiaoningmeng.bean.AlbumInfo;

import java.util.List;

public class AuthorAlbumsAdapter extends BaseQuickAdapter<AlbumInfo, BaseViewHolder> {

    private LayoutInflater mInflater;
    private Context mContext;
    private List<AlbumInfo> albumInfoList;

    public AuthorAlbumsAdapter(List<AlbumInfo> data) {
        super(R.layout.item_author_album, data);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, AlbumInfo albumInfo) {

        baseViewHolder.setText(R.id.tv_album_title, albumInfo.getTitle() != null ? Html.fromHtml(albumInfo.getTitle()) : "")
                .setVisible(R.id.tv_album_listen_num, !"0".equals(albumInfo.getListennum()) && null != albumInfo.getListennum())
                .setText(R.id.tv_album_listen_num, null != albumInfo.getListennum() ? albumInfo.getListennum() : "")
                .setVisible(R.id.tv_album_recommend, !"".equals(albumInfo.getRecommenddesc()) && null != albumInfo.getRecommenddesc())
                .setText(R.id.tv_album_recommend, null != albumInfo.getRecommenddesc() ? albumInfo.getRecommenddesc().replaceAll("[ |　]", " ").trim() : "");

        SimpleDraweeView coverImg = baseViewHolder.getView(R.id.img_album_cover);
        String albumCover = albumInfo.getCover();
        if (URLUtil.isValidUrl(albumCover)) {
            Uri coverImgUri = Uri.parse(albumCover);
            coverImg.setImageURI(coverImgUri);
        }
    }
}