package com.xiaoningmeng.adapter;

import java.util.List;


import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiaoningmeng.PerasonalCenterActivity;
import com.xiaoningmeng.R;
import com.xiaoningmeng.base.BaseFragmentActivity;
import com.xiaoningmeng.bean.Comment;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.utils.AvatarUtils;
import com.xiaoningmeng.view.RatingBar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

public class AblumCommentAdapter extends BaseAdapter implements OnClickListener {

	private LayoutInflater mInflater;
	private Context mContext;
	private List<Comment> comments;

	public AblumCommentAdapter(Context context, List<Comment> comments) {

		mContext = context;
		this.comments = comments;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return comments.size() + 1;
	}

	@Override
	public Object getItem(int position) {

		return 0;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {

		return position == 0 ? 0 : 1;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("ViewHolder")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			if (getItemViewType(position) == 0) {
				convertView = mInflater.inflate(
						R.layout.item_ablum_detail_comment_head, null);
				holder.headDividerView = convertView.findViewById(R.id.v_comment_head_divider);
			} else {
				convertView = mInflater.inflate(
						R.layout.item_ablum_detail_comment, null);
				holder.dividerView = convertView
						.findViewById(R.id.v_comment_divider);
				holder.coverImg = (ImageView) convertView
						.findViewById(R.id.img_comment_cover);
				holder.commentTv = (TextView) convertView
						.findViewById(R.id.tv_comment_content);
				holder.ratingBar = (RatingBar) convertView
						.findViewById(R.id.rb_comment_rate);
				holder.dateTv = (TextView) convertView
						.findViewById(R.id.tv_comment_time);
				holder.nameTv = (TextView) convertView
						.findViewById(R.id.tv_comment_name);
			}
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (getItemViewType(position) == 0) {
			if (position == 0) {
				holder.headDividerView.setVisibility(View.INVISIBLE);
			} else {
				holder.headDividerView.setVisibility(View.VISIBLE);
			}
		} else {
			if(comments != null && comments.size() >0){
				Comment comment = comments.get(position - 1);
				holder.commentTv.setText(comment.getComment() == null ? "": Html.fromHtml(comment.getComment()));
				holder.ratingBar.setStar(comment.getStart_level());
				holder.dateTv.setText(AvatarUtils.getShortTimeStr2(comment.getAddtime()));
				holder.nameTv.setText(comment.getUname());
				holder.nameTv.setTag(position - 1);
				holder.coverImg.setTag(position - 1);
				holder.nameTv.setOnClickListener(this);
				holder.coverImg.setOnClickListener(this);
				String avatarUrl = AvatarUtils.getAvatarUrl(comment.getUid(), comment.getAvatartime(), -1);
				ImageLoader.getInstance().displayImage(avatarUrl, holder.coverImg,Constant.AVARAR_OPTIONS);
				RelativeLayout.LayoutParams lp = (LayoutParams) holder.dividerView.getLayoutParams();
				if (position == comments.size()) {
					lp.addRule(RelativeLayout.RIGHT_OF,0);
				} else {
					lp.addRule(RelativeLayout.RIGHT_OF,R.id.img_comment_cover);
				}
			}
		}

		return convertView;
	}

	static class ViewHolder {
		View headDividerView;
		View dividerView;
		ImageView coverImg;
		TextView commentTv;
		TextView nameTv;
		RatingBar ratingBar;
		TextView dateTv;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_comment_name:
		case R.id.img_comment_cover:
			int position = (int) v.getTag();
			String uid = comments.get(position).getUid();
			Intent i = new Intent(mContext,PerasonalCenterActivity.class);
			i.putExtra("uid", uid);
			((BaseFragmentActivity)mContext).startActivityForNew(i);
			break;
		}
	}
}