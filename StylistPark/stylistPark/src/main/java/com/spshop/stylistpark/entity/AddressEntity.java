package com.spshop.stylistpark.entity;

import java.util.List;


public class AddressEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;
	
	private int addressId; //收货地址Id
	private int defaultId; //默认地址Id
	private String name; //联系人
	private String phone; //手机
	private String email; //邮箱
	private int countryId; //国入库Id
	private String country; //国
	private int proviceId; //省入库Id
	private String province; //省
	private int cityId; //市入库Id
	private String city; //市
	private int districtId; //区入库Id
	private String district; //区 
	private String editAdd; //编辑地址
	private String address; //详细地址
	private List<AddressEntity> mainLists; //地址列表

	
	public AddressEntity() {
		super();
	}


	public AddressEntity(int errCode, String errInfo) {
		super(errCode, errInfo);
	}


	public int getAddressId() {
		return addressId;
	}


	public void setAddressId(int addressId) {
		this.addressId = addressId;
	}


	public int getDefaultId() {
		return defaultId;
	}


	public void setDefaultId(int defaultId) {
		this.defaultId = defaultId;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getPhone() {
		return phone;
	}


	public void setPhone(String phone) {
		this.phone = phone;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public int getCountryId() {
		return countryId;
	}


	public void setCountryId(int countryId) {
		this.countryId = countryId;
	}
	

	public String getCountry() {
		return country;
	}


	public void setCountry(String country) {
		this.country = country;
	}


	public int getProviceId() {
		return proviceId;
	}


	public void setProviceId(int proviceId) {
		this.proviceId = proviceId;
	}


	public String getProvince() {
		return province;
	}


	public void setProvince(String province) {
		this.province = province;
	}


	public int getCityId() {
		return cityId;
	}


	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	
	public String getCity() {
		return city;
	}


	public void setCity(String city) {
		this.city = city;
	}


	public int getDistrictId() {
		return districtId;
	}


	public void setDistrictId(int districtId) {
		this.districtId = districtId;
	}
	

	public String getDistrict() {
		return district;
	}


	public void setDistrict(String district) {
		this.district = district;
	}


	public String getEditAdd() {
		return editAdd;
	}


	public void setEditAdd(String editAdd) {
		this.editAdd = editAdd;
	}


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	public List<AddressEntity> getMainLists() {
		return mainLists;
	}


	public void setMainLists(List<AddressEntity> mainLists) {
		this.mainLists = mainLists;
	}
	
}
