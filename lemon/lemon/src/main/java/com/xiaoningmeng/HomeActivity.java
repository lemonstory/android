package com.xiaoningmeng;

import com.orhanobut.dialogplus.DialogPlus;
import com.umeng.onlineconfig.OnlineConfigAgent;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.xiaoningmeng.base.BaseFragmentActivity;
import com.xiaoningmeng.bean.PlayingStory;
import com.xiaoningmeng.bean.ShareBean;
import com.xiaoningmeng.bean.UserInfo;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.event.LoginEvent;
import com.xiaoningmeng.fragment.AccountFragment;
import com.xiaoningmeng.fragment.DiscoverFragment;
import com.xiaoningmeng.fragment.MineFragment;
import com.xiaoningmeng.fragment.SearchFragment;
import com.xiaoningmeng.http.LHttpHandler;
import com.xiaoningmeng.http.LHttpRequest;
import com.xiaoningmeng.manager.PlayWaveManager;
import com.xiaoningmeng.player.MusicService;
import com.xiaoningmeng.player.PlayObserver;
import com.xiaoningmeng.player.PlayerManager;
import com.xiaoningmeng.utils.PreferenceUtil;
import com.xiaoningmeng.view.SearchView;
import com.xiaoningmeng.view.ShareDialog;
import com.xiaoningmeng.view.dialog.TipDialog;
import com.ypy.eventbus.EventBus;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
	public SearchFragment mSearchFragment;
	private AccountFragment mAccountFragment;
	private TextView mDisCoverTabTv;
	private TextView mMineTabTv;
	private TextView mSearchTabTv;
	private TextView mPerasonTabTv;
	private ImageView mCoverImg;

	private TextView mTitleTv;
	private ImageView mTitleImg;
	public SearchView mSearchBarView;
	private View mActionBarView;
	private Handler mHanler = new Handler();

	public static final String FRAG_DISCOVER = "FRAG_DISCOVER";
	public static final String FRAG_MINE = "FRAG_MINE";
	public static final String FRAG_SEARCH = "FRAG_SEARCH";
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
		mSearchTabTv = (TextView) this.findViewById(R.id.tv_home_search);
		mPerasonTabTv = (TextView) this.findViewById(R.id.tv_home_account);
		mCoverImg = (ImageView) findViewById(R.id.img_home_cover);
		mTitleTv = (TextView)findViewById(R.id.tv_head_title);
		mTitleImg = (ImageView)findViewById(R.id.img_head_title);
		mSearchBarView = (SearchView) findViewById(R.id.search_bar);
		mActionBarView = findViewById(R.id.action_bar);
		setTabSelect(0);
		mHanler.postDelayed(new Runnable() {
			@Override
			public void run() {
				reminderTip();
				shareTip();
				checkVersion();
			}
		},100);

	}

	/**
	 * 提示设置闹钟
	 */
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

	/**
	 * 分享给好友
	 */
	private void shareTip() {
		int remindCountDown = PreferenceUtil.getInt("Share_countdown");
		if(remindCountDown <= Constant.SHARE_COUNTDOWN){
			if(remindCountDown == Constant.SHARE_COUNTDOWN){
				new TipDialog.Builder(this).setLayoutId(R.layout.dialog_prompt)
						.setNeedBg(true)
						.setAutoDismiss(false).setOnClickListener(new com.orhanobut.dialogplus.OnClickListener() {
					@Override
					public void onClick(DialogPlus dialog, View view) {
						if(view.getId() == R.id.tv_dialog_enter) {
							dialog.dismiss();
							mHanler.postDelayed(new Runnable() {
								@Override
								public void run() {
									mController = new ShareDialog().show(HomeActivity.this,
											new ShareBean("小柠檬", null, Constant.SHARE_OFFCAIL_URL));
								}
							}, 250);
						}
					}
				}).create().show();
			}
			remindCountDown++;
			PreferenceUtil.putInt("Share_countdown", remindCountDown);
		}
	}

	/**
	 * 检查版本
	 */
	private void checkVersion() {
			final int updateCount = PreferenceUtil.getInt("update_countdown",0);
		if(updateCount < Constant.UPDATE_COUNTDOWN) {
			UmengUpdateAgent.setUpdateAutoPopup(false);
			UmengUpdateAgent.setUpdateOnlyWifi(true);
			UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
				@Override
				public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {

					switch (updateStatus) {
						case 0: // has update
							PreferenceUtil.putInt("update_countdown",updateCount+1);
							UmengUpdateAgent.showUpdateDialog(HomeActivity.this, updateInfo);
							break;
					}
				}
			});
			UmengUpdateAgent.update(this);
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
				mActionBarView.setVisibility(View.VISIBLE);
				mSearchBarView.setVisibility(View.INVISIBLE);
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
				mActionBarView.setVisibility(View.VISIBLE);
				mSearchBarView.setVisibility(View.INVISIBLE);
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
				mActionBarView.setVisibility(View.INVISIBLE);
				mSearchBarView.setVisibility(View.VISIBLE);
				//mTitleImg.setVisibility(View.INVISIBLE);
				//mTitleTv.setVisibility(View.VISIBLE);
				//mTitleTv.setText("搜索");
				mSearchFragment = (SearchFragment) manager
						.findFragmentByTag(FRAG_SEARCH);
				hideTab(transaction);
				if (mSearchFragment == null) {
					mSearchFragment = new SearchFragment();
					transaction.add(R.id.fl_fragment, mSearchFragment, FRAG_SEARCH);
				} else {
					transaction.show(mSearchFragment);
				}
				mSearchTabTv.setSelected(true);
				transaction.commitAllowingStateLoss();
				break;
			case 3:
				mActionBarView.setVisibility(View.VISIBLE);
				mSearchBarView.setVisibility(View.INVISIBLE);
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
		mSearchTabTv.setSelected(false);
		mPerasonTabTv.setSelected(false);
	}

	private void hideTab(FragmentTransaction transaction) {
		if (mDiscoverFragment != null) {
			transaction.hide(mDiscoverFragment);
		}
		if (mMineFragment != null) {
			transaction.hide(mMineFragment);
		}
		if (mSearchFragment != null) {
			transaction.hide(mSearchFragment);
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
			case R.id.tv_home_search:
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
			if(!mSearchBarView.checkIsFocus()) { //检查mSearchView是否还有焦点
				showCancelAppDialog();
			}
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


	public void search(String content){
		mSearchBarView.search(content);
	}

	private UMSocialService mController;
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		/** 使用SSO授权必须添加如下代码 */
		if(mController != null) {
			UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(
					requestCode);
			if (ssoHandler != null) {
				ssoHandler.authorizeCallBack(requestCode, resultCode, data);
			}
		}
	}
}
