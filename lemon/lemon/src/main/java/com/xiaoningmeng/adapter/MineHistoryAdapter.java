package com.xiaoningmeng.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.xiaoningmeng.R;
import com.xiaoningmeng.bean.AlbumInfo;
import com.xiaoningmeng.bean.ListenerAlbum;
import com.xiaoningmeng.bean.Story;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.utils.TimeUtils;

import java.util.Calendar;
import java.util.List;

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
			holder.coverImg = (SimpleDraweeView) convertView.findViewById(R.id.img_mine_item_cover);
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

		//http://www.fresco-cn.org/docs/rounded-corners-and-circles.html
		//并非所有的图片分支部分都可以实现圆角，目前只有占位图片和实际图片可以实现圆角
		Uri coverImgUri = Uri.parse(listenerAlbum.getAlbuminfo().getCover());
		GenericDraweeHierarchy hierarchy = holder.coverImg.getHierarchy();
		hierarchy.setPlaceholderImage(Constant.getPosDrawable(position));
		holder.coverImg.setImageURI(coverImgUri);

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

		SimpleDraweeView coverImg;
		TextView historyTimeTv;
		View historyBottomDividerV;
		View historyTopDividerV;
		View historyDividerV;
		View historyRl;
		TextView storyTv;
		TextView titleTv;
		TextView playTimeTv;
	}
}