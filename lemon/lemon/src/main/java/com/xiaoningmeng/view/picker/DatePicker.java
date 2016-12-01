package com.xiaoningmeng.view.picker;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.xiaoningmeng.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DatePicker extends FrameLayout {

	private Context mContext;
	private NumberPicker mDayPicker;
	private NumberPicker mMonthPicker;
	private NumberPicker mYearPicker;
	private Calendar mCalendar;
	private Calendar mToadyCalendar;
	private List<String> mMonthDisplay;
	private boolean isThisYear;
	private boolean isThisMonth;

	public DatePicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		mCalendar = Calendar.getInstance();
		mToadyCalendar = Calendar.getInstance();
		initMonthDisplay();
		((LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.layout_date_picker, this, true);
		mDayPicker = (NumberPicker) findViewById(R.id.date_day);
		mMonthPicker = (NumberPicker) findViewById(R.id.date_month);
		mYearPicker = (NumberPicker) findViewById(R.id.date_year);
		mDayPicker.setMinValue(1);
		mDayPicker.setMaxValue(31);
		mDayPicker.setValue(mCalendar.get(Calendar.DAY_OF_MONTH));
		mDayPicker.setFormatter(NumberPicker.TWO_DIGIT_FORMATTER);

		mMonthPicker.setMinValue(0);
		mMonthPicker.setMaxValue(11);
		mMonthPicker.setDisplayedValues(mMonthDisplay);
		mMonthPicker.setValue(mCalendar.get(Calendar.MONTH));

		mYearPicker.setMinValue(1950);
		mYearPicker.setMaxValue(2100);
		mYearPicker.setValue(mCalendar.get(Calendar.YEAR));

		mMonthPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

			@Override
			public void onValueChange(NumberPicker picker, int oldVal,
					int newVal) {
				if (isThisYear && newVal >= mToadyCalendar.get(Calendar.MONTH)) {
					isThisMonth = true;
					mMonthPicker.setValue(mToadyCalendar.get(Calendar.MONTH));
					mCalendar.set(Calendar.MONTH,
							mToadyCalendar.get(Calendar.MONTH));
				} else {
					isThisMonth = false;
					mCalendar.set(Calendar.MONTH, newVal);
				}
				updateDate();
			}
		});
		mDayPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

			@Override
			public void onValueChange(NumberPicker picker, int oldVal,
					int newVal) {
				if (isThisYear && isThisMonth
						&& newVal >= mToadyCalendar.get(Calendar.DATE)) {
					mCalendar.set(Calendar.DATE,
							mToadyCalendar.get(Calendar.DATE));
					mDayPicker.setValue(mToadyCalendar.get(Calendar.DATE));
				} else {
					mCalendar.set(Calendar.DATE, newVal);
				}
				updateDate();
			}
		});
		mYearPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

			@Override
			public void onValueChange(NumberPicker picker, int oldVal,
					int newVal) {
				if (newVal >= mToadyCalendar.get(Calendar.YEAR)) {
					isThisYear = true;
					mYearPicker.setValue(mToadyCalendar.get(Calendar.YEAR) - 14);
					mCalendar.set(Calendar.YEAR,
							mToadyCalendar.get(Calendar.YEAR) - 14);
				} else {
					isThisYear = false;
					mCalendar.set(Calendar.YEAR, newVal);
				}

				updateDate();
			}
		});

		updateDate();

	}

	private void initMonthDisplay() {
		mMonthDisplay = new ArrayList<String>();
		mMonthDisplay.add("一月");
		mMonthDisplay.add("二月");
		mMonthDisplay.add("三月");
		mMonthDisplay.add("四月");
		mMonthDisplay.add("五月");
		mMonthDisplay.add("六月");
		mMonthDisplay.add("七月");
		mMonthDisplay.add("八月");
		mMonthDisplay.add("九月");
		mMonthDisplay.add("十月");
		mMonthDisplay.add("十一月");
		mMonthDisplay.add("十二月");
	}
	
	public void setDate(String birthDay){
		if(birthDay != null){
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			try {
				Date date = format.parse(birthDay);
				mYearPicker.setValue(date.getYear());
				mMonthPicker.setValue(date.getMonth());
				mDayPicker.setValue(date.getDay());
			} catch (ParseException e) {
				
				e.printStackTrace();
			}
		}
		
	}

	private void updateDate() {

		mDayPicker.setMinValue(mCalendar.getActualMinimum(Calendar.DATE));
		mDayPicker.setMaxValue(mCalendar.getActualMaximum(Calendar.DATE));
		mDayPicker.setValue(mCalendar.get(Calendar.DATE));
		mMonthPicker.setValue(mCalendar.get(Calendar.MONTH));
		mYearPicker.setValue(mCalendar.get(Calendar.YEAR));
	}

	public DatePicker(Context context) {
		this(context, null);
	}

	public String getDate() {
		// yyyy-mm-dd//
		int month = mMonthPicker.getValue() + 1;
		int day = mDayPicker.getValue();
		String date = mYearPicker.getValue() + "-"+ (month>= 10 ? month :"0"+month) + "-" + (day>= 10?day:"0"+day);
		return date;
	}
	
	public String[] getDateArray() {
		String data[] = new String[3];
		int month = mMonthPicker.getValue() + 1;
		int day = mDayPicker.getValue();
		data[0] = mYearPicker.getValue()+"";
		data[1] = month>= 10 ? month+"" :"0"+month;
		data[2] = day>= 10?day+"":"0"+day;
		return data;
	}

	public int getDay() {
		return mCalendar.get(Calendar.DAY_OF_MONTH);
	}

	public int getMonth() {
		return mCalendar.get(Calendar.MONTH);
	}

	public int getYear() {
		return mCalendar.get(Calendar.YEAR);
	}

	public void setCalendar(Calendar calendar) {
		mCalendar = calendar;
		updateDate();
	}

}
