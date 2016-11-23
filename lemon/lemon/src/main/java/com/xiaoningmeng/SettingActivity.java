package com.xiaoningmeng;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.xiaoningmeng.application.MyApplication;
import com.xiaoningmeng.auth.UserAuth;
import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.bean.PlayingStory;
import com.xiaoningmeng.bean.ShareBean;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.http.JsonCallback;
import com.xiaoningmeng.http.LHttpRequest;
import com.xiaoningmeng.manager.PlayWaveManager;
import com.xiaoningmeng.player.PlayNotificationManager;
import com.xiaoningmeng.player.PlayObserver;
import com.xiaoningmeng.player.PlayerManager;
import com.xiaoningmeng.utils.PreferenceUtil;
import com.xiaoningmeng.view.ShareDialog;
import com.xiaoningmeng.view.dialog.TextDialogLoading;


public class SettingActivity extends BaseActivity implements OnClickListener,
		PlayObserver {

	private TextView mAlarmTv;
	public static final String ALARM_TIME = "alarm_time";
	private ImageView mCoverImg;



	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		setTitleName("设置");
		mAlarmTv = (TextView) findViewById(R.id.tv_setting_alarm);
		PlayerManager.getInstance().register(this);
		mCoverImg = (ImageView) findViewById(R.id.img_head_right);

		setRightHeadIcon(R.drawable.ic_player_flag_wave_01);
		findViewById(R.id.tv_setting_logout).setVisibility(MyApplication.getInstance().isIsLogin() ? View.VISIBLE :View.INVISIBLE);


	}

	private void setAlarmTime() {
		if(PreferenceUtil.getBoolean("isOpenAlarm", false)){
			int hour = PreferenceUtil.getInt("alarmHour");
			int minute = PreferenceUtil.getInt("alarmMinute");
			mAlarmTv.setText((hour>=10?hour:"0"+hour)+":"+(minute>=10 ?minute:"0"+minute));
		}else{
			mAlarmTv.setText("");
		}
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_setting_reminder:
			startActivityForNew(new Intent(this, AlarmClockActivity.class));

			break;
		case R.id.rl_setting_sleep:
			startActivityForNew(new Intent(this, ReminderActivity.class));
			break;
		case R.id.rl_setting_rate:
			try{
			Uri uri = Uri.parse("market://search?q=pname:"+ this.getPackageName());
			Intent it = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(it);
			}catch (Exception e) {
			}
			break;
		case R.id.rl_setting_feedback:

			startActivityForNew(new Intent(this, FeedBackActivity.class));
			break;
		/*case R.id.rl_setting_answer:
			break;*/
		case R.id.tv_setting_logout:
			Logout();
			break;
		case R.id.rl_setting_share:
			String app_name = this.getString(R.string.app_name);
			String app_desc = this.getString(R.string.app_desc);
			mController = new ShareDialog().show(this,new ShareBean(app_name,app_desc,Constant.SHARE_OFFCAIL_ICON_URL,null,Constant.SHARE_OFFCAIL_URL));
			break;
		case R.id.rl_setting_check:
			checkVersion();
			break;
		default:
			break;
		}

	}

	private void Logout() {
		
		TextDialogLoading loading = new TextDialogLoading(this);
		loading.setLoadingTip("正在退出登录");
		LHttpRequest.getInstance().logoutRequest(this,
				new JsonCallback<String>( loading) {

					@Override
					public void onGetDataSuccess(String data) {
						PlayerManager.getInstance().pausePlay();
						PlayNotificationManager.getInstance().cancel();
						UserAuth.getInstance().invinvalidateUserIdentity(
								SettingActivity.this);
						startActivityForNew(new Intent(SettingActivity.this,
								LoginActivity.class));
					}
				});
	}


	@Override
	protected void onResume() {
		PlayWaveManager.getInstance().loadWaveAnim(this, mCoverImg);
		setAlarmTime();
		super.onResume();

	}

	@Override
	public void notify(PlayingStory music) {
		PlayWaveManager.getInstance().notify(music);

	}

	private ShareAction mController;
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onDestroy() {
		PlayerManager.getInstance().unRegister(this);
		super.onDestroy();
	}


	private void checkVersion() {
		UmengUpdateAgent.setUpdateAutoPopup(false);
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
			@Override
			public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {

				switch (updateStatus) {
					case 0: // has update
						UmengUpdateAgent.showUpdateDialog(SettingActivity.this, updateInfo);
						Toast.makeText(SettingActivity.this, getResources().getString(R.string.check_update_ing), Toast.LENGTH_SHORT).show();
						break;
					case 1: // has no update
						Toast.makeText(SettingActivity.this, getResources().getString(R.string.already_new_version), Toast.LENGTH_SHORT).show();
						break;
					case 2: // none wifi
						Toast.makeText(SettingActivity.this, getResources().getString(R.string.no_wifi), Toast.LENGTH_SHORT).show();
						break;
					case 3: // time out
						Toast.makeText(SettingActivity.this, getResources().getString(R.string.connect_time_out), Toast.LENGTH_SHORT).show();
						break;
					default:
						break;
				}
			}
		});
		UmengUpdateAgent.update(SettingActivity.this);
	}
}
