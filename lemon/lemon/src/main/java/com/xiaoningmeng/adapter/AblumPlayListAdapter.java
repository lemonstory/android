package com.xiaoningmeng.adapter;

import java.util.List;

import com.xiaoningmeng.R;
import com.xiaoningmeng.bean.AlbumInfo;
import com.xiaoningmeng.bean.AudioDownLoad;
import com.xiaoningmeng.bean.PlayingStory;
import com.xiaoningmeng.bean.Story;
import com.xiaoningmeng.download.DownLoadClientImpl;
import com.xiaoningmeng.player.PlayerManager;
import com.xiaoningmeng.player.PlayerManager.PlayState;
import com.xiaoningmeng.view.dialog.TipDialog;

import android.annotation.SuppressLint;
import android.content.Context;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AblumPlayListAdapter extends BaseAdapter implements
		OnClickListener {

	private LayoutInflater mInflater;
	private Context mContext;
	private List<Story> storys;
	private AlbumInfo albumInfo;
	private PlayingStory mPlayingStory;
	private String playStoryId;

	public AblumPlayListAdapter(Context context, List<Story> storys,
			AlbumInfo albumInfo,String playStoryId) {

		mContext = context;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.storys = storys;
		this.albumInfo = albumInfo;
		this.playStoryId = playStoryId;
		mPlayingStory = PlayerManager.getInstance().getPlayingStory();
	}

	@Override
	public int getCount() {
		return storys.size();
	}

	@Override
	public Story getItem(int position) {

		return storys.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public void notifyView(ListView listView,AudioDownLoad t){
		
		int fristVisiblePos = listView.getFirstVisiblePosition()-1;
		int lastVisiblePos = listView.getLastVisiblePosition()-1;
		for(int i = fristVisiblePos; i<=lastVisiblePos;i++){
			if(i >=0 && i< storys.size()){
				Story s = storys.get(i);
				if(t.getStoryId().equals(s.getStoryId())){
					ViewHolder holder = (ViewHolder) listView.getChildAt(i -fristVisiblePos).getTag();
					getDownloadView(holder, s);
					break;
				}
			}
		}
	}
	


	@SuppressLint("ViewHolder")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(
					R.layout.item_ablum_detail_play_list, null);
			holder.numberTv = (TextView) convertView
					.findViewById(R.id.tv_play_list_number);
			holder.statusImg = (ImageView) convertView
					.findViewById(R.id.img_play_list_status);
			holder.titleTv = (TextView) convertView
					.findViewById(R.id.tv_play_list_title);
			holder.downloadTv = (TextView) convertView
					.findViewById(R.id.tv_play_list_download);
			holder.sucessImg = (ImageView) convertView
					.findViewById(R.id.img_play_download_status);
			holder.progressBar = (ProgressBar) convertView
					.findViewById(R.id.rpb_download_progress);
			holder.downloadFl = convertView.findViewById(R.id.fl_download);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Story story = storys.get(position);
		holder.numberTv.setText((position + 1) + "");
		getPlayView(holder, story);
		getDownloadView(holder, story);
		holder.downloadFl.setTag(position);
		holder.downloadFl.setOnClickListener(this);
		holder.titleTv.setText(Html.fromHtml(story.getTitle()));
		return convertView;
	}

	private void getPlayView(ViewHolder holder, Story story) {
		if (story.getAlbum_id().equals(mPlayingStory.albumid)) {
			if(story.getMediapath().equals(mPlayingStory.mediapath)){
				holder.numberTv.setVisibility(View.INVISIBLE);
				holder.statusImg.setVisibility(View.VISIBLE);
				holder.titleTv.setSelected(mPlayingStory.playState == PlayState.RESUME || mPlayingStory.playState == PlayState.START || mPlayingStory.playState == PlayState.PLAY);
			}else{
				holder.numberTv.setVisibility(View.VISIBLE);
				holder.statusImg.setVisibility(View.INVISIBLE);
				holder.titleTv.setSelected(false);
			}
		}else if(story.getStoryId().equals(playStoryId)){
			holder.numberTv.setVisibility(View.INVISIBLE);
			holder.statusImg.setVisibility(View.VISIBLE);
			holder.titleTv.setSelected(false);
		} else {
			holder.numberTv.setVisibility(View.VISIBLE);
			holder.statusImg.setVisibility(View.INVISIBLE);
			holder.titleTv.setSelected(false);
		}
	}

	private void getDownloadView(ViewHolder holder, Story story) {
		int downloadStatus = DownLoadClientImpl.getInstance().getDownloadStatus(story.getMediapath());
		holder.progressBar.setMax(100);
		if (downloadStatus == -1) {
			holder.downloadTv.setVisibility(View.VISIBLE);
			holder.sucessImg.setVisibility(View.INVISIBLE);
			holder.progressBar.setVisibility(View.VISIBLE);
			holder.downloadTv.setText("下载");
			holder.progressBar.setProgress(0);
		} else if (downloadStatus == 1) {
			holder.downloadTv.setVisibility(View.INVISIBLE);
			holder.sucessImg.setVisibility(View.VISIBLE);
			holder.progressBar.setVisibility(View.INVISIBLE);
		} else {
			AudioDownLoad downLoad = DownLoadClientImpl.getInstance()
					.getDownloadArray().get(story.getMediapath());
			holder.downloadTv.setVisibility(View.VISIBLE);
			holder.progressBar.setVisibility(View.VISIBLE);
			if (downLoad != null)
				holder.progressBar.setProgress(downLoad.getProgress());
			holder.sucessImg.setVisibility(View.INVISIBLE);
			holder.downloadTv.setText("下载中");
		}
	}

	static class ViewHolder {
		TextView numberTv;
		ImageView statusImg;
		TextView titleTv;
		TextView downloadTv;
		ProgressBar progressBar;
		ImageView sucessImg;
		View downloadFl;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fl_download:
			int position = (int) v.getTag();
			Story story = storys.get(position);
			int downloadStatus = DownLoadClientImpl.getInstance().getDownloadStatus(story.getMediapath());
			if(downloadStatus == -1){
				AudioDownLoad downLoad = new AudioDownLoad(story,position);
				DownLoadClientImpl.getInstance().download(downLoad, albumInfo);
			}else if(downloadStatus == 0){
				new TipDialog.Builder(mContext).setAutoDismiss(true)
				.setTransparent(true).setTipText("嗯哈，正在下载中呢").create().show();
			}else{
				new TipDialog.Builder(mContext).setAutoDismiss(true)
				.setTransparent(true).setTipText("嗯哈，你已经下载过啦").create().show();
			}
			break;

		default:
			break;
		}
	}
}