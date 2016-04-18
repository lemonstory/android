package com.xiaoningmeng;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;

import com.baoyz.swipemenu.xlistview.XListView;
import com.xiaoningmeng.adapter.ReminderAdapter;
import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.bean.PlayingStory;
import com.xiaoningmeng.manager.PlayWaveManager;
import com.xiaoningmeng.player.PlayObserver;
import com.xiaoningmeng.player.PlayerManager;
import com.xiaoningmeng.reminder.Reminder;
import com.xiaoningmeng.utils.PreferenceUtil;

public class ReminderActivity extends BaseActivity implements OnClickListener,
		PlayObserver {

	private XListView mListView;
	public static final String REMINDER_TIME = "reminder_time";
	public static final String REMINDER_INT = "reminder_int";
	private int[] reminderTimeInt;
	private ReminderAdapter mAdapter;
	private ImageView mCoverImg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reminder);
		setTitleName("睡眠定时");
		mListView = (XListView) findViewById(R.id.lv_home_discover);
		mCoverImg = (ImageView) findViewById(R.id.img_head_right);
		setRightHeadIcon(R.drawable.play_flag_wave_01);
		mListView.setPullLoadEnable(false);
		mListView.setPullRefreshEnable(false);
		reminderTimeInt = getResources().getIntArray(R.array.reminder_int);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				int index = position - 1;
				if (index < 0) {
					index = 0;
				}
				int reminderMode = reminderTimeInt[index];
				PreferenceUtil.putLong(REMINDER_TIME,
						System.currentTimeMillis());
				PreferenceUtil.putInt(REMINDER_INT, reminderMode);
				if (reminderMode > 0) {
					Reminder.setReminder(ReminderActivity.this,
							reminderMode * 60);
				} else if (reminderMode < 0) {
					PlayerManager.getInstance().setReminderSong(
							Math.abs(reminderMode));
				}
				mAdapter.setReminderMode(index);
				mAdapter.notifyDataSetChanged();
			}
		});
		int reminder = recoveryReminder();
		mAdapter = new ReminderAdapter(this, reminder);
		mListView.setAdapter(mAdapter);
		PlayerManager.getInstance().register(this);
		//Slidr.attach(this);
	}

	private int recoveryReminder() {
		int reminderInt = PreferenceUtil.getInt(REMINDER_INT);
		long reminderTime = PreferenceUtil.getLong(REMINDER_TIME);
		if (reminderInt > 0
				&& reminderTime + reminderInt * 60 * 1000 < System
						.currentTimeMillis()) {
			return 0;
		} else {
			for (int i = 0; i < reminderTimeInt.length; i++) {
				if (reminderInt == reminderTimeInt[i]) {
					return i;
				}
			}
		}
		return 0;
	}

	@Override
	public void onClick(View v) {

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
