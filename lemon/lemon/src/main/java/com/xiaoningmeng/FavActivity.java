package com.xiaoningmeng;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;

import com.baoyz.swipemenu.xlistview.XListView.IXListViewListener;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.orhanobut.dialogplus.DialogPlus;
import com.xiaoningmeng.adapter.MoreAdapter;
import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.bean.AlbumInfo;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.event.FavEvent;
import com.xiaoningmeng.http.LHttpHandler;
import com.xiaoningmeng.http.LHttpRequest;
import com.xiaoningmeng.manager.PlayWaveManager;
import com.xiaoningmeng.utils.UiUtils;
import com.xiaoningmeng.view.dialog.TipDialog;
import com.ypy.eventbus.EventBus;

public class FavActivity extends BaseActivity implements IXListViewListener {
	private SwipeMenuListView mListView;
	private List<AlbumInfo> mFaAlbumList;
	private BaseAdapter mAdapter;
	private ImageView mCoverImg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fav);
		setTitleName("我的收藏");
		mCoverImg = (ImageView) findViewById(R.id.img_head_right);
		setRightHeadIcon(R.drawable.play_flag_wave_01);
		mListView = (SwipeMenuListView) findViewById(R.id.lv_home_discover);
		mListView.setPullLoadEnable(false);
		mListView.setPullRefreshEnable(true);
		mFaAlbumList = new ArrayList<>();
		mAdapter = new MoreAdapter(this, mFaAlbumList,true);
		mListView.setAdapter(mAdapter);
		showLoadingTip();
		mListView.setXListViewListener(this);
		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {

				SwipeMenuItem openItem = new SwipeMenuItem(
						FavActivity.this);
				openItem.setBackground(new ColorDrawable(getResources()
						.getColor(R.color.logout_bg_normal)));
				openItem.setWidth(UiUtils.getInstance(
						FavActivity.this).DipToPixels(90));
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
					showDelDialog(position);
				}
				return false;
			}
		});
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				int pos = position - 1;
				if(pos >= 0){
				Intent i = new Intent(FavActivity.this,AblumDetailActivity.class);
				i.putExtra("albumId", mFaAlbumList.get(pos).getAlbumid());
				startActivityForNew(i);
				}

			}
		});
		requestFavData(Constant.FRIST, null);
		EventBus.getDefault().register(this);
		//Slidr.attach(this);
	}
	
	private void showDelDialog(final int position){
		new TipDialog.Builder(this)
		.setHasBtn(true)
		.setTipText("确定取消收藏该专辑？")
		.setEnterText("删除")
		.setTransparent(false)
		.setOnClickListener(
				new com.orhanobut.dialogplus.OnClickListener() {

					@Override
					public void onClick(DialogPlus dialog, View view) {
						switch (view.getId()) {
						case R.id.tv_dialog_enter:
							delFavAblum(position);
							dialog.dismiss();
							break;
						case R.id.tv_dialog_cancel:
							dialog.dismiss();
							break;
						}
					}
				}).create().show();
	}
	private void delFavAblum(final int position) {
			final AlbumInfo albumInfo = mFaAlbumList.get(position);
			final String albumId = albumInfo.getAlbumid();
			LHttpRequest.getInstance().delFavAlbumRequest(this,albumId,
					new LHttpHandler<String>(this) {

						@Override
						public void onGetDataSuccess(String data) {
							albumInfo.updateAll("albumid =?",albumId);
							mFaAlbumList.remove(position);
							mAdapter.notifyDataSetChanged();
							showEmptyTip("收藏的故事会出现在这里喔.");
							
						}
			});
	}

	private void requestFavData(final String direction, String startId) {
		LHttpRequest.getInstance().getFavAlbumListRequest(this, direction,
				startId, 10, new LHttpHandler<List<AlbumInfo>>(this) {

					@Override
					public void onGetDataSuccess(List<AlbumInfo> data) {
						hideLoadingTip();
						if (data != null) {
							if (direction == Constant.FRIST) {
								mFaAlbumList.clear();
								if (data.size() < 10) {
									mListView.setPullLoadEnable(false);
								}else{
									mListView.setPullLoadEnable(true);
								}
							} else if (direction == Constant.DOWN) {
								if (data.size() < 10) {
									mListView.setFootViewNoMore(true);
								}
							}else if(direction == Constant.UP){
								mFaAlbumList.clear();
							}
							mFaAlbumList.addAll(data);
							mAdapter.notifyDataSetChanged();
						}	
						showEmptyTip("收藏的故事会出现在这里喔.");		
					}
					
					@Override
					public void onFailure(int statusCode, Header[] headers,
							String responseString, Throwable throwable) {
						hideLoadingTip();
						super.onFailure(statusCode, headers, responseString, throwable);
					}

					@Override
					public void onFinish() {
						onLoad();
						super.onFinish();
					}
				});
	}

	@Override
	public void onRefresh() {

		requestFavData(Constant.FRIST, Constant.FRIST_ID);
		
	}

	@Override
	public void onLoadMore() {
		int size = mFaAlbumList.size();
		if (size > 0) {
		
			String startId = mFaAlbumList.get(size - 1).getFavid();
			requestFavData(Constant.DOWN, startId);
		} else {
			requestFavData(Constant.FRIST, Constant.FRIST_ID);
		}
	}

	private void onLoad() {

		mListView.stopRefresh();
		mListView.stopLoadMore();
	}
	
	TextView emptyView;
	public void showEmptyTip(String tip) {
		if(mFaAlbumList.size() == 0 && mListView.getHeaderViewsCount() == 1){
			if(emptyView == null){
				emptyView = (TextView) View.inflate(this,R.layout.fragment_empty, null);
			}
			emptyView.setText(tip);
			mListView.addHeaderView(emptyView,null,false);
			mListView.setPullLoadEnable(false);
			mListView.setPullRefreshEnable(false);
		}
	}
	
	public void hideEmptyTip() {
		if(emptyView != null && mListView.getHeaderViewsCount() > 1){
			mListView.removeHeaderView(emptyView);
		}
		mListView.setPullRefreshEnable(true);
		mListView.setPullLoadEnable(true);
	}
	
	
	View loadingView;
	public void showLoadingTip() {
		if(mListView.getHeaderViewsCount() == 1){
			if(loadingView == null){
				loadingView = View.inflate(this,R.layout.fragment_loading, null);
				loadingView.setPadding(0, getResources().getDimensionPixelOffset(R.dimen.home_banner_height), 0, 0);
			}
			mListView.addHeaderView(loadingView,null,false);
		}
	}
	
	public void hideLoadingTip() {
		
		if(loadingView != null && mListView.getHeaderViewsCount() > 1){
			mListView.removeHeaderView(loadingView);
		}
	}
	
	@Override
	protected void onResume() {
		PlayWaveManager.getInstance().loadWaveAnim(this, mCoverImg);
		super.onResume();

	}
	
	@Override
	protected void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}
	
	public void onEventMainThread(FavEvent favEvent){
		if(favEvent.fav == 1){
			mFaAlbumList.add(favEvent.albumInfo);
		}else{
			for(int i = 0; i<mFaAlbumList.size();i++){
				if(favEvent.albumInfo.getAlbumid().equals(mFaAlbumList.get(i).getAlbumid())){
					mFaAlbumList.remove(i);
				}
			}
		}
		mAdapter.notifyDataSetChanged();
	}
}
