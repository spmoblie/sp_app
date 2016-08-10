package com.spshop.stylistpark.entity;

import java.util.List;


public class LogisticsEntity extends BaseEntity {
	
	private static final long serialVersionUID = 1L;

	/**
	 * 物流信息内容
	 */
	private String msgContent;
	
	/**
	 * 物流信息时间
	 */
	private String msgTime;
	
	/**
	 * 集合打包传输
	 */
	private List<LogisticsEntity> mainLists;
	

	public LogisticsEntity() {
		super();
	}


	public LogisticsEntity(int errCode, String errInfo) {
		super(errCode, errInfo);
	}

	@Override
	public String getEntityId() {
		return "";
	}

	public String getMsgContent() {
		return msgContent;
	}


	public void setMsgContent(String msgContent) {
		this.msgContent = msgContent;
	}


	public String getMsgTime() {
		return msgTime;
	}


	public void setMsgTime(String msgTime) {
		this.msgTime = msgTime;
	}


	public List<LogisticsEntity> getMainLists() {
		return mainLists;
	}


	public void setMainLists(List<LogisticsEntity> mainLists) {
		this.mainLists = mainLists;
	}
	
}
