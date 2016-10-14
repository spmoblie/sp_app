package com.spshop.stylistpark.entity;

public class QQUserInfoEntity {
	
	private int errcode; 
	private String errmsg; 
	private String nickname	; //用户昵称
	private String gender; //用户性别(1为男性，2为女性)
	private String avatar; //用户头像，用户没有头像时该项为空
    
    
    public QQUserInfoEntity(int errcode, String errmsg) {
		super();
		this.errcode = errcode;
		this.errmsg = errmsg;
	}


	public QQUserInfoEntity(int errcode, String errmsg, String nickname,
			String gender, String avatar) {
		super();
		this.errcode = errcode;
		this.errmsg = errmsg;
		this.nickname = nickname;
		this.gender = gender;
		this.avatar = avatar;
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


	public String getAvatar() {
		return avatar;
	}


	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

}
