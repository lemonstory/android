package com.xiaoningmeng;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.baoyz.swipemenu.xlistview.XListView;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.xiaoningmeng.adapter.RatingAdapter;
import com.xiaoningmeng.application.MyApplication;
import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.bean.PlayingStory;
import com.xiaoningmeng.bean.Rank;
import com.xiaoningmeng.bean.UserInfo;
import com.xiaoningmeng.http.JsonCallback;
import com.xiaoningmeng.http.LHttpRequest;
import com.xiaoningmeng.manager.PlayWaveManager;
import com.xiaoningmeng.player.PlayObserver;
import com.xiaoningmeng.player.PlayerManager;
import com.xiaoningmeng.view.dialog.DrawableDialogLoading;

import java.util.ArrayList;
import java.util.List;

public class RankActivity extends BaseActivity implements PlayObserver {
	
	private XListView mListView;
	private RatingAdapter mAdapter;
	private ImageView mCoverImg;
	private List<UserInfo> mUserInfos;
	private TextView mPositionTv;
	private TextView mTimeTv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Fresco.initialize(this);
		setContentView(R.layout.activity_rating);
		setTitleName("学霸排行榜");
		mListView = (XListView) findViewById(R.id.lv_home_discover);
		mCoverImg = (ImageView) findViewById(R.id.img_head_right);
		mPositionTv = (TextView) findViewById(R.id.tv_rank_position);
		mTimeTv = (TextView) findViewById(R.id.tv_rank_time);
		setRightHeadIcon(R.drawable.play_flag_wave_01);
		mUserInfos = new ArrayList<>();
		mListView.setPullLoadEnable(false);
		mListView.setPullRefreshEnable(false);
		mAdapter = new RatingAdapter(this,mUserInfos);
		View bottomView = new View(this);
		bottomView.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT,
				getResources().getDimensionPixelSize(R.dimen.download_item_cover_height)));
		mListView.addFooterView(bottomView);
		mListView.setAdapter(mAdapter);
		setLoading(new DrawableDialogLoading(this));
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				int pos = position - 1;
				if(pos>= 0 && mUserInfos.size() > pos){
					String uid = mUserInfos.get(pos).getUid();
					Intent i = new Intent(RankActivity.this,PerasonalActivity.class);
					i.putExtra("uid", uid);
					startActivityForNew(i);
				}
			}
		});
		requestData();
		PlayerManager.getInstance().register(this);
	}

	private void requestData() {
		LHttpRequest.getInstance().rankListenerUserListReq(this, 200,
				new JsonCallback<Rank>(this) {

					@Override
					public void onGetDataSuccess(Rank data) {
						mUserInfos.addAll(data.getList());
						mAdapter.notifyDataSetChanged();
						if(MyApplication.getInstance().isIsLogin()){
							mPositionTv.setText("您目前排名 " +data.getUserranknum());
							mTimeTv.setText("榜单更新时间 "+data.getUserrankuptime());
						}else{
							mPositionTv.setText("您还未登录 ");
						}
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
		super.onDestroy();
	}
}
