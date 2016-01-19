package com.xiaoningmeng.view.picker;

import java.util.ArrayList;
import java.util.List;

import com.umeng.socialize.utils.Log;
import com.xiaoningmeng.R;
import com.xiaoningmeng.bean.City;
import com.xiaoningmeng.bean.Province;
import com.xiaoningmeng.bean.Zone;
import com.xiaoningmeng.db.AreaDao;
import com.xiaoningmeng.view.picker.NumberPicker.OnValueChangeListener;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

public class AreaPicker extends FrameLayout {

	private Context mContext;
	private NumberPicker mProvincePicker;
	private NumberPicker mCityPicker;
	private NumberPicker mAreaPicker;
	private List<Province> provinces;
	private List<City> citys;
	private List<Zone> areas;
	private List<String> cityNames = new ArrayList<String>();
	private List<String> zonesNames = new ArrayList<String>();
	private AreaDao areaDao;

	public AreaPicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		((LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.layout_area_picker, this, true);
		mProvincePicker = (NumberPicker) findViewById(R.id.province);
		mCityPicker = (NumberPicker) findViewById(R.id.city);
		mAreaPicker = (NumberPicker) findViewById(R.id.zone);

		areaDao = new AreaDao(getContext());
		provinces = areaDao.getAllProvinces();
		int size = provinces.size();
		List<String> proNames = new ArrayList<String>(size);
		for (int i = 0; i < size; i++) {
			proNames.add(provinces.get(i).getProName());
		}
		mProvincePicker.setMinValue(0);
		mProvincePicker.setMaxValue(size - 1);
		mProvincePicker.setValue(0);
		mProvincePicker.setDisplayedValues(proNames);
		updateCity(provinces.get(0).getProSort());

		mAreaPicker.setVisibility(View.VISIBLE);
		updateArea(citys.get(0).getCitySort());

		mProvincePicker.setOnValueChangedListener(new OnValueChangeListener() {

			@Override
			public void onValueChange(NumberPicker picker, int oldVal,
					int newVal) {
				updateCity(provinces.get(newVal).getProSort());
			}
		});

		mCityPicker.setOnValueChangedListener(new OnValueChangeListener() {

			@Override
			public void onValueChange(NumberPicker picker, int oldVal,
					int newVal) {
				updateArea(citys.get(newVal).getCitySort());
			}
		});

		mAreaPicker.setOnValueChangedListener(new OnValueChangeListener() {

			@Override
			public void onValueChange(NumberPicker picker, int oldVal,
					int newVal) {
			}
		});

	}

	private void updateCity(int proId) {

		cityNames.clear();
		zonesNames.clear();
		citys = areaDao.getAllCityByProId(proId);
		int size = citys.size();
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				cityNames.add(citys.get(i).getCityName());
			}
			int citySort = citys.get(0).getCitySort();
			updateArea(citySort);
			mCityPicker.setMinValue(0);
			mCityPicker.setMaxValue(size - 1);
			mCityPicker.setValue(0);
		} else {
			updateArea(-1);
			cityNames.add("");
			mCityPicker.setMinValue(0);
			mCityPicker.setMaxValue(0);
			mCityPicker.setValue(0);

		}
		mCityPicker.setDisplayedValues(cityNames);
	}

	private void updateArea(int cityId) {
		if(areas != null)
			areas.clear();
		if(zonesNames != null)
			zonesNames.clear();
		int size;
		if (cityId != -1) {
			areas = areaDao.getAllZoneByCityId(cityId);
			size = areas.size();
		} else {
			size = 0;
		}
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				zonesNames.add(areas.get(i).getZoneName());
			}
			mAreaPicker.setMinValue(0);
			mAreaPicker.setValue(0);
			mAreaPicker.setMaxValue(size - 1);

		} else {
			zonesNames.add("");
			mAreaPicker.setMinValue(0);
			mAreaPicker.setValue(0);
			mAreaPicker.setMaxValue(0);
		}
		mAreaPicker.setDisplayedValues(zonesNames);

	}

	public AreaPicker(Context context) {
		this(context, null);
	}

	public String[] getArea() {

		String[] areas = new String[3];
		areas[0] = provinces.get(mProvincePicker.getValue()).getProName();
		if (cityNames.size() > 0
				&& (cityNames.size() > 1 || zonesNames.size() == 0)) {
			areas[1] = citys.size() > 0 ? citys.get(mCityPicker.getValue())
					.getCityName() : " ";
		} else {
			areas[1] = zonesNames.get(mCityPicker.getValue());
		}
		areas[2] = this.areas.size() > 0 ? zonesNames.get(mAreaPicker
				.getValue()) : " ";
		return areas;
	}

	public String getAreaName() {

		String[] areas = getArea();
		String str = "";
		for (String area : areas) {
			if (area != null)
				str += " " + area;
		}
		return str;
	}
}
