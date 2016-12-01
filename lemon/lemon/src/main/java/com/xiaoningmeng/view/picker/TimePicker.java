package com.xiaoningmeng.view.picker;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.xiaoningmeng.R;

import java.util.ArrayList;
import java.util.List;

public class TimePicker extends FrameLayout {

	private Context mContext;
	private NumberPicker mZonePicker;
	private NumberPicker mHourPicker;
	private NumberPicker mMinPicker;

	public TimePicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		((LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.layout_time_picker, this, true);
		mZonePicker = (NumberPicker) findViewById(R.id.time_zone);
		mHourPicker = (NumberPicker) findViewById(R.id.time_hour);
		mMinPicker = (NumberPicker) findViewById(R.id.time_min);
		mZonePicker.setMinValue(0);
		mZonePicker.setMaxValue(1);
		List<String> zones = new ArrayList<>();
		zones.add("上午");
		zones.add("下午");
		mZonePicker.setDisplayedValues(zones);
		mZonePicker.setValue(0);
		mHourPicker.setMinValue(1);
		mHourPicker.setMaxValue(12);
		mMinPicker.setMinValue(0);
		mMinPicker.setMaxValue(59);
	}

	public TimePicker(Context context) {
		this(context, null);
	}
	
	public void update(int hour,int minute){
		if(hour > 12){
			mZonePicker.setValue(1);
			mHourPicker.setValue(hour -12);
		}else{
			mZonePicker.setValue(0);
			mHourPicker.setValue(hour);
		}
		mMinPicker.setValue(minute);	
	}
	
	public Time getTime(){
		
		int zone = mZonePicker.getValue();
		int hour = zone == 0 ? mHourPicker.getValue() : mHourPicker.getValue() + 12;
		int minute = mMinPicker.getValue();
		return new Time(hour,minute);

	}
	
	public static class Time{
		
		public int hour;
		public int minute;
		public Time(int hour, int minute) {
			this.hour = hour;
			this.minute = minute;
		}
	}
}
