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
	private String headImg;
	
	/**
	 * 会员名称
	 */
	private String userName;
	
	/**
	 * 会员等级
	 */
	private int memberRank;
	
	/**
	 * 成交订单数
	 */
	private String orderCount;
	
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


	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
	}


	public String getHeadImg() {
		return headImg;
	}


	public void setHeadImg(String headImg) {
		this.headImg = headImg;
	}


	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	public int getMemberRank() {
		return memberRank;
	}


	public void setMemberRank(int memberRank) {
		this.memberRank = memberRank;
	}


	public String getOrderCount() {
		return orderCount;
	}


	public void setOrderCount(String orderCount) {
		this.orderCount = orderCount;
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
