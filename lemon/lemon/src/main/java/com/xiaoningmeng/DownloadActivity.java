package com.xiaoningmeng;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.bean.AudioDownLoad;
import com.xiaoningmeng.bean.PlayingStory;
import com.xiaoningmeng.download.DownLoadClientImpl;
import com.xiaoningmeng.download.DownLoadObserver;
import com.xiaoningmeng.download.DownLoadState;
import com.xiaoningmeng.fragment.DownloadFragment;
import com.xiaoningmeng.manager.PlayWaveManager;
import com.xiaoningmeng.player.PlayObserver;
import com.xiaoningmeng.player.PlayerManager;

import java.util.concurrent.atomic.AtomicBoolean;

public class DownloadActivity extends BaseActivity implements
		OnClickListener, DownLoadObserver<AudioDownLoad>, PlayObserver {

	public static final String FRAGMENT_POS = "fragment_pos";
	private TextView mHasDownloadTv;
	private TextView mDownloadingTv;
	private View mHasDownloadDivider;
	private  View mDownloadingDivder;
	private DownloadFragment mDownloadFragment;
	private DownloadFragment mHistoryFragment;
	public static final String FRAG_DOWNLOAD = "FRAG_DOWNLOAD";
	public static final String FRAG_HISTORY = "FRAG_HISTORY";

	private ImageView mWaveImg;
	private AtomicBoolean isNotify = new AtomicBoolean();
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			isNotify.set(false);
			if (mDownloadFragment != null)
				mDownloadFragment.notifyDataChanged();
			if (mHistoryFragment != null)
				mHistoryFragment.notifyDataChanged();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Fresco.initialize(this);
		setContentView(R.layout.activity_download);
		setTitleName("我的下载");
		setRightHeadIcon(R.drawable.ic_player_flag_wave_01);
		mHasDownloadTv = (TextView) findViewById(R.id.tv_download_tab_has);
		mDownloadingTv = (TextView) findViewById(R.id.tv_download_tab_ing);
		mHasDownloadDivider = findViewById(R.id.v_download_tab_has);
		mDownloadingDivder = findViewById(R.id.v_download_tab_ing);
		mWaveImg = (ImageView) findViewById(R.id.img_head_right);
		PlayerManager.getInstance().register(this);
		DownLoadClientImpl.getInstance().registerObserver(this);
		setTabSelect(0);
		//Slidr.attach(this);
	}

	private void setTab(int position) {

		if (position == 0) {
			mHasDownloadTv.setSelected(true);
			mDownloadingTv.setSelected(false);
			mHasDownloadDivider.setVisibility(View.VISIBLE);
			mDownloadingDivder.setVisibility(View.INVISIBLE);
		} else {
			mHasDownloadTv.setSelected(false);
			mDownloadingTv.setSelected(true);
			mHasDownloadDivider.setVisibility(View.INVISIBLE);
			mDownloadingDivder.setVisibility(View.VISIBLE);
		}
	}

	public void setTabSelect(int i) {
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		setTab(i);
		switch (i) {
		case 0:
			mHistoryFragment = (DownloadFragment) manager
					.findFragmentByTag(FRAG_HISTORY);
			hideTab(transaction);
			if (mHistoryFragment == null) {
				mHistoryFragment = new DownloadFragment();
				Bundle bundle = new Bundle();
				bundle.putInt(FRAGMENT_POS, 0);
				mHistoryFragment.setArguments(bundle);
				transaction.add(R.id.fl_fragment, mHistoryFragment,
						FRAG_HISTORY);
			} else {
				/*
				 * transaction.setCustomAnimations(
				 * R.anim.main_translatex0tof100, R.anim.main_translatex100to0);
				 */
				transaction.show(mHistoryFragment);
			}
			transaction.commitAllowingStateLoss();

			break;
		case 1:
			mDownloadFragment = (DownloadFragment) manager
					.findFragmentByTag(FRAG_DOWNLOAD);
			hideTab(transaction);
			/*
			 * transaction.setCustomAnimations( R.anim.main_translatex100to0,
			 * R.anim.main_translatex0tof100);
			 */
			if (mDownloadFragment == null) {
				mDownloadFragment = new DownloadFragment();
				Bundle bundle = new Bundle();
				bundle.putInt(FRAGMENT_POS, 1);
				mDownloadFragment.setArguments(bundle);
				transaction.add(R.id.fl_fragment, mDownloadFragment,
						FRAG_DOWNLOAD);
			} else {
				transaction.show(mDownloadFragment);
			}
			transaction.commitAllowingStateLoss();
			break;
		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.ll_download_tab_has:
			setTabSelect(0);
			break;
		case R.id.ll_download_tab_ing:
			setTabSelect(1);
			break;
		default:
			break;
		}
	}

	@Override
	public void notifyData(final AudioDownLoad t) {
		
				if (t.getStatus() == DownLoadState.DOWN_SUC) {
					if (mDownloadFragment != null)
						mDownloadFragment.notifyDataChanged();
					if (mHistoryFragment != null)
						mHistoryFragment.notifyDataChanged();
				} else if (t.getStatus() == DownLoadState.DOWN_START) {
					if (mDownloadFragment != null)
						mDownloadFragment.notifyDataChanged();
				}
	}

	private void hideTab(FragmentTransaction transaction) {
		if (mDownloadFragment != null) {
			transaction.hide(mDownloadFragment);
		}
		if (mHistoryFragment != null) {
			transaction.hide(mHistoryFragment);
		}
	}

	@Override
	protected void onDestroy() {
		DownLoadClientImpl.getInstance().unregisterObserver(this);
		PlayerManager.getInstance().unRegister(this);
		super.onDestroy();
	}


	@Override
	protected void onResume() {
		PlayWaveManager.getInstance().loadWaveAnim(this, mWaveImg);
		super.onResume();
	}

	@Override
	public void notify(PlayingStory music) {
		PlayWaveManager.getInstance().notify(music);

	}


	@Override
	public void notifyCancel(final AudioDownLoad t) {

			if(t.getStatus() == DownLoadState.DOWN_SUC){
					mHandler.sendEmptyMessage(1);
				}
				if(!isNotify.get()){
					isNotify.set(true);
					mHandler.sendEmptyMessageDelayed(1, 200);
				}
			}
	}
