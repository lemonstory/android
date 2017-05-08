package com.xiaoningmeng;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.UpgradeInfo;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.xiaoningmeng.auth.UserAuth;
import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.bean.PlayingStory;
import com.xiaoningmeng.bean.ShareBean;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.event.LogoutEvent;
import com.xiaoningmeng.http.JsonResponse;
import com.xiaoningmeng.http.LHttpRequest;
import com.xiaoningmeng.manager.PlayWaveManager;
import com.xiaoningmeng.player.PlayNotificationManager;
import com.xiaoningmeng.player.PlayObserver;
import com.xiaoningmeng.player.PlayerManager;
import com.xiaoningmeng.utils.PreferenceUtil;
import com.xiaoningmeng.view.ShareDialog;

import de.greenrobot.event.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.xiaoningmeng.http.LHttpRequest.mRetrofit;


public class SettingActivity extends BaseActivity implements OnClickListener,
        PlayObserver {

    private TextView mAlarmTv;
    public static final String ALARM_TIME = "alarm_time";
    private ImageView mCoverImg;
    private TextView mNewVersionName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setTitleName(getString(R.string.page_title_setting));
        mAlarmTv = (TextView) findViewById(R.id.tv_setting_alarm);
        PlayerManager.getInstance().register(this);
        mCoverImg = (ImageView) findViewById(R.id.img_head_right);
        mNewVersionName = (TextView) findViewById(R.id.tv_new_version_name);

        setRightHeadIcon(R.drawable.ic_player_flag_wave_01);
        findViewById(R.id.tv_setting_logout).setVisibility(UserAuth.getInstance().isLogin(this) ? View.VISIBLE : View.INVISIBLE);
        setNewVersionName(mNewVersionName);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PlayWaveManager.getInstance().loadWaveAnim(this, mCoverImg);
        setAlarmTime();
    }

    @Override
    public void onDestroy() {
        PlayerManager.getInstance().unRegister(this);
        super.onDestroy();
    }

    private void setAlarmTime() {
        if (PreferenceUtil.getBoolean("isOpenAlarm", false)) {
            int hour = PreferenceUtil.getInt("alarmHour");
            int minute = PreferenceUtil.getInt("alarmMinute");
            mAlarmTv.setText((hour >= 10 ? hour : "0" + hour) + ":" + (minute >= 10 ? minute : "0" + minute));
        } else {
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
                try {
                    Uri uri = Uri.parse("market://search?q=pname:" + this.getPackageName());
                    Intent it = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(it);
                } catch (Exception e) {
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
                mController = new ShareDialog().show(this, new ShareBean(app_name, app_desc, Constant.SHARE_OFFCAIL_ICON_URL, null, Constant.SHARE_OFFCAIL_URL));
                break;
            case R.id.rl_setting_check:
                checkVersion();
                break;
            default:
                break;
        }

    }

    private void Logout() {

        UserAuth.getInstance().clearLoginUserInfo(SettingActivity.this);
        LHttpRequest.LogoutRequest logoutRequest = mRetrofit.create(LHttpRequest.LogoutRequest.class);
        Call<JsonResponse<String>> call = logoutRequest.getResult();
        call.enqueue(new Callback<JsonResponse<String>>() {

            @Override
            public void onResponse(Call<JsonResponse<String>> call, Response<JsonResponse<String>> response) {

                if (response.isSuccessful() && response.body().isSuccessful()) {
                    PlayerManager.getInstance().pausePlay();
                    PlayNotificationManager.getInstance().cancel();
                    startActivityForNew(new Intent(SettingActivity.this, LoginActivity.class));
                } else {
                    Logger.e(response.toString());
                }
            }

            @Override
            public void onFailure(Call<JsonResponse<String>> call, Throwable t) {

                Logger.e(t.toString());
            }
        });
        EventBus.getDefault().post(new LogoutEvent());
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


    private void checkVersion() {

        Beta.checkUpgrade(true, false);
    }

    private void setNewVersionName(TextView targetView) {

        UpgradeInfo upgradeInfo = Beta.getUpgradeInfo();
        if (null != upgradeInfo) {
            targetView.setVisibility(View.VISIBLE);
            String versionName = upgradeInfo.versionName;
            String content = String.format("软件更新 %s", versionName);
            targetView.setText(content);
        }
    }
}
