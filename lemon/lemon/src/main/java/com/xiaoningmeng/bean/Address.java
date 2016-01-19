package com.xiaoningmeng.bean;

import java.io.Serializable;

public class Address implements Serializable{

	private static final long serialVersionUID = 1L;
	private String name;
	private String phonenumber;
	private String province;
	private String city;
	private String area;
	private String address;
	private String ecode;
	private String id;
	
	
	public Address(String id,String name, String phonenumber, String province,
			String city, String area, String address, String ecode) {
		this.id = id;
		this.name = name;
		this.phonenumber = phonenumber;
		this.province = province;
		this.city = city;
		this.area = area;
		this.address = address;
		this.ecode = ecode;
	}
	
	
	public Address() {
	}


	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getEcode() {
		return ecode;
	}
	public void setEcode(String ecode) {
		this.ecode = ecode;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	
}
