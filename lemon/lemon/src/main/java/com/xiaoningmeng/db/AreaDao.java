package com.xiaoningmeng.db;

import java.util.ArrayList;
import java.util.List;

import com.xiaoningmeng.bean.City;
import com.xiaoningmeng.bean.Province;
import com.xiaoningmeng.bean.Zone;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AreaDao {

	private AssetsDatabaseManager mg;
	private SQLiteDatabase db;
	private static final String DB_NAME = "china_Province_city_zone.db";
	public static final String T_PROVINCE = "T_Province";
	public static final String T_CITY = "T_City";
	public static final String T_ZONE = "T_Zone";

	public AreaDao(Context context) {

		AssetsDatabaseManager.initManager(context);
		mg = AssetsDatabaseManager.getManager();
		db = mg.getDatabase(DB_NAME);
	}

	public List<Province> getAllProvinces() {

		List<Province> provinces = new ArrayList<Province>();
		Cursor cursor = db.rawQuery("select * from " + T_PROVINCE, null);
		while (cursor.moveToNext()) {
			Province province = new Province();
			province.setProName(cursor.getString(cursor
					.getColumnIndex("ProName")));
			province.setProSort(cursor.getInt(cursor.getColumnIndex("ProSort")));
			province.setProRemark(cursor.getString(cursor
					.getColumnIndex("ProRemark")));
			provinces.add(province);
		}
		if (cursor != null) {
			cursor.close();
		}
		return provinces;

	}

	public List<City> getAllCityByProId(int proId) {

		List<City> citys = new ArrayList<City>();
		Cursor cursor = db.rawQuery("select * from " + T_CITY
				+ " where ProID=?", new String[] { proId + "" });
		while (cursor.moveToNext()) {
			City city = new City();
			city.setCityName(cursor.getString(cursor.getColumnIndex("CityName")));
			city.setCitySort(cursor.getInt(cursor.getColumnIndex("CitySort")));
			city.setProID(proId);
			citys.add(city);
		}
		if (cursor != null) {
			cursor.close();
		}
		return citys;

	}

	public List<Zone> getAllZoneByCityId(int cityId) {

		List<Zone> zones = new ArrayList<Zone>();
		Cursor cursor = db.rawQuery("select * from " + T_ZONE
				+ " where CityID=?", new String[] { cityId + "" });
		while (cursor.moveToNext()) {
			Zone zone = new Zone();
			zone.setZoneName(cursor.getString(cursor.getColumnIndex("ZoneName")));
			zone.setZoneID(cursor.getInt(cursor.getColumnIndex("ZoneID")));
			zone.setCityID(cityId);
			zones.add(zone);
		}
		if (cursor != null) {
			cursor.close();
		}
		return zones;
	}

}
