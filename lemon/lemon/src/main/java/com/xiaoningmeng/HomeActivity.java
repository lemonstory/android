package com.xiaoningmeng;

import com.orhanobut.dialogplus.DialogPlus;
import com.umeng.onlineconfig.OnlineConfigAgent;
import com.xiaoningmeng.base.BaseFragmentActivity;
import com.xiaoningmeng.bean.PlayingStory;
import com.xiaoningmeng.bean.UserInfo;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.event.LoginEvent;
import com.xiaoningmeng.fragment.AccountFragment;
import com.xiaoningmeng.fragment.DiscoverFragment;
import com.xiaoningmeng.fragment.ForumIndexFragment;
import com.xiaoningmeng.fragment.MineFragment;
import com.xiaoningmeng.fragment.CricleFragment;
import com.xiaoningmeng.http.LHttpHandler;
import com.xiaoningmeng.http.LHttpRequest;
import com.xiaoningmeng.manager.DownloadApkManager;
import com.xiaoningmeng.manager.PlayWaveManager;
import com.xiaoningmeng.player.MusicService;
import com.xiaoningmeng.player.PlayObserver;
import com.xiaoningmeng.player.PlayerManager;
import com.xiaoningmeng.utils.PreferenceUtil;
import com.xiaoningmeng.view.dialog.TipDialog;
import com.ypy.eventbus.EventBus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.Header;

public class HomeActivity extends BaseFragmentActivity implements
		OnClickListener, PlayObserver {

	private DiscoverFragment mDiscoverFragment;
	private MineFragment mMineFragment;
	private CricleFragment mSearchFragment;
	private AccountFragment mAccountFragment;
	private ForumIndexFragment mForumIndexFragment;
	private TextView mDisCoverTabTv;
	private TextView mMineTabTv;
	private TextView mForumTabTv;
	private TextView mPerasonTabTv;
	private ImageView mCoverImg;

	private TextView mTitleTv;
	private ImageView mTitleImg;

	public static final String FRAG_DISCOVER = "FRAG_DISCOVER";
	public static final String FRAG_MINE = "FRAG_MINE";
	public static final String FRAG_FORUM = "FRAG_FORUM";
	public static final String FRAG_ACCOUNT = "FRAG_ACCOUNT";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		OnlineConfigAgent.getInstance().updateOnlineConfig(this);
		initView();
		PlayerManager.getInstance().register(this);
		EventBus.getDefault().register(this);

	}

	private void initView() {

		mDiscoverFragment = new DiscoverFragment();
		mDisCoverTabTv = (TextView) this.findViewById(R.id.tv_home_discover);
		mMineTabTv = (TextView) this.findViewById(R.id.tv_home_mine);
		mForumTabTv = (TextView) this.findViewById(R.id.tv_home_forum);
		mPerasonTabTv = (TextView) this.findViewById(R.id.tv_home_account);
		mCoverImg = (ImageView) findViewById(R.id.img_home_cover);
		mTitleTv = (TextView)findViewById(R.id.tv_head_title);
		mTitleImg = (ImageView)findViewById(R.id.img_head_title);
		setTabSelect(0);
		reminderTip();
		shareTip();
	}


	private void reminderTip() {
		int remindCountDown = PreferenceUtil.getInt("Remind_countdown");
		if (remindCountDown <= Constant.REMINDER_COUNTDOWN) {
			if (remindCountDown == Constant.REMINDER_COUNTDOWN) {
				new TipDialog.Builder(this).setHasBtn(true)
						.setTipText(getResources().getString(R.string.remider_tip))
						.setEnterText("设置")
						.setTransparent(false)
						.setOnClickListener(
								new com.orhanobut.dialogplus.OnClickListener() {

									@Override
									public void onClick(DialogPlus dialog, View view) {
										switch (view.getId()) {
											case R.id.tv_dialog_enter:
												Intent i = new Intent(HomeActivity.this, AlarmClockActivity.class);
												i.putExtra("isOpenAlarm", true);
												startActivityForNew(i);
												dialog.dismiss();
												break;
											case R.id.tv_dialog_cancel:
												dialog.dismiss();
												break;
										}
									}
								}).create().show();
			}

			remindCountDown++;
			PreferenceUtil.putInt("Remind_countdown", remindCountDown);
		}
	}

	private void shareTip() {
		int remindCountDown = PreferenceUtil.getInt("Share_countdown");
		if(remindCountDown <= Constant.SHARE_COUNTDOWN){
			if(remindCountDown == Constant.SHARE_COUNTDOWN){
				new TipDialog.Builder(this).setLayoutId(R.layout.dialog_prompt)
						.setNeedBg(true)
						.setAutoDismiss(false).create().show();
			}
			remindCountDown++;
			PreferenceUtil.putInt("Share_countdown", remindCountDown);
		}
	}





	public void setTabSelect(int i) {

		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		resetTab();
		switch (i) {
			case 0:
				mTitleTv.setVisibility(View.INVISIBLE);
				mTitleImg.setVisibility(View.VISIBLE);
				mDiscoverFragment = (DiscoverFragment) manager.findFragmentByTag(FRAG_DISCOVER);
				hideTab(transaction);
				if (mDiscoverFragment == null) {
					mDiscoverFragment = new DiscoverFragment();
					transaction.add(R.id.fl_fragment, mDiscoverFragment,
							FRAG_DISCOVER);
				} else {
					transaction.show(mDiscoverFragment);
				}
				mDisCoverTabTv.setSelected(true);
				transaction.commitAllowingStateLoss();
				break;
			case 1:
				mTitleTv.setText("我的故事");
				mTitleTv.setVisibility(View.VISIBLE);
				mTitleImg.setVisibility(View.INVISIBLE);
				mMineFragment = (MineFragment) manager.findFragmentByTag(FRAG_MINE);
				hideTab(transaction);
				if (mMineFragment == null) {
					mMineFragment = new MineFragment();
					transaction.add(R.id.fl_fragment, mMineFragment, FRAG_MINE);
				} else {
					transaction.show(mMineFragment);
				}
				mMineTabTv.setSelected(true);
				transaction.commitAllowingStateLoss();
				break;
			case 2:
				mTitleImg.setVisibility(View.INVISIBLE);
				mTitleTv.setVisibility(View.VISIBLE);
				mTitleTv.setText("圈子");
				mForumIndexFragment = (ForumIndexFragment) manager
						.findFragmentByTag(FRAG_FORUM);
				hideTab(transaction);
				if (mForumIndexFragment == null) {
					mForumIndexFragment = new ForumIndexFragment();
					transaction.add(R.id.fl_fragment, mForumIndexFragment, FRAG_FORUM);
				} else {
					transaction.show(mForumIndexFragment);
				}
				mForumTabTv.setSelected(true);
				transaction.commitAllowingStateLoss();
				break;
			case 3:
				mTitleImg.setVisibility(View.INVISIBLE);
				mTitleTv.setVisibility(View.VISIBLE);
				mTitleTv.setText("账号");
				mAccountFragment = (AccountFragment) manager
						.findFragmentByTag(FRAG_ACCOUNT);
				hideTab(transaction);
				if (mAccountFragment == null) {
					mAccountFragment = new AccountFragment();
					transaction.add(R.id.fl_fragment, mAccountFragment,FRAG_ACCOUNT);
				} else {
					transaction.show(mAccountFragment);
				}
				mPerasonTabTv.setSelected(true);
				transaction.commitAllowingStateLoss();
				break;
			default:

				break;
		}
	}

	private void resetTab() {

		mDisCoverTabTv.setSelected(false);
		mMineTabTv.setSelected(false);
		mForumTabTv.setSelected(false);
		mPerasonTabTv.setSelected(false);
	}

	private void hideTab(FragmentTransaction transaction) {
		if (mDiscoverFragment != null) {
			transaction.hide(mDiscoverFragment);
		}
		if (mMineFragment != null) {
			transaction.hide(mMineFragment);
		}
		if (mForumIndexFragment != null) {
			transaction.hide(mForumIndexFragment);
		}
		if (mAccountFragment != null) {
			transaction.hide(mAccountFragment);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	@Override
	protected void onResume() {
		super.onResume();
		PlayWaveManager.getInstance().loadWaveAnim(this, mCoverImg);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.tv_home_discover:
				setTabSelect(0);
				break;
			case R.id.tv_home_mine:
				setTabSelect(1);
				break;
			case R.id.tv_home_forum:
				setTabSelect(2);
				break;
			case R.id.tv_home_account:
				setTabSelect(3);
				break;
			case R.id.img_head_search:

				startActivity(new Intent(this,SearchActivity.class));
				overridePendingTransition(R.anim.search_translatey100to0,
						R.anim.search_translatey0tof100);
				break;
			default:
				break;
		}
	}




	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			showCancelAppDialog();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	//显示退出应用的dialog
	private void showCancelAppDialog() {
		new TipDialog.Builder(this)
				.setHasBtn(true)
				.setTipText("确定退出小柠檬？")
				.setShieldActionUp(true)
				.setLayoutId(R.layout.dialog_cancel_tip)
				.setAutoDismiss(false)
				.setOnClickListener(
						new com.orhanobut.dialogplus.OnClickListener() {

							@Override
							public void onClick(DialogPlus dialog, View view) {
								dialog.dismiss();
								switch (view.getId()) {
									case R.id.tv_dialog_enter:
										MusicService.stopService(HomeActivity.this);
										oldFinish();

										break;
									case R.id.tv_dialog_min:
										oldFinish();
										break;
								}

							}
						}).create().show();
	}

	@Override
	public void notify(PlayingStory music) {
		PlayWaveManager.getInstance().notify(music);
	}

	//向uc同步用户登录信息
	public void onEventAsync(LoginEvent event) {

		UserInfo userinfo = event.userInfo;
		String uc_callback = userinfo.getUcCallback();
		LHttpRequest.getInstance().UCSyncLoginRequest(HomeActivity.this, uc_callback, new LHttpHandler<UserInfo>(HomeActivity.this) {

			@Override
			public void onGetDataSuccess(UserInfo data) {
				//
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
								  String responseString, Throwable throwable) {
				//DebugUtils.e("UCSyncLoginRequest onFailure callback is run");
				//TODO:uc_callback的接口返回，不是项目接口的标准规范，所以会进入到这个回调里面
			}
		});
	}

	@Override
	public void onDestroy() {
		PlayerManager.getInstance().unRegister(this);
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
}
