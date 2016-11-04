package com.spshop.stylistpark.entity;

import java.util.List;

public class MemberEntity extends BaseEntity {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 会员Id
	 */
	private String userId;
	
	/**
	 * 会员头像
	 */
	private String avatar;

	/**
	 * 会员性别
	 */
	private String gender;

	/**
	 * 会员昵称
	 */
	private String memberNick;

	/**
	 * 会员等级
	 */
	private int memberRank;

	/**
	 * 会员来源
	 */
	private String memberType;

	/**
	 * 成交订单金额
	 */
	private String orderMoney;
	
	/**
	 * 最近登录时间
	 */
	private String lastLogin;
	
	/**
	 * 数集总数量
	 */
	private int countTotal;
	
	/**
	 * 传输数据集
	 */
	private List<MemberEntity> mainLists;
	

	public MemberEntity() {
		super();
	}
	
	
	public MemberEntity(int errCode, String errInfo) {
		super(errCode, errInfo);
	}

	@Override
	public String getEntityId() {
		return userId;
	}

	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
	}


	public String getAvatar() {
		return avatar;
	}


	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}


	public String getMemberNick() {
		return memberNick;
	}


	public void setMemberNick(String memberNick) {
		this.memberNick = memberNick;
	}


	public String getGender() {
		return gender;
	}


	public void setGender(String gender) {
		this.gender = gender;
	}


	public int getMemberRank() {
		return memberRank;
	}


	public void setMemberRank(int memberRank) {
		this.memberRank = memberRank;
	}


	public String getMemberType() {
		return memberType;
	}


	public void setMemberType(String memberType) {
		this.memberType = memberType;
	}


	public String getOrderMoney() {
		return orderMoney;
	}


	public void setOrderMoney(String orderMoney) {
		this.orderMoney = orderMoney;
	}


	public String getLastLogin() {
		return lastLogin;
	}


	public void setLastLogin(String lastLogin) {
		this.lastLogin = lastLogin;
	}


	public int getCountTotal() {
		return countTotal;
	}


	public void setCountTotal(int countTotal) {
		this.countTotal = countTotal;
	}


	public List<MemberEntity> getMainLists() {
		return mainLists;
	}


	public void setMainLists(List<MemberEntity> mainLists) {
		this.mainLists = mainLists;
	}

}
