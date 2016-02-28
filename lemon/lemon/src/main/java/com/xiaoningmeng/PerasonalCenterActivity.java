package com.xiaoningmeng;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.baoyz.swipemenu.xlistview.XListView;
import com.baoyz.swipemenu.xlistview.XListView.IXListViewListener;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.xiaoningmeng.adapter.PerasonalAdapter;
import com.xiaoningmeng.application.MyApplication;
import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.bean.AlbumInfo;
import com.xiaoningmeng.bean.HomeInfo;
import com.xiaoningmeng.bean.ListenerAlbum;
import com.xiaoningmeng.bean.PlayingStory;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.event.HistoryEvent;
import com.xiaoningmeng.http.LHttpHandler;
import com.xiaoningmeng.http.LHttpRequest;
import com.xiaoningmeng.manager.PlayWaveManager;
import com.xiaoningmeng.player.PlayObserver;
import com.xiaoningmeng.player.PlayerManager;
import com.xiaoningmeng.utils.AvatarUtils;
import com.ypy.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class PerasonalCenterActivity extends BaseActivity implements
		PlayObserver, OnClickListener,IXListViewListener {
	private XListView mListView;
	private ImageView mCoverImg;
	private TextView mAccountNameTv;
	private TextView mAccountContentTv;
	private SimpleDraweeView mAvatarView;
	private List<ListenerAlbum> mAlbumList;
	private BaseAdapter mAdapter;
	private String uid;
	private View headerView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		Fresco.initialize(this);
		setContentView(R.layout.activity_perasonal_center);
		initView();
		uid = getIntent().getStringExtra("uid");
		requestHomeInfo(Constant.FRIST, Constant.FRIST_ID);
		PlayerManager.getInstance().register(this);
		EventBus.getDefault().register(this);
	}

	private void initView() {
		
		mListView = (XListView) findViewById(R.id.lv_home_discover);
		mCoverImg = (ImageView) findViewById(R.id.img_head_right);
		setRightHeadIcon(R.drawable.play_flag_wave_01);
		mListView.setPullLoadEnable(false);
		mListView.setPullRefreshEnable(false);
		mListView.setXListViewListener(this);
		headerView = View.inflate(this, R.layout.layout_perasonal_head,null);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				int pos = position -2;
				ListenerAlbum album = mAlbumList.get(pos);
				AlbumInfo albumInfo = album.getAlbuminfo();
				Intent intent = new Intent(PerasonalCenterActivity.this, AblumDetailActivity.class);
				intent.putExtra("albumId", albumInfo.getAlbumid());
				startActivityForNew(intent);
				if(uid != null && uid.equals(MyApplication.getInstance().getUid())){
				mAlbumList.add(0, mAlbumList.remove(pos));
				mAdapter.notifyDataSetChanged();
				}	
			}
		});
		mAccountNameTv = (TextView) headerView.findViewById(R.id.tv_account_name);
		mAccountContentTv = (TextView) headerView.findViewById(R.id.tv_account_content);
		mAvatarView = (SimpleDraweeView)headerView.findViewById(R.id.img_perasonal_icon);
		mAlbumList = new ArrayList<>();
		mAdapter = new PerasonalAdapter(this,mAlbumList);
		mListView.setAdapter(mAdapter);
	}
	
	public void requestHomeInfo(final String direction,String startStroyId){
		
		if(uid == null || uid.equals(MyApplication.getInstance().getUid())){
			uid = MyApplication.getInstance().getUid();
		}
		LHttpRequest.getInstance().getHomeInfoReq(this,uid, direction, startStroyId, Constant.MAX_REQ_LEN, new LHttpHandler<HomeInfo>(this) {

			@Override
			public void onGetDataSuccess(HomeInfo data) {
				List<ListenerAlbum> albums = data.getListenalbumlist();
				if(direction == Constant.FRIST){
					mListView.addHeaderView(headerView,null,false);
					mAccountNameTv.setText(data.getNickname());
					setTitleName(data.getNickname());
					String age = data.getAge() == null ?"":(data.getAge()+"岁");
					String province = data.getProvince() == null ?" ":(" "+data.getProvince()+" ");
					String city = data.getCity() == null?"":data.getCity();
					mAccountContentTv.setText(age + province + city);
					String avatarUrl = AvatarUtils.getAvatarUrl(data.getUid(), data.getAvatartime(), -1);
					Uri avatarUri = Uri.parse(avatarUrl);
					mAvatarView.setImageURI(avatarUri);
				}
				if(albums != null && albums.size() > 0){
					if(direction == Constant.FRIST){
						if(albums.size() == Constant.MAX_REQ_LEN){
							mListView.setPullLoadEnable(true);
						}else{
							mListView.setPullLoadEnable(false);
						}
					}else{
						if(albums.size() == 0){
							mListView.setFootViewNoMore(true);
						}
					}
					mAlbumList.addAll(albums);
					mAdapter.notifyDataSetChanged();
				}
				if(mAlbumList.size() == 0){
					showEmptyTip("Ta还没收听的故事喔");
				}
			}
			
			@Override
			public void onFinish() {
				onLoad();
				super.onFinish();
			}
		});
	}

	@Override
	protected void onResume() {
		PlayWaveManager.getInstance().loadWaveAnim(this, mCoverImg);
		super.onResume();

	}

	@Override
	public void notify(PlayingStory music) {
		PlayWaveManager.getInstance().notify(music);

	}


	@Override
	public void onDestroy() {
		PlayerManager.getInstance().unRegister(this);
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_perasonal_head:
			break;

		default:
			break;
		}

	}

	@Override
	public void onRefresh() {
		if(mAlbumList.size() > 0){
			requestHomeInfo(Constant.UP, mAlbumList.get(0).getAlbumid());
		}else{
			requestHomeInfo(Constant.FRIST, Constant.FRIST_ID);
		}
		
	}

	@Override
	public void onLoadMore() {
		int size= mAlbumList.size();
		if(size > 0){
			requestHomeInfo(Constant.DOWN, mAlbumList.get(size-1).getAlbumid());
		}else{
			requestHomeInfo(Constant.FRIST, Constant.FRIST_ID);
		}
	}
	
	private void onLoad() {

		mListView.stopRefresh();
		mListView.stopLoadMore();
	}
	
	public void onEventMainThread(HistoryEvent historyEvent){
		if(uid != null && uid.equals(MyApplication.getInstance().getUid())){
			hideEmptyTip();
			int albumSzie = mAlbumList.size();
				for(int i = 0; i <albumSzie ; i++){
					ListenerAlbum album = mAlbumList.get(i);
					if(album.getAlbumid().equals(historyEvent.albumId)){
						if(historyEvent.listenerAlbum == null){
							album.setPlaystoryid(historyEvent.storyId);
							album.setUptime(System.currentTimeMillis()/1000+"");
							if(i != 0){
								mAlbumList.remove(album);
								mAlbumList.add(0,album);
						}
							mAdapter.notifyDataSetChanged();
							return;
						}else{
							mAlbumList.remove(album);
							break;
						}
					}
				}
		mAlbumList.add(0,historyEvent.listenerAlbum);
		mAdapter.notifyDataSetChanged();
		}
	}
	
	TextView emptyView;
	public void showEmptyTip(String tip) {
		emptyView = (TextView) View.inflate(this,R.layout.fragment_empty, null);
		emptyView.setText(tip);
		emptyView.setPadding(0, getResources().getDimensionPixelOffset(R.dimen.home_discover_item_img_height), 0, 0);
		mListView.addHeaderView(emptyView, null, false);
		mListView.setPullLoadEnable(false);

	}
	

	public void hideEmptyTip() {
		if(emptyView != null && mListView.getHeaderViewsCount()>2){
			mListView.removeHeaderView(emptyView);
		}
		mListView.setPullLoadEnable(true);
	}
}
