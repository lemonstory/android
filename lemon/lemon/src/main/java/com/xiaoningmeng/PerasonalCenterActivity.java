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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baoyz.swipemenu.xlistview.XListView;
import com.baoyz.swipemenu.xlistview.XListView.IXListViewListener;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.xiaoningmeng.adapter.PerasonalAdapter;
import com.xiaoningmeng.application.MyApplication;
import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.bean.AlbumInfo;
import com.xiaoningmeng.bean.HomeInfo;
import com.xiaoningmeng.bean.ListenerAlbum;
import com.xiaoningmeng.bean.PlayingStory;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.event.HistoryEvent;
import com.xiaoningmeng.http.JsonCallback;
import com.xiaoningmeng.http.LHttpRequest;
import com.xiaoningmeng.manager.PlayWaveManager;
import com.xiaoningmeng.player.PlayObserver;
import com.xiaoningmeng.player.PlayerManager;
import com.xiaoningmeng.utils.AvatarUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class PerasonalCenterActivity extends BaseActivity implements
		PlayObserver, OnClickListener,IXListViewListener {
	private XListView mListView;
	private ImageView mCoverImg;
	private TextView mAccountNameTv;
	private TextView mAccountContentTv;
	private TextView mAccountPostTv;
	private LinearLayout mAccountPostContainer;
	private SimpleDraweeView mAvatarView;
	private List<ListenerAlbum> mAlbumList;
	private BaseAdapter mAdapter;
	private String uid;
	private String nickname;
	private View headerView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		Fresco.initialize(this);
		setContentView(R.layout.activity_perasonal_center);
		initView();
		uid = getIntent().getStringExtra("uid");
		requestHomeInfo(Constant.FRIST, Constant.FRIST_ID);
		getUserProfileData(uid);
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
		mAccountPostTv = (TextView) headerView.findViewById(R.id.tv_account_post);
		mAccountPostContainer = (LinearLayout) headerView.findViewById(R.id.ll_account_post_container);
		mAlbumList = new ArrayList<>();
		mAdapter = new PerasonalAdapter(this,mAlbumList);
		mListView.setAdapter(mAdapter);
	}
	
	public void requestHomeInfo(final String direction,String startStroyId){
		
		if(uid == null || uid.equals(MyApplication.getInstance().getUid())){
			uid = MyApplication.getInstance().getUid();
		}
		LHttpRequest.getInstance().getHomeInfoReq(this,uid, direction, startStroyId, Constant.MAX_REQ_LEN, new JsonCallback<HomeInfo>() {

			@Override
			public void onGetDataSuccess(HomeInfo data) {
				List<ListenerAlbum> albums = data.getListenalbumlist();
				if(direction == Constant.FRIST){
					mListView.addHeaderView(headerView,null,false);
					nickname = data.getNickname();
					mAccountNameTv.setText(nickname);
					setTitleName(nickname);
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

		case R.id.ll_account_post_container:
			Intent intent = new Intent(this, MyThreadActivity.class);
			intent.putExtra("uid", uid);
			intent.putExtra("nickname",nickname);
			this.startActivityForNew(intent);
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

		public void getUserProfileData(final String uid) {

		LHttpRequest.getInstance().getUserProfile(this,
				new JsonCallback<String>() {

					@Override
					public void onGetDataSuccess(String data) {

						try {

							JSONObject jsonObject = new JSONObject(data);
							JSONObject variablesObject = new JSONObject(jsonObject.getString("Variables"));

							Gson gson = new Gson();
							if (variablesObject.has("space")) {

								//设置帖子数
								JSONObject spaceObject = new JSONObject(variablesObject.getString("space"));


								if (spaceObject.has("uid") && spaceObject.has("threads")) {
									if (uid.equals(spaceObject.getString("uid"))) {
										mAccountPostContainer.setVisibility(View.VISIBLE);
										mAccountPostTv.setText(spaceObject.getString("threads"));
										int postCount = Integer.parseInt(spaceObject.getString("threads"));
										if (postCount > 0) {
											mAccountPostContainer.setEnabled(true);
										}else {
											mAccountPostContainer.setEnabled(false);
										}
									}else {
										//到这来就是系统出错了
									}
								}else {
									mAccountPostContainer.setEnabled(false);
									mAccountPostTv.setText("0");
								}
							}

						} catch (JSONException e) {

							e.printStackTrace();
						}
					}
				},uid);
	}
}
