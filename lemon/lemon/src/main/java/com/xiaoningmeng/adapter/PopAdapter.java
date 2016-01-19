package com.xiaoningmeng.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiaoningmeng.R;
import com.xiaoningmeng.bean.AlbumInfo;
import com.xiaoningmeng.bean.Tag;
import com.xiaoningmeng.constant.Constant;

import java.util.List;

public class PopAdapter extends BaseAdapter{

	private LayoutInflater mInflater;
	private Context mContext;
	private List<Tag> firstTags;
	private String mSelectTag;

	public PopAdapter(Context context, List<Tag> firstTags,String selectTag) {

		mContext = context;
		this.firstTags = firstTags;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mSelectTag = selectTag;
	}

	@Override
	public int getCount() {
		return firstTags.size();
	}

	@Override
	public Tag getItem(int position) {

		return firstTags.get(position);
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
			convertView = mInflater.inflate(R.layout.pop_classification_item, null);
			holder.titleTv = (TextView) convertView.findViewById(R.id.tv_pop_item);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Tag tag = getItem(position);
		holder.titleTv.setText(tag.getName());
		holder.titleTv.setSelected(tag.getId().equals(mSelectTag));
		return convertView;
	}

	static class ViewHolder {
		TextView titleTv;
	}


	public void setSelectTag(String selectTag) {
		this.mSelectTag = selectTag;
	}
}