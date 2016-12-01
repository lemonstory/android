package com.xiaoningmeng.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoningmeng.R;


public class ReminderAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context mContext;
	private String[] reminderTimeStr;
	private int reminderMode;

	public ReminderAdapter(Context context, int reminderMode) {

		mContext = context;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		reminderTimeStr = mContext.getResources().getStringArray(
				R.array.reminder);
		this.reminderMode = reminderMode;

	}

	@Override
	public int getCount() {
		return reminderTimeStr.length;
	}

	@Override
	public Object getItem(int position) {

		return reminderTimeStr[position];
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
			convertView = mInflater.inflate(R.layout.item_remider, null);
			holder.reminderTv = (TextView) convertView
					.findViewById(R.id.tv_reminder_time);
			holder.reminderImg = (ImageView) convertView
					.findViewById(R.id.img_reminder_time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.reminderTv.setText(reminderTimeStr[position]);
		if (position == reminderMode) {
			holder.reminderImg.setVisibility(View.VISIBLE);
			holder.reminderTv.setSelected(true);
		} else {
			holder.reminderImg.setVisibility(View.INVISIBLE);
			holder.reminderTv.setSelected(false);
		}
		return convertView;
	}

	public void setReminderMode(int reminderMode) {
		this.reminderMode = reminderMode;
	}

	static class ViewHolder {
		ImageView reminderImg;
		TextView reminderTv;
	}

}