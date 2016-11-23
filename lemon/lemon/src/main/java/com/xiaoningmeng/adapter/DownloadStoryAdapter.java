package com.xiaoningmeng.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.xiaoningmeng.DownloadStoryActivity;
import com.xiaoningmeng.R;
import com.xiaoningmeng.bean.AudioDownLoad;
import com.xiaoningmeng.bean.Story;
import com.xiaoningmeng.download.DownLoadState;
import com.xiaoningmeng.player.PlayerManager;
import com.xiaoningmeng.utils.FileUtils;
import com.xiaoningmeng.utils.TimeUtils;

import java.util.List;

public class DownloadStoryAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context mContext;
	private int type;
	private List<AudioDownLoad> downLoads;
	private boolean isCheck;

	public DownloadStoryAdapter(Context context, int type,
			List<AudioDownLoad> downLoads, boolean isCheck) {

		mContext = context;
		this.type = type;
		this.downLoads = downLoads;
		this.isCheck = isCheck;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return downLoads.size();
	}

	@Override
	public AudioDownLoad getItem(int position) {

		return downLoads.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@SuppressLint("ViewHolder")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			if (type == DownloadStoryActivity.HISTORY_TYPE || type == DownloadStoryActivity.DEL_HISTORY_TYPE) {
				convertView = mInflater.inflate(R.layout.item_download_history,null);
				holder.dividerView = convertView.findViewById(R.id.v_download_divider);
			} else {
				convertView = mInflater.inflate(R.layout.item_download_downloading, null);
				holder.resumePb = (ProgressBar) convertView.findViewById(R.id.rpb_download_progress);
				holder.puasePb = (ProgressBar) convertView.findViewById(R.id.rpb_download_progress_pause);
			}
			holder.checkImg = (ImageView) convertView.findViewById(R.id.img_download_check);
			holder.storyTitle = (TextView) convertView.findViewById(R.id.tv_download_title);
			holder.timeTv = (TextView) convertView.findViewById(R.id.tv_download_time);
			holder.sizeTv = (TextView) convertView.findViewById(R.id.tv_download_size);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		AudioDownLoad downLoad = getItem(position);
		Story story = downLoad.getStory();
		holder.storyTitle.setText(story.getTitle());
		holder.timeTv.setText(TimeUtils.getShortTimeShot(Integer.parseInt(story.getTimes())));
		if (type == DownloadStoryActivity.HISTORY_TYPE
				|| type == DownloadStoryActivity.DEL_HISTORY_TYPE) {
			holder.sizeTv.setText(FileUtils.getFormatSize(downLoad.getEndPos()));
			if (isCheck) {
				holder.checkImg.setVisibility(View.VISIBLE);
				holder.checkImg.setSelected(downLoad.isCheck);
			} else {
				if (story.getMediapath().equals(PlayerManager.getInstance().getPlayingStory().mediapath)) {
					holder.checkImg.setVisibility(View.VISIBLE);
					holder.checkImg.setImageResource(R.drawable.ic_horn_on);
				} else {
					holder.checkImg.setVisibility(View.INVISIBLE);
				}
			}
			RelativeLayout.LayoutParams lp = (LayoutParams) holder.dividerView.getLayoutParams();
			lp.addRule(RelativeLayout.RIGHT_OF,position == getCount() -1 ? 0 : R.id.img_download_check);
		} else if (type == DownloadStoryActivity.DOWNLOAD_TYPE) {
			String tip = null;
			if(downLoad.getStatus() == DownLoadState.DOWN_PAUSE){
				holder.puasePb.setVisibility(View.VISIBLE);
				holder.resumePb.setVisibility(View.INVISIBLE);
				holder.puasePb.setProgress(downLoad.getProgress());
				tip =  "已暂停";
						
			}else{
				holder.puasePb.setVisibility(View.INVISIBLE);
				holder.resumePb.setVisibility(View.VISIBLE);
				holder.resumePb.setProgress(downLoad.getProgress());
				tip = "";
			}
			String fileSize = FileUtils.getFormatSize(downLoad.getEndPos());
			String downloadSize = FileUtils.getFormatSize(downLoad
					.getStartPos());
			holder.sizeTv.setText(tip + downloadSize + "/" + fileSize);
			holder.checkImg.setVisibility(isCheck ? View.VISIBLE
					: View.INVISIBLE);
			holder.checkImg.setSelected(downLoad.isCheck);
		}
		return convertView;
	}

	public boolean isCheck() {
		return isCheck;
	}

	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}

	static class ViewHolder {
		ImageView checkImg;
		TextView timeTv;
		TextView sizeTv;
		TextView storyTitle;
		ProgressBar resumePb;
		ProgressBar puasePb;
		View dividerView;
	}

}