package com.xiaoningmeng;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.bean.WeekDay;
import com.xiaoningmeng.reminder.Reminder;
import com.xiaoningmeng.utils.PreferenceUtil;
import com.xiaoningmeng.view.SwitchButton;
import com.xiaoningmeng.view.picker.TimePicker;
import com.xiaoningmeng.view.picker.TimePicker.Time;

import java.io.Serializable;
import java.util.List;

public class AlarmClockActivity extends BaseActivity implements 
		OnClickListener {

	private TimePicker timePicker;
	private TextView mRepatTv;
	private TextView mRingTv;
	private SwitchButton mSwitchButton;
	private int selectRingPos;
	private List<WeekDay> weekDays;
	private String[] soundArray;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setTheme(R.style.PickTheme);
		setContentView(R.layout.activity_alarm_clock);
		setRightHeadIcon(R.drawable.ic_player_flag_wave_01);
		setTitleName("定时提醒");
		setRightHeadText("保存");
		timePicker = (TimePicker) findViewById(R.id.timePicker);
		mRepatTv = (TextView) findViewById(R.id.tv_alarm_repeat);
		mRingTv = (TextView) findViewById(R.id.tv_alarm_ring);
		mSwitchButton = (SwitchButton)findViewById(R.id.sb_alarm_switch);
		initAlarmData();
	}

	private void initAlarmData() {
		int hour = PreferenceUtil.getInt("alarmHour",20);
		int minute = PreferenceUtil.getInt("alarmMinute");
		timePicker.update(hour, minute);
		boolean isOpenAlarm = getIntent().getBooleanExtra("isOpenAlarm", false);
		if(!isOpenAlarm){
		 isOpenAlarm = PreferenceUtil.getBoolean("isOpenAlarm", false);
		}
		mSwitchButton.setChecked(isOpenAlarm);
		weekDays = WeekDay.getWeek();
		selectRingPos = PreferenceUtil.getInt("alarmSound");
		initAlarmRepat();
		initAlarmSound();
	}

	private void initAlarmSound() {
		if(soundArray == null)
			soundArray = getResources().getStringArray(R.array.music_cover_name);
		mRingTv.setText(soundArray[selectRingPos]);
	}

	private void initAlarmRepat() {
		
		StringBuffer stringBuffer = new StringBuffer();
		int count = 0;
		for(int i =0; i < weekDays.size();i++){
			WeekDay weekDay = weekDays.get(i);
			if(weekDay.isCheck()){
				stringBuffer.append(weekDay.getDayStr());
				stringBuffer.append(" ");
				count++;
			}
		}
		if(count == 7){
			mRepatTv.setText("每天");
		}else if(count == 0){
			mRepatTv.setText("永不");
		}else{
			mRepatTv.setText(stringBuffer.toString());
		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.rl_alarm_repeat:
			Intent  i = new Intent(this,AlarmSelectActivity.class);
			i.putExtra("alaramSelect", 0);
			i.putExtra("weekDays", (Serializable)weekDays);
			startActivityForResult(i,0);
			break;
		case R.id.rl_alarm_ring:
			Intent  ii = new Intent(this,AlarmSelectActivity.class);
			ii.putExtra("alaramSelect",1);
			ii.putExtra("ringPosition", selectRingPos);
			startActivityForResult(ii,0);
			break;
		case R.id.tv_head_right:
			WeekDay.saveWeek(weekDays);
			PreferenceUtil.putInt("alarmSound", selectRingPos);
			boolean isOpen = mSwitchButton.isChecked();
			PreferenceUtil.putBoolean("isOpenAlarm",isOpen);
			Time time = timePicker.getTime();
			PreferenceUtil.putInt("alarmHour",time.hour);
			PreferenceUtil.putInt("alarmMinute",time.minute);
			if(isOpen){
				Reminder.setAlarmReminder(this, time.hour, time.minute);
			}else{
				Reminder.cancelAlarmReminder(this);
			}
			finish();
			break;
		default:
			break;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == 3){
			weekDays  = (List<WeekDay>) data.getSerializableExtra("weekDays");
			initAlarmRepat();
		}else if(resultCode == 4){
			selectRingPos = data.getIntExtra("ringPosition", 0);
			initAlarmSound();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
