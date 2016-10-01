package com.spshop.stylistpark.entity;

import java.util.List;

public class BalanceDetailEntity extends BaseEntity {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 明细总数目
	 */
	private String logId;

	/**
	 * 明细总数目
	 */
	private int countTotal;

	/**
	 * 提现状态
	 */
	private int status;
	
	/**
	 * 提现状态描述
	 */
	private String statusHint;
	
	/**
	 * 账户余额
	 */
	private double amount;
	
	/**
	 * 交易描述
	 */
	private String changeDesc;
	
	/**
	 * 交易时间
	 */
	private String changeTime;
	
	/**
	 * 交易类型
	 */
	private String type;
	
	/**
	 * 当前货币
	 */
	private String currency;
	
	/**
	 * 交易金额
	 */
	private String changeMoney;
	
	/**
	 * 集合打包传输
	 */
	private List<BalanceDetailEntity> mainLists;

	
	public BalanceDetailEntity() {
		super();
	}


	public BalanceDetailEntity(int errCode, String errInfo) {
		super(errCode, errInfo);
	}

	@Override
	public String getEntityId() {
		return logId;
	}

	public String getLogId() {
		return logId;
	}

	public void setLogId(String logId) {
		this.logId = logId;
	}

	public int getCountTotal() {
		return countTotal;
	}


	public void setCountTotal(int countTotal) {
		this.countTotal = countTotal;
	}


	public int getStatus() {
		return status;
	}


	public void setStatus(int status) {
		this.status = status;
	}


	public String getStatusHint() {
		return statusHint;
	}


	public void setStatusHint(String statusHint) {
		this.statusHint = statusHint;
	}


	public double getAmount() {
		return amount;
	}


	public void setAmount(double amount) {
		this.amount = amount;
	}


	public String getChangeDesc() {
		return changeDesc;
	}


	public void setChangeDesc(String changeDesc) {
		this.changeDesc = changeDesc;
	}


	public String getChangeTime() {
		return changeTime;
	}


	public void setChangeTime(String changeTime) {
		this.changeTime = changeTime;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getCurrency() {
		return currency;
	}


	public void setCurrency(String currency) {
		this.currency = currency;
	}


	public String getChangeMoney() {
		return changeMoney;
	}


	public void setChangeMoney(String changeMoney) {
		this.changeMoney = changeMoney;
	}


	public List<BalanceDetailEntity> getMainLists() {
		return mainLists;
	}


	public void setMainLists(List<BalanceDetailEntity> mainLists) {
		this.mainLists = mainLists;
	}
	
}
