package com.xiaoningmeng.adapter;


import com.xiaoningmeng.R;


import android.annotation.SuppressLint;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AlarmSoundSelectAdapter extends BaseAdapter{

	private LayoutInflater mInflater;
	private Context mContext;
	private String[] sounds;
	private int selectedPositon;

	public AlarmSoundSelectAdapter(Context context,int selectPos) {

		mContext = context;
		sounds = context.getResources().getStringArray(R.array.music_cover_name);
		this.selectedPositon = selectPos;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return sounds.length;
	}

	@Override
	public String getItem(int position) {

		return sounds[position];
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
			convertView = mInflater.inflate(R.layout.item_alarm_repat, null);
			holder.coverImg = (ImageView) convertView
					.findViewById(R.id.img_select);
			holder.titleTv = (TextView) convertView
					.findViewById(R.id.tv_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.coverImg.setVisibility(selectedPositon == position ? View.VISIBLE :ViewGroup.INVISIBLE);
		holder.titleTv.setText(sounds[position]);
		return convertView;
	}

	static class ViewHolder {
		ImageView coverImg;
		TextView titleTv;
	}


	public int getSelectedPositon() {
		return selectedPositon;
	}

	public void setSelectedPositon(int selectedPositon) {
		this.selectedPositon = selectedPositon;
	}
	
	
}