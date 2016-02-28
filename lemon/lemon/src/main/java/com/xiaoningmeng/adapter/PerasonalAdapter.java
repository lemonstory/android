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
import com.xiaoningmeng.bean.ListenerAlbum;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.utils.TimeUtils;

import java.util.Calendar;
import java.util.List;

public class PerasonalAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context mContext;
	private List<ListenerAlbum> mListenerAlbums;
	private static int NEXT_TYPE= 1;
	private static int SIMPLE_TYPE= 2;

	public PerasonalAdapter(Context context,List<ListenerAlbum> listenerAlbums) {

		mContext = context;
		mListenerAlbums = listenerAlbums;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return mListenerAlbums.size();
	}

	@Override
	public ListenerAlbum getItem(int position) {

		return mListenerAlbums.get(position);
	}


	/*@Override
	public int getItemViewType(int position) {
		if(position == 0){
			return NEXT_TYPE;
		}else if(TimeUtils.isSameOneDay(Long.parseLong(mListenerAlbums.get(position -1).getUptime()),Long.parseLong(mListenerAlbums.get(position).getUptime()))){
			return SIMPLE_TYPE;
		}
		return NEXT_TYPE;
	}*/
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
			convertView = mInflater.inflate(R.layout.item_perasonal, null);
			holder.coverImg = (SimpleDraweeView) convertView.findViewById(R.id.img_mine_item_cover);
			holder.historyTimeTv = (TextView) convertView.findViewById(R.id.tv_history_time);
			holder.historyBottomDividerV = convertView.findViewById(R.id.v_history_divider);
			holder.historyTopDividerV = convertView.findViewById(R.id.v_top_history_divider);
			holder.historyRl = convertView.findViewById(R.id.rl_history);
			holder.listenerTv=(TextView) convertView.findViewById(R.id.tv_listener);
			holder.favTv=(TextView) convertView.findViewById(R.id.tv_fav);
			holder.commentTv=(TextView) convertView.findViewById(R.id.tv_comment);
			holder.titleTv=(TextView) convertView.findViewById(R.id.tv_fav_title);
			holder.addTimeTv= (TextView) convertView.findViewById(R.id.tv_add_time);
			holder.albumDividerV = convertView.findViewById(R.id.v_album_divider);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		ListenerAlbum listenerAlbum = getItem(position);
		AlbumInfo albumInfo = listenerAlbum.getAlbuminfo();
		Calendar explainCalendar = Calendar.getInstance();
		explainCalendar.setTimeInMillis(Long.parseLong(listenerAlbum.getUptime()) * 1000);
		/*if (getItemViewType(position) == NEXT_TYPE) {
			holder.historyTimeTv.setVisibility(View.VISIBLE);
			holder.historyTimeTv.setText(TimeUtils.completeDay(explainCalendar.get(Calendar.MONTH), explainCalendar.get(Calendar.DAY_OF_MONTH)));
			holder.historyBottomDividerV.setVisibility(View.VISIBLE);
			holder.historyTopDividerV.setVisibility(position == 0 ?View.GONE:View.VISIBLE);
		} else {
			holder.historyTimeTv.setVisibility(View.GONE);
			holder.historyBottomDividerV.setVisibility(View.GONE);
			holder.historyTopDividerV.setVisibility(View.GONE);
		}*/
		holder.historyTimeTv.setVisibility(View.GONE);
		holder.historyBottomDividerV.setVisibility(View.GONE);
		holder.historyTopDividerV.setVisibility(position != 0 ? View.GONE : View.VISIBLE);
		if(position + 1 < getCount()){
			if(getItemViewType(position + 1) == NEXT_TYPE){
				holder.albumDividerV.setVisibility(View.GONE);
			}else{
				RelativeLayout.LayoutParams lp = (LayoutParams) holder.albumDividerV.getLayoutParams();
				lp.addRule(RelativeLayout.ALIGN_LEFT,R.id.ll_history_bottom);
				holder.albumDividerV.setVisibility(View.VISIBLE);
			}
		}else{
			RelativeLayout.LayoutParams lp = (LayoutParams) holder.albumDividerV.getLayoutParams();
			lp.addRule(RelativeLayout.ALIGN_LEFT,0);
			holder.albumDividerV.setVisibility(View.VISIBLE);
		}
		holder.favTv.setText(albumInfo.getFavnum() == 0 ? "收藏" : (albumInfo.getFavnum() + ""));
		holder.favTv.setSelected(albumInfo.getFav() == 1);
		holder.titleTv.setText(albumInfo.getTitle());
		holder.commentTv.setText(albumInfo.getCommentnum() ==0?"评论":(albumInfo.getCommentnum()+""));
		holder.addTimeTv.setText(TimeUtils.getShowTimeString1(Long.parseLong(listenerAlbum.getUptime()) * 1000));
		holder.listenerTv.setText(albumInfo.getListennum() + "");
		Uri coverImgUri = Uri.parse(listenerAlbum.getAlbuminfo().getCover());
		GenericDraweeHierarchy hierarchy = holder.coverImg.getHierarchy();
		hierarchy.setPlaceholderImage(Constant.getPosDrawable(position));
		holder.coverImg.setImageURI(coverImgUri);
		return convertView;
	}

	static class ViewHolder {

		SimpleDraweeView coverImg;
		TextView historyTimeTv;
		View historyBottomDividerV;
		View historyTopDividerV;
		View historyRl;
		TextView listenerTv;
		TextView favTv;
		TextView commentTv;
		TextView titleTv;
		TextView addTimeTv;
		View albumDividerV;
		
	}


}