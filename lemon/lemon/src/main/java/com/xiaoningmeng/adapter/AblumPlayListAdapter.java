package com.xiaoningmeng.adapter;

import android.text.Html;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoningmeng.R;
import com.xiaoningmeng.bean.AudioDownLoad;
import com.xiaoningmeng.bean.PlayingStory;
import com.xiaoningmeng.bean.Story;
import com.xiaoningmeng.download.DownLoadClientImpl;
import com.xiaoningmeng.player.PlayerManager;
import com.xiaoningmeng.player.PlayerManager.PlayState;

import java.util.List;

public class AblumPlayListAdapter extends BaseQuickAdapter<Story,BaseViewHolder>{

	private PlayingStory mPlayingStory;
	private String playStoryId;

	public AblumPlayListAdapter(List<Story> data) {

		super(R.layout.item_ablum_detail_play_list, data);
		mPlayingStory = PlayerManager.getInstance().getPlayingStory();
	}

	@Override
	protected void convert(BaseViewHolder helper, Story item) {
		helper.setText(R.id.tv_play_list_number,helper.getAdapterPosition() + 1 + "")
				.setText(R.id.tv_play_list_title,Html.fromHtml(item.getTitle()))
				.addOnClickListener(R.id.fl_download);
		getPlayView(helper,item);
		getDownloadView(helper,item);
	}


	public void setPlayStoryId(String playStoryId){
		this.playStoryId = playStoryId;
	}

	

	

	private void getPlayView(BaseViewHolder helper, Story story) {
		if (story.getAlbum_id() != null && story.getAlbum_id().equals(mPlayingStory.albumid)) {
			if(story.getMediapath().equals(mPlayingStory.mediapath)){
				helper.getView(R.id.tv_play_list_number).setVisibility(View.INVISIBLE);
				helper.getView(R.id.img_play_list_status).setVisibility(View.VISIBLE);
				helper.getView(R.id.tv_play_list_title).setSelected(mPlayingStory.playState == PlayState.RESUME
						|| mPlayingStory.playState == PlayState.START || mPlayingStory.playState == PlayState.PLAY);
			}else{
				helper.getView(R.id.tv_play_list_number).setVisibility(View.VISIBLE);
				helper.getView(R.id.img_play_list_status).setVisibility(View.INVISIBLE);
				helper.getView(R.id.tv_play_list_title).setSelected(false);
			}
		}else if(story.getId() != null && story.getId().equals(playStoryId)){
			helper.getView(R.id.tv_play_list_number).setVisibility(View.INVISIBLE);
			helper.getView(R.id.img_play_list_status).setVisibility(View.VISIBLE);
			helper.getView(R.id.tv_play_list_title).setSelected(false);
		} else {
			helper.getView(R.id.tv_play_list_number).setVisibility(View.VISIBLE);
			helper.getView(R.id.img_play_list_status).setVisibility(View.INVISIBLE);
			helper.getView(R.id.tv_play_list_title).setSelected(false);
		}
	}

	private void getDownloadView(BaseViewHolder helper, Story story) {
		int downloadStatus = DownLoadClientImpl.getInstance().getDownloadStatus(story.getMediapath());
		ProgressBar progressBar =  helper.getView(R.id.rpb_download_progress);
		TextView downloadTv = helper.getView(R.id.tv_play_list_download);
		View sucessImg =  helper.getView(R.id.img_play_download_status);
		progressBar.setMax(100);
		if (downloadStatus == -1) {
			downloadTv.setVisibility(View.VISIBLE);
			sucessImg.setVisibility(View.INVISIBLE);
			progressBar.setVisibility(View.VISIBLE);
			downloadTv.setText("下载");
			progressBar.setProgress(0);
		} else if (downloadStatus == 1) {
			downloadTv.setVisibility(View.INVISIBLE);
			sucessImg.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.INVISIBLE);
		} else {
			AudioDownLoad downLoad = DownLoadClientImpl.getInstance()
					.getDownloadArray().get(story.getMediapath());
			downloadTv.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.VISIBLE);
			if (downLoad != null)
				progressBar.setProgress(downLoad.getProgress());
			sucessImg.setVisibility(View.INVISIBLE);
			downloadTv.setText("下载中");
		}
	}




}