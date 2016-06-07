package com.spshop.stylistpark.entity;

import java.util.List;

public class BounsEntity extends BaseEntity {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 红包Id
	 */
	private String bounsId;
	
	/**
	 * 红包名称
	 */
	private String typeName;
	
	/**
	 * 当前货币
	 */
	private String currency;
	
	/**
	 * 红包金额
	 */
	private String bounsMoney;
	
	/**
	 * 使用限制
	 */
	private String bounsLimit;
	
	/**
	 * 红包分类编号
	 */
	private int statusType;
	
	/**
	 * 红包分类名称
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
	 * 数集总数量
	 */
	private int countTotal;
	
	/**
	 * 传输数据集
	 */
	private List<BounsEntity> mainLists;
	

	public BounsEntity() {
		super();
	}
	
	
	public BounsEntity(int errCode, String errInfo) {
		super(errCode, errInfo);
	}

	
	public String getBounsId() {
		return bounsId;
	}


	public void setBounsId(String bounsId) {
		this.bounsId = bounsId;
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


	public String getBounsMoney() {
		return bounsMoney;
	}


	public void setBounsMoney(String bounsMoney) {
		this.bounsMoney = bounsMoney;
	}


	public String getBounsLimit() {
		return bounsLimit;
	}


	public void setBounsLimit(String bounsLimit) {
		this.bounsLimit = bounsLimit;
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


	public int getCountTotal() {
		return countTotal;
	}


	public void setCountTotal(int countTotal) {
		this.countTotal = countTotal;
	}


	public List<BounsEntity> getMainLists() {
		return mainLists;
	}


	public void setMainLists(List<BounsEntity> mainLists) {
		this.mainLists = mainLists;
	}

}
