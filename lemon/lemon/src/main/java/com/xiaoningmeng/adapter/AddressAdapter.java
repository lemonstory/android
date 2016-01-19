package com.xiaoningmeng.adapter;

import java.util.List;

import com.xiaoningmeng.ModifyPerasonalActivity;
import com.xiaoningmeng.R;
import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.bean.Address;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AddressAdapter extends BaseAdapter{

	private LayoutInflater mInflater;
	private Context mContext;
	private List<Address> mAddresses;
/*	private String mSelectedId;*/

	public AddressAdapter(Context context,List<Address> addresses/*,String addressId*/) {
		mContext = context;
		mAddresses = addresses;
		//mSelectedId = addressId;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return mAddresses.size();
	}

	@Override
	public Address getItem(int position) {

		return mAddresses.get(position);
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
			convertView = mInflater.inflate(R.layout.item_address, null);
			holder.addressTv = (TextView) convertView.findViewById(R.id.tv_address_name);
			holder.selectedImg = (ImageView)convertView.findViewById(R.id.img_address_selected);
			holder.addressTitleTv = (TextView) convertView.findViewById(R.id.tv_address_title);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Address address = getItem(position);
		String area = (address.getArea()!=null?address.getArea():"");
		String addressStr = address.getProvince()+address.getCity() + area + address.getAddress();
		/*if(address.getId() != null && address.getId().equals(mSelectedId)){
			//holder.selectedImg.setVisibility(View.VISIBLE);
			holder.addressTitleTv.setText("默认地址:");
		}else{*/
			//holder.selectedImg.setVisibility(View.INVISIBLE);
			holder.addressTitleTv.setText(address.getName());
		/*}*/
		holder.selectedImg.setTag(position);
		holder.addressTv.setText(addressStr);
		return convertView;
	}

	static class ViewHolder {
		TextView addressTv;
		ImageView selectedImg;
		TextView addressTitleTv;
	}
	
/*	public void setSelectedAddressId(String addressId){
		this.mSelectedId = addressId;
	}*/

}