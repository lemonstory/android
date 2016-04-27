package com.xiaoningmeng.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.text.Html;
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

public class SearchAdapter extends BaseAdapter{

	private LayoutInflater mInflater;
	private Context mContext;
	private List<AlbumInfo> albumInfoList;

	public SearchAdapter(Context context, List<AlbumInfo> albumInfos) {

		mContext = context;
		albumInfoList = albumInfos;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return albumInfoList.size();
	}

	@Override
	public AlbumInfo getItem(int position) {

		return albumInfoList.get(position);
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
			convertView = mInflater.inflate(R.layout.item_search_default, null);
			holder.coverImg = (SimpleDraweeView) convertView.findViewById(R.id.img_search_item_cover);
			holder.titleTv = (TextView) convertView.findViewById(R.id.tv_search_title);
			holder.dividerView = convertView.findViewById(R.id.v_album_divider);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		AlbumInfo albumInfo = getItem(position);
		Uri coverImgUri = Uri.parse(albumInfo.getCover());
		GenericDraweeHierarchy hierarchy = holder.coverImg.getHierarchy();
		hierarchy.setPlaceholderImage(Constant.getPosDrawable(position));
		holder.coverImg.setImageURI(coverImgUri);
		holder.titleTv.setText(Html.fromHtml(albumInfo.getTitle()));
		RelativeLayout.LayoutParams lp = (LayoutParams) holder.dividerView.getLayoutParams();
		lp.addRule(RelativeLayout.RIGHT_OF,position ==  getCount() - 1? 0 : R.id.img_search_item_cover);
		
		return convertView;
	}

	static class ViewHolder {
		SimpleDraweeView coverImg;
		TextView titleTv;
		View dividerView;
	}
}