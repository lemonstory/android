package com.xiaoningmeng.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class Province implements Parcelable {

	private int proSort;
	private String proName;
	private String proRemark;

	public Province() {
	}

	public Province(Parcel in) {
		proSort = in.readInt();
		proName = in.readString();
		proRemark = in.readString();
	}

	public int getProSort() {
		return proSort;
	}

	public void setProSort(int proSort) {
		this.proSort = proSort;
	}

	public String getProName() {
		return proName;
	}

	public void setProName(String proName) {
		this.proName = proName;
	}

	public String getProRemark() {
		return proRemark;
	}

	public void setProRemark(String proRemark) {
		this.proRemark = proRemark;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(proSort);
		out.writeString(proName);
		out.writeString(proRemark);

	}

	public static final Parcelable.Creator<Province> CREATOR = new Creator<Province>() {

		@Override
		public Province createFromParcel(Parcel source) {

			return new Province(source);
		}

		@Override
		public Province[] newArray(int size) {

			return new Province[size];
		}

	};

}
