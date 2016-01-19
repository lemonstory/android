package com.xiaoningmeng;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.player.SoundManager;
import com.xiaoningmeng.reminder.Reminder;
import com.xiaoningmeng.utils.PreferenceUtil;

public class AlarmActivity extends BaseActivity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alarm);
		int position = PreferenceUtil.getInt("alarmSound");
		SoundManager.getInstance().start(this, position> 9 ? 9 :position, SoundManager.SoundMode.CYCLE);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void finish() {
		SoundManager.getInstance().end();
		super.finish();
		overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_dialog_cancel:
			Reminder.setDelayAlarmReminder(this, 5*60);
			finish();
			break;
		case R.id.tv_dialog_enter:
			finish();
			break;
		default:
			break;
		}
		
	}
	

}
