package com.xiaoningmeng.bean;

import java.util.List;

public class HomeInfo {
	
	private String uid;
	private String nickname;
	private String avatartime;
	private String province;
	private String city;
	private String area;
	private String phonenumber;
	private String gender;
	private String age;
	private List<ListenerAlbum> listenalbumlist;
	
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
	public String getPhonenumber() {
		return phonenumber;
	}
	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public List<ListenerAlbum> getListenalbumlist() {
		return listenalbumlist;
	}
	public void setListenalbumlist(List<ListenerAlbum> listenalbumlist) {
		this.listenalbumlist = listenalbumlist;
	}
	public String getAvatartime() {
		return avatartime;
	}
	public void setAvatartime(String avatartime) {
		this.avatartime = avatartime;
	}
	
	
	
}
