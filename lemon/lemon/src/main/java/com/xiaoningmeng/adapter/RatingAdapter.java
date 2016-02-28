package com.xiaoningmeng.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.xiaoningmeng.R;
import com.xiaoningmeng.bean.UserInfo;
import com.xiaoningmeng.utils.AvatarUtils;

import java.util.List;

public class RatingAdapter extends BaseAdapter implements OnClickListener {

	private LayoutInflater mInflater;
	private Context mContext;
	private List<UserInfo> userInfos;

	public RatingAdapter(Context context,List<UserInfo> userInfos) {

		mContext = context;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.userInfos = userInfos;
	}

	@Override
	public int getCount() {
		return userInfos.size();
	}

	@Override
	public UserInfo getItem(int position) {

		return userInfos.get(position);
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
			convertView = mInflater.inflate(R.layout.item_rating, null);
			holder.coverImg = (SimpleDraweeView) convertView
					.findViewById(R.id.img_rating_cover);
			holder.ratingBg = (ImageView) convertView
					.findViewById(R.id.img_rating_bg);
			holder.numberBitImg = (ImageView) convertView
					.findViewById(R.id.img_rating_number_bit);
			holder.numberTenImg = (ImageView) convertView
					.findViewById(R.id.img_rating_number_ten);
			holder.numberHundredImg = (ImageView) convertView
					.findViewById(R.id.img_rating_number_hundred);
			holder.nameTv = (TextView)convertView.findViewById(R.id.tv_rating_name);
			holder.areaTv = (TextView)convertView.findViewById(R.id.tv_rating_area);
			holder.listenTv = (TextView)convertView.findViewById(R.id.tv_rating_listener);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		UserInfo userInfo = getItem(position);
		holder.ratingBg.setVisibility(position <= 2 ? View.VISIBLE: View.INVISIBLE);
		String avatarUrl = AvatarUtils.getAvatarUrl(userInfo.getUid(), userInfo.getAvatartime(), -1);
		Uri avatarUri = Uri.parse(avatarUrl);
		holder.coverImg.setImageURI(avatarUri);
		holder.nameTv.setText(userInfo.getNickname()!= null ?userInfo.getNickname() :"");
		holder.areaTv.setText(userInfo.getProvince()!= null ?userInfo.getProvince():""+userInfo.getCity()!= null ?userInfo.getCity():"");
		holder.listenTv.setText(userInfo.getListennum()!= null ?userInfo.getListennum():"0");
		int pos = position +1;
		if(pos <=3){
			int bitId = mContext.getResources().getIdentifier("rating_w_" + pos, "drawable",mContext.getPackageName());
			holder.numberBitImg.setImageResource(bitId);
			holder.numberBitImg.setVisibility(View.VISIBLE);
			holder.numberTenImg.setVisibility(View.GONE);
			holder.numberHundredImg.setVisibility(View.GONE);
		}else if(pos < 10){
			int bitId = mContext.getResources().getIdentifier("rating_" + pos, "drawable",mContext.getPackageName());
			holder.numberBitImg.setImageResource(bitId);
			holder.numberBitImg.setVisibility(View.VISIBLE);
			holder.numberTenImg.setVisibility(View.GONE);
			holder.numberHundredImg.setVisibility(View.GONE);
			
		}else if(pos <100){
			int bit = pos % 10;
			int ten = pos % 100 / 10;
			int bitId = mContext.getResources().getIdentifier("rating_" + bit, "drawable",mContext.getPackageName());
			int tenId = mContext.getResources().getIdentifier("rating_" + ten, "drawable",mContext.getPackageName());
			holder.numberBitImg.setImageResource(bitId);
			holder.numberTenImg.setImageResource(tenId);
			holder.numberBitImg.setVisibility(View.VISIBLE);
			holder.numberTenImg.setVisibility(View.VISIBLE);
			holder.numberHundredImg.setVisibility(View.GONE);
		}else{
			int bit = pos % 10;
			int ten = pos % 100 / 10;
			int hunsband = pos /100;
			int bitId = mContext.getResources().getIdentifier("rating_" + bit, "drawable",mContext.getPackageName());
			int tenId = mContext.getResources().getIdentifier("rating_" + ten, "drawable",mContext.getPackageName());
			int hunsbandId = mContext.getResources().getIdentifier("rating_" + hunsband, "drawable",mContext.getPackageName());
			holder.numberBitImg.setImageResource(bitId);
			holder.numberTenImg.setImageResource(tenId);
			holder.numberHundredImg.setImageResource(hunsbandId);
			holder.numberBitImg.setVisibility(View.VISIBLE);
			holder.numberTenImg.setVisibility(View.VISIBLE);
			holder.numberHundredImg.setVisibility(View.VISIBLE);
		}
		return convertView;
	}

	static class ViewHolder {
		SimpleDraweeView coverImg;
		ImageView ratingBg;
		ImageView numberBitImg;
		ImageView numberTenImg;
		ImageView numberHundredImg;
		TextView nameTv;
		TextView areaTv;
		TextView listenTv;
	}

	@Override
	public void onClick(View v) {

	}
}