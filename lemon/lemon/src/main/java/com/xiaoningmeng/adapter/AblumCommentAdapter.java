package com.xiaoningmeng.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.xiaoningmeng.R;
import com.xiaoningmeng.bean.Comment;
import com.xiaoningmeng.bean.CommentInfo;
import com.xiaoningmeng.utils.AvatarUtils;
import com.xiaoningmeng.view.RatingBar;

import java.util.List;

public class AblumCommentAdapter extends BaseQuickAdapter<Comment.ItemsBean, BaseViewHolder> {

	private LayoutInflater mInflater;
	private Context mContext;
	private List<CommentInfo> comments;

	public AblumCommentAdapter(List<Comment.ItemsBean> data) {
		super(R.layout.item_ablum_comment, data);
	}

	@Override
	protected void convert(BaseViewHolder baseViewHolder, Comment.ItemsBean comment) {

		baseViewHolder.setText(R.id.tv_comment_content, comment.getComment() == null ? "": Html.fromHtml(comment.getComment()))
				.setText(R.id.tv_comment_name, comment.getUname())
				.setText(R.id.tv_comment_time, AvatarUtils.getShortTimeStr2(comment.getAddtime()));

		//头像
		SimpleDraweeView avatarImg = baseViewHolder.getView(R.id.img_avatar);
		Uri avatarUri = Uri.parse(comment.getAvatar());
		avatarImg.setImageURI(avatarUri);

		//评级
		RatingBar ratingBar = baseViewHolder.getView(R.id.rb_comment_rate);
		ratingBar.setStar(Integer.parseInt(comment.getStart_level()));

		//添加点击事件
		baseViewHolder.addOnClickListener(R.id.tv_comment_name)
				.addOnClickListener(R.id.img_avatar);
	}
}