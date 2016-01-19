package com.xiaoningmeng.adapter;

import java.util.Calendar;
import java.util.List;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiaoningmeng.R;
import com.xiaoningmeng.bean.AlbumInfo;
import com.xiaoningmeng.bean.ListenerAlbum;
import com.xiaoningmeng.bean.Story;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.utils.TimeUtils;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MineHistoryAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<ListenerAlbum> mListenerAlbums;

	public MineHistoryAdapter(Context context,List<ListenerAlbum> listenerAlbums) {

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

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		GroupViewHolder holder = null;
		if (convertView == null) {
			holder = new GroupViewHolder();
			convertView = mInflater.inflate(R.layout.item_mine_history_header, null);
			holder.coverImg = (ImageView) convertView.findViewById(R.id.img_mine_item_cover);
			holder.historyTimeTv = (TextView) convertView.findViewById(R.id.tv_history_time);
			holder.historyTopDividerV = convertView.findViewById(R.id.v_history_top_divider);
			holder.historyBottomDividerV = convertView.findViewById(R.id.v_history_bottom_divider);
			holder.historyDividerV = convertView.findViewById(R.id.v_top_history_divider);
			holder.historyRl = convertView.findViewById(R.id.rl_history);
			holder.storyTv=(TextView) convertView.findViewById(R.id.tv_mine_story);
			holder.titleTv=(TextView) convertView.findViewById(R.id.tv_fav_title);
			holder.playTimeTv= (TextView) convertView.findViewById(R.id.tv_add_time);
			convertView.setTag(holder);
		} else {
			holder = (GroupViewHolder) convertView.getTag();
		}
		ListenerAlbum listenerAlbum = getItem(position);
		AlbumInfo albumInfo = listenerAlbum.getAlbuminfo();
		Calendar explainCalendar = Calendar.getInstance();
		explainCalendar.setTimeInMillis(Long.parseLong(listenerAlbum.getUptime())*1000);
		holder.historyTimeTv.setVisibility(View.GONE);
		holder.historyTopDividerV.setVisibility(View.GONE);
		holder.historyDividerV.setVisibility(position != 0 ?View.GONE:View.VISIBLE);
		holder.titleTv.setText(albumInfo.getTitle());
		Story story = albumInfo.getStoryinfo();
		if(story!= null) {
			holder.storyTv.setText(story.getTitle());
			if(listenerAlbum.getPlaytimes() >= Integer.parseInt(story.getTimes()) -3) {
				holder.playTimeTv.setText("播放完成");
			}else{
				holder.playTimeTv.setText("播放至"+TimeUtils.getShortTimeShot(listenerAlbum.getPlaytimes()));
			}
		}else{
			holder.storyTv.setText("");
			holder.playTimeTv.setText("暂无播放信息");
		}
		ImageLoader.getInstance().displayImage(listenerAlbum.getAlbuminfo().getCover(), holder.coverImg,Constant.getSmallAlbumOptions(position));
		if(position == getCount()-1){
			RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.historyBottomDividerV.getLayoutParams();
			lp.addRule(RelativeLayout.ALIGN_LEFT,0);
			holder.historyBottomDividerV.setVisibility(View.VISIBLE);
		}else{
			RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.historyBottomDividerV.getLayoutParams();
			lp.addRule(RelativeLayout.ALIGN_LEFT,R.id.tv_fav_title);
			holder.historyBottomDividerV.setVisibility(View.VISIBLE);
		}
		return convertView;
	}

	static class GroupViewHolder {

		ImageView coverImg;
		TextView historyTimeTv;
		View historyBottomDividerV;
		View historyTopDividerV;
		View historyDividerV;
		View historyRl;
		TextView storyTv;
		TextView titleTv;
		TextView playTimeTv;
	}

/*
	static class ChildViewHolder{
		TextView numberTv;
		TextView titleTv;
		TextView resumeTv;
		TextView timeTv;
		View dividerView;
		View storyView;
	}*/


/*	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		GroupViewHolder holder = null;
		if (convertView == null) {
			holder = new GroupViewHolder();
			convertView = mInflater.inflate(R.layout.item_mine_history_header, null);
			holder.coverImg = (ImageView) convertView.findViewById(R.id.img_mine_item_cover);
			holder.historyTimeTv = (TextView) convertView.findViewById(R.id.tv_history_time);
			holder.historyBottomDividerV = convertView.findViewById(R.id.v_history_divider);
			holder.historyTopDividerV = convertView.findViewById(R.id.v_top_history_divider);
			holder.historyRl = convertView.findViewById(R.id.rl_history);
			holder.listenerTv=(TextView) convertView.findViewById(R.id.tv_listener);
			holder.favTv=(TextView) convertView.findViewById(R.id.tv_fav);
			holder.commentTv=(TextView) convertView.findViewById(R.id.tv_comment);
			holder.titleTv=(TextView) convertView.findViewById(R.id.tv_fav_title);
			holder.addTimeTv= (TextView) convertView.findViewById(R.id.tv_add_time);
			convertView.setTag(holder);
		} else {
			holder = (GroupViewHolder) convertView.getTag();
		}
		ListenerAlbum listenerAlbum = getGroup(groupPosition);
		AlbumInfo albumInfo = listenerAlbum.getAlbuminfo();
		Calendar explainCalendar = Calendar.getInstance();
		explainCalendar.setTimeInMillis(Long.parseLong(listenerAlbum.getUptime())*1000);
		*//*if (getItemViewType(groupPosition) == NEXT_TYPE) {
			holder.historyTimeTv.setVisibility(View.VISIBLE);
			holder.historyTimeTv.setText(TimeUtils.completeDay(explainCalendar.get(Calendar.MONTH), explainCalendar.get(Calendar.DAY_OF_MONTH)));
			holder.historyBottomDividerV.setVisibility(View.VISIBLE);
			holder.historyTopDividerV.setVisibility(groupPosition == 0 ?View.GONE:View.VISIBLE);
		} else {*//*
			holder.historyTimeTv.setVisibility(View.GONE);
			holder.historyBottomDividerV.setVisibility(View.GONE);
			//holder.historyTopDividerV.setVisibility(View.VISIBLE);
		*//*}*//*
			holder.historyTopDividerV.setVisibility(groupPosition != 0 ?View.GONE:View.VISIBLE);
		holder.favTv.setText(albumInfo.getFavnum()==0?"收藏":(albumInfo.getFavnum()+""));
		holder.favTv.setSelected(albumInfo.getFav() == 1);
		holder.titleTv.setText(albumInfo.getTitle());
		holder.commentTv.setText(albumInfo.getCommentnum() ==0?"评论":(albumInfo.getCommentnum()+""));
		holder.listenerTv.setText(albumInfo.getListennum()+"");
		holder.addTimeTv.setText(TimeUtils.formatDateToShot(Long.parseLong(listenerAlbum.getUptime())*1000));
		ImageLoader.getInstance().displayImage(listenerAlbum.getAlbuminfo().getCover(), holder.coverImg,Constant.getSmallAlbumOptions(groupPosition));
		return convertView;
	}*/

	/*@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		ChildViewHolder holder;
		if (convertView == null) {
			holder = new ChildViewHolder();
			convertView = mInflater.inflate(R.layout.item_mine_history_small, null);
			holder.numberTv = (TextView) convertView.findViewById(R.id.tv_history_number);
			holder.titleTv = (TextView)convertView.findViewById(R.id.tv_history_title);
			holder.resumeTv = (TextView)convertView.findViewById(R.id.tv_history_resume);
			holder.timeTv = (TextView)convertView.findViewById(R.id.tv_history_time);
			holder.dividerView = convertView.findViewById(R.id.v_history_divider);
			holder.storyView = convertView.findViewById(R.id.rl_history_story);
			convertView.setTag(holder);
		} else {
			holder = (ChildViewHolder) convertView.getTag();
		}
		ListenerAlbum listenerAlbum = getGroup(groupPosition);
		Story story = getChild(groupPosition, childPosition);
		holder.numberTv.setText(childPosition+1+"");
		holder.titleTv.setText(story.getTitle());
		if(story.getStoryId().equals(listenerAlbum.getPlaystoryid())){
			holder.resumeTv.setVisibility(View.VISIBLE);
			holder.timeTv.setVisibility(View.INVISIBLE);
			holder.storyView.setSelected(true);
			holder.dividerView.setVisibility(View.INVISIBLE);
		}else{
			holder.timeTv.setVisibility(View.VISIBLE);
			holder.timeTv.setText(TimeUtils.getShortTimeShot(Integer.parseInt(story.getTimes())));
			holder.resumeTv.setVisibility(View.INVISIBLE);
			holder.storyView.setSelected(false);
			if(isLastChild){
				if(groupPosition == getGroupCount()-1){
					RelativeLayout.LayoutParams lp = (LayoutParams) holder.dividerView.getLayoutParams();
					lp.addRule(RelativeLayout.ALIGN_LEFT,0);
					holder.dividerView.setVisibility(View.VISIBLE);
				}else{
					holder.dividerView.setVisibility(View.INVISIBLE);
				}
			}else{
				RelativeLayout.LayoutParams lp = (LayoutParams) holder.dividerView.getLayoutParams();
				lp.addRule(RelativeLayout.ALIGN_LEFT,R.id.tv_history_title);
				holder.dividerView.setVisibility(View.VISIBLE);
			}
		}

		return convertView;
	}*/


	
}