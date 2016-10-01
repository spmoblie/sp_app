package com.spshop.stylistpark.entity;

import java.util.List;

public class CouponEntity extends BaseEntity {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 优惠券Id
	 */
	private String couponId;
	
	/**
	 * 优惠券名称
	 */
	private String typeName;
	
	/**
	 * 当前货币
	 */
	private String currency;
	
	/**
	 * 优惠券金额
	 */
	private String couponMoney;
	
	/**
	 * 使用限制
	 */
	private String couponLimit;
	
	/**
	 * 优惠券分类编号
	 */
	private int statusType;
	
	/**
	 * 优惠券分类名称
	 */
	private String statusName;
	
	/**
	 * 起始日期
	 */
	private String startDate;
	
	/**
	 * 结束日期
	 */
	private String endDate;
	
	/**
	 * 传输数据集
	 */
	private List<CouponEntity> mainLists;
	

	public CouponEntity() {
		super();
	}
	
	
	public CouponEntity(int errCode, String errInfo) {
		super(errCode, errInfo);
	}

	@Override
	public String getEntityId() {
		return couponId;
	}
	
	public String getCouponId() {
		return couponId;
	}


	public void setCouponId(String couponId) {
		this.couponId = couponId;
	}


	public String getTypeName() {
		return typeName;
	}


	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}


	public String getCurrency() {
		return currency;
	}


	public void setCurrency(String currency) {
		this.currency = currency;
	}


	public String getCouponMoney() {
		return couponMoney;
	}


	public void setCouponMoney(String couponMoney) {
		this.couponMoney = couponMoney;
	}


	public String getCouponLimit() {
		return couponLimit;
	}


	public void setCouponLimit(String couponLimit) {
		this.couponLimit = couponLimit;
	}


	public int getStatusType() {
		return statusType;
	}


	public void setStatusType(int statusType) {
		this.statusType = statusType;
	}


	public String getStatusName() {
		return statusName;
	}


	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}


	public String getStartDate() {
		return startDate;
	}


	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}


	public String getEndDate() {
		return endDate;
	}


	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}


	public List<CouponEntity> getMainLists() {
		return mainLists;
	}


	public void setMainLists(List<CouponEntity> mainLists) {
		this.mainLists = mainLists;
	}

}
