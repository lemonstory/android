package com.xiaoningmeng.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class City implements Parcelable {

	private int citySort;
	private int proID;
	private String cityName;

	public City(Parcel in) {
		citySort = in.readInt();
		proID = in.readInt();
		cityName = in.readString();
	}

	public City() {

	}

	public int getCitySort() {
		return citySort;
	}

	public void setCitySort(int citySort) {
		this.citySort = citySort;
	}

	public int getProID() {
		return proID;
	}

	public void setProID(int proID) {
		this.proID = proID;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	@Override
	public int describeContents() {

		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {

		out.writeInt(citySort);
		out.writeInt(proID);
		out.writeString(cityName);
	}

	public static final Parcelable.Creator<City> CREATOR = new Creator<City>() {

		@Override
		public City createFromParcel(Parcel source) {

			return new City(source);
		}

		@Override
		public City[] newArray(int size) {

			return new City[size];
		}

	};

}
