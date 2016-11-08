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

public class AlbumAdatper extends BaseQuickAdapter<AlbumInfo, BaseViewHolder> {
    private int mImgHeight;

    public AlbumAdatper(List<AlbumInfo> data) {
        super(R.layout.item_recommend2, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AlbumInfo item) {
        helper.setText(R.id.tv_story_cover, item.getTitle() != null ? Html.fromHtml(item.getTitle()) : "")
                .setVisible(R.id.tv_story_tip, !"0".equals(item.getListennum()) && null != item.getListennum())
                .setText(R.id.tv_story_tip, null != item.getListennum() ? item.getListennum() : "");
        SimpleDraweeView coverImg = helper.getView(R.id.img_story_cover);

        Uri coverImg2Uri = Uri.parse(item.getCover());
        coverImg.setImageURI(coverImg2Uri);
    }
}