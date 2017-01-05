package com.xiaoningmeng.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.xiaoningmeng.R;
import com.xiaoningmeng.bean.AlbumInfo;
import com.xiaoningmeng.constant.Constant;

import java.util.List;

public class MoreAdapter extends BaseAdapter{

	private LayoutInflater mInflater;
	private Context mContext;
	private List<AlbumInfo> mAlbumList;
	private boolean isFav;

	public MoreAdapter(Context context, List<AlbumInfo> albumList) {

		mContext = context;
		this.mAlbumList = albumList;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public MoreAdapter(Context context, List<AlbumInfo> albumList,boolean isFav) {

		mContext = context;
		this.mAlbumList = albumList;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.isFav = isFav;
	}

	@Override
	public int getCount() {
		return mAlbumList.size();
	}

	@Override
	public AlbumInfo getItem(int position) {

		return mAlbumList.get(position);
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
			convertView = mInflater.inflate(R.layout.item_fav, null);
			holder.coverImg = (SimpleDraweeView) convertView.findViewById(R.id.img_mine_item_cover);
			holder.titleTv = (TextView) convertView.findViewById(R.id.tv_fav_title);
			holder.listenerTv = (TextView) convertView.findViewById(R.id.tv_listener);
			holder.albumDividerV = convertView.findViewById(R.id.v_album_divider);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		AlbumInfo album = getItem(position);
		Uri coverImgUri = Uri.parse(album.getCover());
		GenericDraweeHierarchy hierarchy = holder.coverImg.getHierarchy();
		hierarchy.setPlaceholderImage(Constant.getPosDrawable(position));
		holder.coverImg.setImageURI(coverImgUri);
		holder.titleTv.setText(album.getTitle());
		//holder.dateTv.setText(AvatarUtils.getShortTimeStr2(album.getAdd_time()));
		holder.listenerTv.setText(album.getListennum()+"");
		RelativeLayout.LayoutParams lp = (LayoutParams) holder.albumDividerV.getLayoutParams();
		lp.addRule(RelativeLayout.ALIGN_LEFT,position == getCount() -1 ? 0 : R.id.ll_history_bottom);
		return convertView;
	}

	static class ViewHolder {
		SimpleDraweeView coverImg;
		TextView titleTv;
		TextView listenerTv;
		View albumDividerV;
	}

}