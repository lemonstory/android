package com.xiaoningmeng.adapter;

import android.net.Uri;
import android.text.Html;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.xiaoningmeng.R;
import com.xiaoningmeng.bean.Tag;

import java.util.List;

/**
 * Created by gaoyong on 16/11/10.
 */

public class TagAdapter extends BaseQuickAdapter<Tag, BaseViewHolder> {

    public TagAdapter(List<Tag> tagList) {
        super(R.layout.item_circle_tag, tagList);
    }

    @Override
    protected void convert(BaseViewHolder helper, Tag item) {
        helper.setText(R.id.tv_title, item.getName() != null ? Html.fromHtml(item.getName()) : "");
        SimpleDraweeView coverImg = helper.getView(R.id.img_tag_cover);
        Uri coverImgUri = Uri.parse(item.getCover());
        coverImg.setImageURI(coverImgUri);
    }
}
