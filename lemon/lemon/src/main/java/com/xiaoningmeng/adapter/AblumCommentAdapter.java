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

public class AblumCommentAdapter extends BaseQuickAdapter<Comment.DataBean.ItemsBean, BaseViewHolder> {

	private LayoutInflater mInflater;
	private Context mContext;
	private List<CommentInfo> comments;

	public AblumCommentAdapter(List<Comment.DataBean.ItemsBean> data) {
		super(R.layout.item_ablum_comment, data);
	}

	@Override
	protected void convert(BaseViewHolder baseViewHolder, Comment.DataBean.ItemsBean comment) {

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
//
//	@Override
//	public View getView(final int position, View convertView, ViewGroup parent) {
//		ViewHolder holder = null;
//		if (convertView == null) {
//			holder = new ViewHolder();
//			if (getItemViewType(position) == 0) {
//				convertView = mInflater.inflate(
//						R.layout.item_ablum_detail_comment_head, null);
//				holder.headDividerView = convertView.findViewById(R.id.v_comment_head_divider);
//			} else {
//				convertView = mInflater.inflate(
//						R.layout.item_ablum_comment, null);
//				holder.dividerView = convertView
//						.findViewById(R.id.v_comment_divider);
//				holder.coverImg = (SimpleDraweeView) convertView
//						.findViewById(R.id.img_comment_cover);
//				holder.commentTv = (TextView) convertView
//						.findViewById(R.id.tv_comment_content);
//				holder.ratingBar = (RatingBar) convertView
//						.findViewById(R.id.rb_comment_rate);
//				holder.dateTv = (TextView) convertView
//						.findViewById(R.id.tv_comment_time);
//				holder.nameTv = (TextView) convertView
//						.findViewById(R.id.tv_comment_name);
//			}
//			convertView.setTag(holder);
//		} else {
//			holder = (ViewHolder) convertView.getTag();
//		}
//		if (getItemViewType(position) == 0) {
//			if (position == 0) {
//				holder.headDividerView.setVisibility(View.INVISIBLE);
//			} else {
//				holder.headDividerView.setVisibility(View.VISIBLE);
//			}
//		} else {
//			if(comments != null && comments.size() >0){
//				CommentInfo comment = comments.get(position - 1);
//				holder.commentTv.setText(comment.getComment() == null ? "": Html.fromHtml(comment.getComment()));
//				holder.ratingBar.setStar(comment.getStart_level());
//				holder.dateTv.setText(AvatarUtils.getShortTimeStr2(comment.getAddtime()));
//				holder.nameTv.setText(comment.getUname());
//				holder.nameTv.setTag(position - 1);
//				holder.coverImg.setTag(position - 1);
//				holder.nameTv.setOnClickListener(this);
//				holder.coverImg.setOnClickListener(this);
//				String avatarUrl = AvatarUtils.getAvatarUrl(comment.getUid(), comment.getAvatartime(), -1);
//				Uri uri = Uri.parse(avatarUrl);
//				holder.coverImg.setImageURI(uri);
//				RelativeLayout.LayoutParams lp = (LayoutParams) holder.dividerView.getLayoutParams();
//				if (position == comments.size()) {
//					lp.addRule(RelativeLayout.RIGHT_OF,0);
//				} else {
//					lp.addRule(RelativeLayout.RIGHT_OF,R.id.img_comment_cover);
//				}
//			}
//		}
//
//		return convertView;
//	}
//
//	static class ViewHolder {
//		View headDividerView;
//		View dividerView;
//		SimpleDraweeView coverImg;
//		TextView commentTv;
//		TextView nameTv;
//		RatingBar ratingBar;
//		TextView dateTv;
//	}
//
//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.tv_comment_name:
//		case R.id.img_comment_cover:
//			int position = (int) v.getTag();
//			String uid = comments.get(position).getUid();
//			Intent i = new Intent(mContext,PerasonalCenterActivity.class);
//			i.putExtra("uid", uid);
//			((BaseActivity)mContext).startActivityForNew(i);
//			break;
//		}
//	}
}