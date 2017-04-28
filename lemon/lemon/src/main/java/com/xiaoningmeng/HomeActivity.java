package com.xiaoningmeng;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.orhanobut.dialogplus.DialogPlus;
import com.umeng.onlineconfig.OnlineConfigAgent;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.bean.PlayingStory;
import com.xiaoningmeng.bean.ShareBean;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.fragment.AccountFragment;
import com.xiaoningmeng.fragment.DiscoverFragment;
import com.xiaoningmeng.fragment.MineFragment;
import com.xiaoningmeng.manager.PlayWaveManager;
import com.xiaoningmeng.player.MusicService;
import com.xiaoningmeng.player.PlayObserver;
import com.xiaoningmeng.player.PlayerManager;
import com.xiaoningmeng.utils.PreferenceUtil;
import com.xiaoningmeng.view.SearchView;
import com.xiaoningmeng.view.ShareDialog;
import com.xiaoningmeng.view.dialog.TipDialog;

public class HomeActivity extends BaseActivity implements
        OnClickListener, PlayObserver {

    private Context mContext;
    private DiscoverFragment mDiscoverFragment;
    private MineFragment mMineFragment;
    private AccountFragment mAccountFragment;
    private TextView mDisCoverTabTv;
    private TextView mMineTabTv;
    public String mShopTitle;
    private TextView mPerasonTabTv;
    private ImageView mCoverImg;
    private ImageView mSearchImg;
    public TextView mTitleTv;
    private ImageView mTitleImg;
    public SearchView mSearchBarView;
    private View mActionBarView;
    private Handler mHanler = new Handler();

    public static final String FRAG_DISCOVER = "FRAG_DISCOVER";
    public static final String FRAG_MINE = "FRAG_MINE";
    public static final String FRAG_ACCOUNT = "FRAG_ACCOUNT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mContext = this;
        Fresco.initialize(this);
        setContentView(R.layout.activity_home);
        OnlineConfigAgent.getInstance().updateOnlineConfig(this);
        initView();
        PlayerManager.getInstance().register(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {

        super.onResume();
        if (mDisCoverTabTv.isSelected() || mMineTabTv.isSelected() || mPerasonTabTv.isSelected()) {

            PlayWaveManager.getInstance().loadWaveAnim(this, mCoverImg);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {

        PlayerManager.getInstance().unRegister(this);
        super.onDestroy();
    }

    private void initView() {
        mDiscoverFragment = new DiscoverFragment();
        mDisCoverTabTv = (TextView) this.findViewById(R.id.tv_home_discover);
        mMineTabTv = (TextView) this.findViewById(R.id.tv_home_mine);
        mPerasonTabTv = (TextView) this.findViewById(R.id.tv_home_account);
        mCoverImg = (ImageView) findViewById(R.id.img_home_cover);
        mSearchImg = (ImageView) findViewById(R.id.img_head_search);
        mTitleTv = (TextView) findViewById(R.id.tv_head_title);
        mTitleImg = (ImageView) findViewById(R.id.img_head_title);
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
        }, 100);

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
                        .setEnterText("好")
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
        if (remindCountDown <= Constant.SHARE_COUNTDOWN) {
            if (remindCountDown == Constant.SHARE_COUNTDOWN) {
                new TipDialog.Builder(this)
                        .setLayoutId(R.layout.dialog_prompt)
                        .setNeedBg(true)
                        .setAutoDismiss(false)
                        .setOnClickListener(new com.orhanobut.dialogplus.OnClickListener() {
                            @Override
                            public void onClick(DialogPlus dialog, View view) {
                                if (view.getId() == R.id.tv_dialog_enter) {
                                    dialog.dismiss();
                                    mHanler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            String app_name = HomeActivity.this.getString(R.string.app_name);
                                            String app_desc = HomeActivity.this.getString(R.string.app_desc);
                                            mController = new ShareDialog().show(HomeActivity.this, new ShareBean(app_name, app_desc, Constant.SHARE_OFFCAIL_ICON_URL, null, Constant.SHARE_OFFCAIL_URL));
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
        final int updateCount = PreferenceUtil.getInt("update_countdown", 0);
        if (updateCount < Constant.UPDATE_COUNTDOWN) {
            //TODO:
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
                mSearchImg.setVisibility(View.VISIBLE);
                mCoverImg.setVisibility(View.VISIBLE);
                mCoverImg.setImageResource(R.drawable.ic_player_flag_wave_01);
                PlayWaveManager.getInstance().loadWaveAnim(this, mCoverImg);
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
                mSearchImg.setVisibility(View.VISIBLE);
                mCoverImg.setVisibility(View.VISIBLE);
                mCoverImg.setImageResource(R.drawable.ic_player_flag_wave_01);
                PlayWaveManager.getInstance().loadWaveAnim(this, mCoverImg);
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
                mActionBarView.setVisibility(View.VISIBLE);
                mSearchBarView.setVisibility(View.INVISIBLE);
                mSearchImg.setVisibility(View.VISIBLE);
                mCoverImg.setVisibility(View.VISIBLE);
                mCoverImg.setImageResource(R.drawable.ic_player_flag_wave_01);
                PlayWaveManager.getInstance().loadWaveAnim(this, mCoverImg);
                mTitleImg.setVisibility(View.INVISIBLE);
                mTitleTv.setVisibility(View.VISIBLE);
                mTitleTv.setText("账号");
                mAccountFragment = (AccountFragment) manager.findFragmentByTag(FRAG_ACCOUNT);
                hideTab(transaction);
                if (mAccountFragment == null) {
                    mAccountFragment = new AccountFragment();
                    transaction.add(R.id.fl_fragment, mAccountFragment, FRAG_ACCOUNT);
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
        mPerasonTabTv.setSelected(false);
    }

    private void hideTab(FragmentTransaction transaction) {
        if (mDiscoverFragment != null) {
            transaction.hide(mDiscoverFragment);
        }
        if (mMineFragment != null) {
            transaction.hide(mMineFragment);
        }
        if (mAccountFragment != null) {
            transaction.hide(mAccountFragment);
        }
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
            case R.id.tv_home_account:
                setTabSelect(2);
                break;
            case R.id.img_head_search:
                //new SimpleDialogFragment ().show(getSupportFragmentManager().beginTransaction(), "simpleDialog");
                startActivity(new Intent(this, SearchActivity.class));
                //overridePendingTransition(R.anim.search_translatey100to0, R.anim.search_translatey0tof100);
                break;
            default:
                break;
        }
    }

    //TODO:第一次运行正常,重复点击back键dialog会自动消失.原因待查
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            if (!mSearchBarView.checkIsFocus()) { //检查mSearchView是否还有焦点
//                showCancelAppDialog();
//            }
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    //显示退出应用的dialog
    private void showCancelAppDialog() {
        TipDialog tipDialog = new TipDialog.Builder(this)
                .setHasBtn(true)
                .setTipText("确定退出小柠檬？")
                .setShieldActionUp(true)
                .setLayoutId(R.layout.dialog_cancel_tip)
                .setAutoDismiss(false)
                .setOnClickListener(
                        new com.orhanobut.dialogplus.OnClickListener() {

                            @Override
                            public void onClick(DialogPlus dialog, View view) {

                                switch (view.getId()) {

                                    case R.id.tv_dialog_enter:
                                        dialog.dismiss();
                                        MusicService.stopService(HomeActivity.this);
                                        oldFinish();

                                        break;

                                    case R.id.tv_dialog_min:
                                        dialog.dismiss();
                                        oldFinish();
                                        break;

                                    default:
                                        dialog.dismiss();
                                        break;
                                }

                            }
                        }).create();
        tipDialog.show();
    }

    @Override
    public void notify(PlayingStory music) {

        PlayWaveManager.getInstance().notify(music);
    }


    public void search(String content) {
        mSearchBarView.search(content);
    }

    private ShareAction mController;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }
}
