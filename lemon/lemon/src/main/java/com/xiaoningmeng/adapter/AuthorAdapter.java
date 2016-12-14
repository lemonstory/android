package com.xiaoningmeng.adapter;

import android.net.Uri;
import android.text.TextUtils;
import android.webkit.URLUtil;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.xiaoningmeng.R;
import com.xiaoningmeng.bean.Author;

import java.util.List;

/**
 * Created by gaoyong on 16/12/12.
 */

public class AuthorAdapter extends BaseQuickAdapter<Author, BaseViewHolder> {


    public AuthorAdapter(int layoutResId, List<Author> data) {
        super(layoutResId, data);
    }

    public AuthorAdapter(List<Author> data) {
        super(data);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, Author author) {

        baseViewHolder.setText(R.id.tv_author_name, author.getNickname());
        if ( author.getAlbum_num() > 0) {
            baseViewHolder.setText(R.id.tv_album_num, Integer.toString(author.getAlbum_num()));
        }

        if(!TextUtils.isEmpty(author.getCard())) {
            baseViewHolder.setText(R.id.tv_author_card,author.getCard());
        }

        //头像
        SimpleDraweeView avatarImg = baseViewHolder.getView(R.id.img_avatar);
        if(URLUtil.isValidUrl(author.getAvatar())) {
            Uri avatarUri = Uri.parse(author.getAvatar());
            avatarImg.setImageURI(avatarUri);
        }
    }
}
