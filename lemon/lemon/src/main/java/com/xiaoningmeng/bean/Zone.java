package com.xiaoningmeng.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class Zone implements Parcelable {

	private int zoneID;
	private int cityID;
	private String zoneName;

	public Zone() {

	}

	public Zone(Parcel in) {
		zoneID = in.readInt();
		cityID = in.readInt();
		zoneName = in.readString();
	}

	public int getZoneID() {
		return zoneID;
	}

	public void setZoneID(int zoneID) {
		this.zoneID = zoneID;
	}

	public String getZoneName() {
		return zoneName;
	}

	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}

	public int getCityID() {
		return cityID;
	}

	public void setCityID(int cityID) {
		this.cityID = cityID;
	}

	@Override
	public int describeContents() {

		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(cityID);
		out.writeInt(zoneID);
		out.writeString(zoneName);

	}

	public static final Parcelable.Creator<Zone> CREATOR = new Creator<Zone>() {

		@Override
		public Zone createFromParcel(Parcel source) {

			return new Zone(source);
		}

		@Override
		public Zone[] newArray(int size) {

			return new Zone[size];
		}

	};

}
