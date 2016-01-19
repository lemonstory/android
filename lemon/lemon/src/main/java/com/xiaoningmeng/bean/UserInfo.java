package com.xiaoningmeng.bean;

import org.litepal.crud.DataSupport;

public class UserInfo extends DataSupport {

	private String uid;
	private String nickname;
	private String phonenumber;
	private String province;
	private String city;
	private String area;
	protected Address addressinfo;
	private String age;
	private String gender;
	private String avatartime;
	protected String listennum;
	private String uc_callback;

	public UserInfo() {

	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getPhonenumber() {
		return phonenumber;
	}

	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getAvatartime() {
		return avatartime;
	}

	public void setAvatartime(String avatartime) {
		this.avatartime = avatartime;
	}

	public Address getAddressinfo() {
		return addressinfo;
	}

	public void setAddressinfo(Address addressinfo) {
		this.addressinfo = addressinfo;
	}

	public String getListennum() {
		return listennum;
	}

	public void setListennum(String listennum) {
		this.listennum = listennum;
	}

	public void setUcCallback(String uc_callback) {this.uc_callback = uc_callback;}

	public String getUcCallback() { return  uc_callback;}


}
