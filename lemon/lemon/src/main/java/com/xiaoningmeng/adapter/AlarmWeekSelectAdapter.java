package com.xiaoningmeng.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoningmeng.bean.WeekDay;
import com.xiaoningmeng.R;

import java.util.List;

public class AlarmWeekSelectAdapter extends BaseAdapter{

	private LayoutInflater mInflater;
	private Context mContext;
	private List<WeekDay> weekDays;

	public AlarmWeekSelectAdapter(Context context,List<WeekDay> weekDays) {

		mContext = context;
		this.weekDays = weekDays;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		int count = 0;
		if (weekDays != null) {
			count = weekDays.size();
		}
		return count;
	}

	@Override
	public WeekDay getItem(int position) {

		return weekDays.get(position);
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
			holder.coverImg = (ImageView) convertView.findViewById(R.id.img_select);
			holder.titleTv = (TextView) convertView.findViewById(R.id.tv_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		WeekDay weekDay = weekDays.get(position);
		holder.coverImg.setVisibility(weekDay.isCheck()?View.VISIBLE :ViewGroup.INVISIBLE);
		holder.titleTv.setText(weekDay.getDayStr());
		return convertView;
	}

	static class ViewHolder {
		ImageView coverImg;
		TextView titleTv;
	}

}