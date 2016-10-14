package com.spshop.stylistpark.entity;

public class WXUserInfoEntity {
	
	private int errcode;//":40003,"
	private String  errmsg;//":"invalid openid"
	private String openid;//	普通用户的标识，对当前开发者帐号唯一
	private String nickname	;//普通用户昵称
	private String gender	;//普通用户性别，1为男性，2为女性
	private String province;//	普通用户个人资料填写的省份
	private String city;//	普通用户个人资料填写的城市
	private String country;//	国家，如中国为CN
	private String avatar;//	用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像），用户没有头像时该项为空
	private String privilege;//	用户特权信息，json数组，如微信沃卡用户为（chinaunicom）
    private String unionid;//	用户统一标识。针对一个微信开放平台帐号下的应用，同一用户的unionid是唯一的。	
	private String language;
    
    
    
    public WXUserInfoEntity(int errcode, String errmsg) {
		super();
		this.errcode = errcode;
		this.errmsg = errmsg;
	}
	public WXUserInfoEntity( String openid,
			String nickname, String gender, String province, String city,
			String country, String avatar, String privilege, String unionid, String language) {
		super();
		
		this.openid = openid;
		this.nickname = nickname;
		this.gender = gender;
		this.province = province;
		this.city = city;
		this.country = country;
		this.avatar = avatar;
		this.privilege = privilege;
		this.unionid = unionid;
		this.language = language;
	}
	
	
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public int getErrcode() {
		return errcode;
	}
	public void setErrcode(int errcode) {
		this.errcode = errcode;
	}
	public String getErrmsg() {
		return errmsg;
	}
	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
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
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public String getPrivilege() {
		return privilege;
	}
	public void setPrivilege(String privilege) {
		this.privilege = privilege;
	}
	public String getUnionid() {
		return unionid;
	}
	public void setUnionid(String unionid) {
		this.unionid = unionid;
	}
    
    

}
