package com.xiaoningmeng;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mobads.SplashAd;
import com.baidu.mobads.SplashAdListener;
import com.xiaoningmeng.auth.UserAuth;
import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.download.DownLoadClientImpl;
import com.xiaoningmeng.download.DownloadNotificationManager;
import com.xiaoningmeng.manager.UploadManager;
import com.xiaoningmeng.player.PlayerManager;
import com.xiaoningmeng.utils.PreferenceUtil;

public class SplashActivity extends BaseActivity {

    private Handler mHandler = new Handler();
    public static final int LOGIN_TIME = 1600;
    private TextView mAdCountDownTv;
    private int countDown = 5;
    private boolean isLoadAd = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mAdCountDownTv = (TextView) findViewById(R.id.tv_ad_countdown);
        DownLoadClientImpl.getInstance();// 初始化下载器
        PlayerManager.getInstance(); // 初始化音乐播放器
        DownloadNotificationManager.getInstance();//初始化下载通知栏
        UploadManager.getInstance().uploadRecord();

        int loadCountDown = PreferenceUtil.getInt("load_countdown");
        if (isLoadAd && loadCountDown > 3) {
            loadAd();
        } else {
            PreferenceUtil.putInt("load_countdown", loadCountDown + 1);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    jumpHomeActivity();
                }
            }, LOGIN_TIME);
        }
    }

    private void loadAd() {
        RelativeLayout adsParent = (RelativeLayout) this.findViewById(R.id.adsRl);
        SplashAdListener listener = new SplashAdListener() {
            @Override
            public void onAdDismissed() {
                jumpWhenCanClick();// 跳转至您的应用主界面
            }

            @Override
            public void onAdFailed(String arg0) {
                jumpHomeActivity();
            }

            @Override
            public void onAdPresent() {
            }

            @Override
            public void onAdClick() {

            }
        };
        new SplashAd(this, adsParent, listener, Constant.BAIDU_SPLASH_AD_ID, true);
        mAdCountDownTv.setVisibility(View.VISIBLE);
        mHandler.post(adCountDownRunnable);
    }

    /**
     * 广告倒计时
     */
    Runnable adCountDownRunnable = new Runnable() {
        @Override
        public void run() {
            if (countDown >= 0) {
                mAdCountDownTv.setText("倒计时:" + countDown + "s");
                mHandler.postDelayed(this, 1000);
                countDown--;
            } else {
                mHandler.removeCallbacks(this);
                mAdCountDownTv.setVisibility(View.INVISIBLE);
            }
        }
    };

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacks(adCountDownRunnable);
        super.onDestroy();
    }

    /**
     * 当设置开屏可点击时，需要等待跳转页面关闭后，再切换至您的主窗口。故此时需要增加waitingOnRestart判断。
     * 另外，点击开屏还需要在onRestart中调用jumpWhenCanClick接口。
     */
    public boolean waitingOnRestart = false;

    private void jumpWhenCanClick() {
        if (this.hasWindowFocus() || waitingOnRestart) {
            if (UserAuth.getInstance().isFirst()) {
                jumpGuideActivity();
            } else {
                jumpHomeActivity();
            }
        } else {
            waitingOnRestart = true;
        }
    }

    private void jumpHomeActivity() {
        Intent i = new Intent(SplashActivity.this, HomeActivity.class);
        startActivity(i);
        oldFinish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void jumpGuideActivity() {
        Intent i = new Intent(SplashActivity.this, GuideActivity.class);
        startActivity(i);
        oldFinish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (waitingOnRestart) {
            jumpWhenCanClick();
        }
    }

    @Override
    public void onBackPressed() {
        oldFinish();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            setStatusBarHide();
        }
    }

}
