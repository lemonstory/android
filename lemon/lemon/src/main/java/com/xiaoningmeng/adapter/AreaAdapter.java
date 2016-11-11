package com.xiaoningmeng.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoningmeng.AccountModifyInfoActivity;
import com.xiaoningmeng.R;
import com.xiaoningmeng.bean.City;
import com.xiaoningmeng.bean.Province;
import com.xiaoningmeng.bean.Zone;

import java.util.List;

public class AreaAdapter<T> extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context mContext;
	private List<T> list;
	private int type;
	private int slelectPosition = -1;

	public AreaAdapter(Context context, List<T> list, int type) {

		mContext = context;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.list = list;
		this.type = type;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {

		return list.get(position);
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
			convertView = mInflater.inflate(R.layout.item_area, null);
			holder.areaNameTv = (TextView) convertView
					.findViewById(R.id.tv_area_name);
			holder.selectedTv = (TextView) convertView
					.findViewById(R.id.tv_area_selected);
			holder.rightImg = (ImageView) convertView
					.findViewById(R.id.img_area_right);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (type == AccountModifyInfoActivity.MODIFY_ADDRESS_PROVINCES) {
			Province province = (Province) getItem(position);
			holder.areaNameTv.setText(province.getProName());
		} else if (type == AccountModifyInfoActivity.MODIFY_ADDRESS_CITIES) {
			City city = (City) getItem(position);
			holder.areaNameTv.setText(city.getCityName());
		} else if (type == AccountModifyInfoActivity.MODIFY_ADDRESS_ZONES) {
			Zone area = (Zone) getItem(position);
			holder.rightImg.setVisibility(View.INVISIBLE);
			holder.areaNameTv.setText(area.getZoneName());
		}
		holder.selectedTv
				.setVisibility(position == slelectPosition ? View.VISIBLE
						: View.INVISIBLE);

		return convertView;
	}

	static class ViewHolder {
		TextView areaNameTv;
		TextView selectedTv;
		ImageView rightImg;
	}

	public int getSlelectPosition() {
		return slelectPosition;
	}

	public void setSlelectPosition(int slelectPosition) {
		this.slelectPosition = slelectPosition;
	}

}