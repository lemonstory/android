package com.xiaoningmeng;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.xiaoningmeng.adapter.DownloadStoryAdapter;
import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.bean.AlbumInfo;
import com.xiaoningmeng.bean.AudioDownLoad;
import com.xiaoningmeng.bean.PlayingStory;
import com.xiaoningmeng.bean.Story;
import com.xiaoningmeng.download.DownLoadClientImpl;
import com.xiaoningmeng.download.DownLoadObserver;
import com.xiaoningmeng.download.DownLoadState;
import com.xiaoningmeng.manager.PlayWaveManager;
import com.xiaoningmeng.player.PlayObserver;
import com.xiaoningmeng.player.PlayerManager;
import com.xiaoningmeng.player.PlayerManager.AlbumSource;
import com.xiaoningmeng.utils.UiUtils;
import com.xiaoningmeng.view.dialog.TipDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class DownloadStoryActivity extends BaseActivity implements
		DownLoadObserver<AudioDownLoad>, PlayObserver, OnClickListener {

	private SwipeMenuListView mListView;
	private int type = 0;
	private String albumId;
	private AlbumInfo albumInfo;
	private TextView mDelBtnTv;
	private DownloadStoryAdapter mAdapter;
	private List<AudioDownLoad> downLoads;
	private TextView mBeignOrPauseTv;
	private ImageView mWaveImg;
	public static final int HISTORY_TYPE = 0;
	public static final int DOWNLOAD_TYPE = 1;
	public static final int DEL_HISTORY_TYPE = 2;
	private String tip = null;
	private int selectCount;

	private AtomicBoolean isNotify = new AtomicBoolean();
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			isNotify.set(false);
			mAdapter.notifyDataSetChanged();
			if(downLoads.size() == 0){
				showEmptyTip(tip);
			}else{
				hideEmptyTip();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_story_download);
		mListView = (SwipeMenuListView) findViewById(R.id.listView);
		mListView.setPullLoadEnable(false);
		mListView.setPullRefreshEnable(false);
		mWaveImg = (ImageView) findViewById(R.id.img_head_right);
		mDelBtnTv = (TextView) findViewById(R.id.tv_download_del_do);
		mBeignOrPauseTv = (TextView)findViewById(R.id.tv_batch_start);
		type = getIntent().getIntExtra("type", 0);
		albumInfo = getIntent().getParcelableExtra("album");
		if(albumInfo == null){
			finish();
			return;
		}
		albumId = albumInfo.getId();
		boolean isCheck = false;
		if (type == HISTORY_TYPE) {
			findViewById(R.id.ll_download_tab).setVisibility(View.GONE);
			downLoads = DownLoadClientImpl.getInstance().mHistoryMap.get(albumId);
			setTitleName(albumInfo.getTitle());
			setRightHeadIcon(R.drawable.ic_player_flag_wave_01);
			mDelBtnTv.setVisibility(View.GONE);
			isCheck = false;
			tip = "该专辑没有已下载的故事";
		} else if (type == DOWNLOAD_TYPE) {
			findViewById(R.id.rl_history_tab).setVisibility(View.GONE);
			downLoads = DownLoadClientImpl.getInstance().mDownloadMap.get(albumId);
			setTitleName(albumInfo.getTitle());
			setRightHeadIcon(R.drawable.ic_player_flag_wave_01);
			mDelBtnTv.setVisibility(View.GONE);
			isCheck = false;
			tip = "该专辑没有正在下载的故事";
		} else if (type == DEL_HISTORY_TYPE) {
			findViewById(R.id.ll_download_tab).setVisibility(View.GONE);
			findViewById(R.id.rl_history_tab).setVisibility(View.GONE);
			downLoads = DownLoadClientImpl.getInstance().mHistoryMap.get(albumId);
			setTitleName("批量删除");
			setRightHeadText("取消");
			tip = "该专辑没有已下载的故事";
			isCheck = true;
			mDelBtnTv.setVisibility(View.VISIBLE);
		}
		if(downLoads == null){
			downLoads = new ArrayList<>();
		}
		Collections.sort(downLoads);
		changedBeignOrPauseState(true);
		mAdapter = new DownloadStoryAdapter(this, type, downLoads, isCheck);
		if(downLoads.size() == 0){
			showEmptyTip(tip);
		}

		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {

				SwipeMenuItem openItem = new SwipeMenuItem(
						DownloadStoryActivity.this);
				openItem.setBackground(new ColorDrawable(getResources()
						.getColor(R.color.logout_bg_normal)));
				openItem.setWidth(UiUtils.getInstance(
						DownloadStoryActivity.this).DipToPixels(90));
				openItem.setTitle("删除");
				openItem.setTitleSize(18);
				openItem.setTitleColor(Color.WHITE);
				menu.addMenuItem(openItem);
			}
		};

		// set creator
		mListView.setMenuCreator(creator);
		mListView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
		mListView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(int position, SwipeMenu menu,
					int index) {
				switch (index) {
				case 0:
					AudioDownLoad downLoad = downLoads.remove(position);
					DownLoadClientImpl.getInstance().cancel(downLoad);
					if(downLoads.size() == 0){
						showEmptyTip(tip);
					}
					break;
				}
				return false;
			}
		});
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				int pos = position -1;
				if (type == HISTORY_TYPE) {
					List<Story> stories = new ArrayList<>();
					for (AudioDownLoad downLoad : downLoads) {
						Story s = downLoad.getStory();
						stories.add(s);
					}
					PlayerManager.getInstance().playStory(albumInfo, stories,pos, AlbumSource.DOWNLOAD);
				} else if (type == DOWNLOAD_TYPE) {
					if (!mAdapter.isCheck()) {
						AudioDownLoad downLoad = downLoads.get(pos);
						if (downLoad != null) {
							if (downLoad.getStatus() == DownLoadState.DOWN_PAUSE) {
								DownLoadClientImpl.getInstance().download(downLoad);
							} else {
								DownLoadClientImpl.getInstance().pause(downLoad);
							}
						}
					} else {
						AudioDownLoad downLoad = downLoads.get(pos);
						downLoad.setCheck(!downLoad.isCheck);
						if(downLoad.isCheck){
							selectCount++;
						}else{
							selectCount --;
						}
						mDelBtnTv.setText("删除选中"+(selectCount > 0 ?"("+selectCount+")":""));
						mAdapter.notifyDataSetChanged();
					}
				} else if (type == DEL_HISTORY_TYPE) {
					AudioDownLoad downLoad = downLoads.get(pos);
					downLoad.setCheck(!downLoad.isCheck);
					if(downLoad.isCheck){
						selectCount++;
					}else{
						selectCount --;
					}
					mDelBtnTv.setText("删除选中"+(selectCount > 0 ?"("+selectCount+")":""));
					mAdapter.notifyDataSetChanged();
				}
			}
		});
		DownLoadClientImpl.getInstance().registerObserver(this);
		PlayerManager.getInstance().register(this);
	}

	private void changedBeignOrPauseState(boolean removeCheck){
		int pauseCount = 0;
		int donwloadingCount = 0;
		for(AudioDownLoad download:downLoads){
			if(removeCheck)
				download.setCheck(false);
			if(download.getStatus() == DownLoadState.DOWN_PAUSE){
				pauseCount++;
			}else{
				donwloadingCount++;
			}
		}
		if(donwloadingCount == 0){
			mBeignOrPauseTv.setText("全部开始("+pauseCount+")");
			mBeignOrPauseTv.setTag(1);
		}else{
			mBeignOrPauseTv.setText("全部暂停("+donwloadingCount+")");
			mBeignOrPauseTv.setTag(0);
		}
	}
	
	@Override
	protected void onDestroy() {
		DownLoadClientImpl.getInstance().unregisterObserver(this);
		PlayerManager.getInstance().unRegister(this);
		super.onDestroy();
	}

	@Override
	public void notifyData(final AudioDownLoad t) {

			if (t.getAlbumid().equals(albumId)) {
				if(t.getStatus() == DownLoadState.DOWN_SUC){
					mHandler.sendEmptyMessage(1);
					changedBeignOrPauseState(false);
				}else if(t.getStatus() ==DownLoadState.DOWN_PAUSE){
					mHandler.sendEmptyMessage(1);
					changedBeignOrPauseState(false);
				}else if(t.getStatus() == DownLoadState.DOWN_CANCEL){
					changedBeignOrPauseState(false);
				}if(!isNotify.get()){
					isNotify.set(true);
					mHandler.sendEmptyMessageDelayed(1, 200);
				}
			}
	}


	@Override
	public void notifyCancel(final AudioDownLoad t) {
		
			if (t.getAlbumid().equals(albumId)) {

				mAdapter.notifyDataSetChanged();
				
			}
			
				
	}

	@Override
	protected void onResume() {
		PlayWaveManager.getInstance().loadWaveAnim(this, mWaveImg);
		super.onResume();
	}

	@Override
	public void notify(PlayingStory music) {
		if (type == HISTORY_TYPE || type == DOWNLOAD_TYPE)
			PlayWaveManager.getInstance().notify(music);
		switch (music.playState) {
		case PLAY:
			if (music.current == 0) {
				if (type == HISTORY_TYPE&& albumId.equals(PlayerManager.getInstance().getPlayingStory().albumid)) {
					mAdapter.notifyDataSetChanged();
				}
			}
			break;
		case START:
			if (type == HISTORY_TYPE&& albumId.equals(PlayerManager.getInstance().getPlayingStory().albumid)) {
				mAdapter.notifyDataSetChanged();
			}
			break;
		default:
			break;
		}

	}



	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_download_play_all:
			List<Story> stories = new ArrayList<>();
			for (AudioDownLoad downLoad : downLoads) {
				Story s = downLoad.getStory();
				stories.add(s);
			}
			PlayerManager.getInstance().playStory(albumInfo, stories, 0,AlbumSource.DOWNLOAD);
			break;
		case R.id.tv_download_more:
			if (downLoads != null && downLoads.size() > 0)
				DialogPlus.newDialog(this)
						.setContentHolder(
								new ViewHolder(R.layout.dialog_download_del))
						.setGravity(Gravity.BOTTOM)
						.setOnClickListener(
								new com.orhanobut.dialogplus.OnClickListener() {

									@Override
									public void onClick(DialogPlus dialog,
											View view) {
										switch (view.getId()) {
										case R.id.tv_download_del:
											dialog.dismiss();
											Intent i = new Intent(
													DownloadStoryActivity.this,
													DownloadStoryActivity.class);
											i.putExtra("type", DEL_HISTORY_TYPE);
											i.putExtra("album", albumInfo);
											startActivityForNew(i);
											break;
										case R.id.tv_download_cancel:
											dialog.dismiss();
											break;
										default:
											break;
										}

									}
								}).create().show();
			break;
		case R.id.tv_batch_start:
			//下载
			if(((int)v.getTag()) == 1){
				if (downLoads != null && downLoads.size() > 0) {
					for (AudioDownLoad downLoad : downLoads) {
						DownLoadClientImpl.getInstance().download(downLoad);
					}
					mAdapter.notifyDataSetChanged();
				}
			}else{ //暂停
				if (downLoads != null && downLoads.size() > 0) {
					for (AudioDownLoad downLoad : downLoads) {
						DownLoadClientImpl.getInstance().pause(downLoad);
					}
					mAdapter.notifyDataSetChanged();
				}
			}
			
			break;
		case R.id.tv_batch_delete:
			if (mAdapter.isCheck()) {
				mAdapter.setCheck(false);
				((TextView) v).setText("批量删除");
				v.setSelected(false);
				mAdapter.notifyDataSetChanged();
				mDelBtnTv.setVisibility(View.GONE);
			} else {
				mAdapter.setCheck(true);
				((TextView) v).setText("取消删除");
				v.setSelected(true);
				mAdapter.notifyDataSetChanged();
				mDelBtnTv.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.tv_download_del_do:
			if(selectCount == 0){
				new TipDialog.Builder(this)
				.setAutoDismiss(true).setTransparent(false)
				.setTipText("请选择需要删除故事").create().show();
				return;
			}
			List<AudioDownLoad> checkDownLoads = new ArrayList<>();
			for (AudioDownLoad d : downLoads) {
				if (d.isCheck) {
					checkDownLoads.add(d);
				}
			}
			for (AudioDownLoad downLoad : checkDownLoads) {
				DownLoadClientImpl.getInstance().cancel(downLoad);
			}
			selectCount = 0;
			if(downLoads.size() == 0){
				showEmptyTip(tip);
			}
			break;
		case R.id.tv_head_right:
			finish();
			break;
		default:
			break;
		}

	}

	
	TextView emptyView;
	public void showEmptyTip(String tip) {
		if(mListView.getHeaderViewsCount() == 1){
			if(emptyView == null){
				emptyView = (TextView) View.inflate(this,R.layout.fragment_empty, null);
			}
		emptyView = (TextView) View.inflate(this,R.layout.fragment_empty, null);
		emptyView.setText(tip);
		emptyView.setPadding(0, getResources().getDimensionPixelOffset(R.dimen.home_discover_item_img_height), 0, 0);
		mListView.addHeaderView(emptyView,null,false);
		mListView.setPullLoadEnable(false);
		}

	}
	

	public void hideEmptyTip() {
		if(emptyView != null && mListView.getHeaderViewsCount()>1){
			mListView.removeHeaderView(emptyView);
		}
	}
}
