package com.spshop.stylistpark.entity;

public class PaymentEntity extends BaseEntity {
	
	private static final long serialVersionUID = 1L;

	/**
	 * 支付交易会话Id
	 */
	private String prepayid;
	
	/**
	 * 随机字符串
	 */
	private String noncestr;
	
	/**
	 * 时间戳
	 */
	private String timestamp;
	
	/**
	 * 签名
	 */
	private String sign;
	
	/**
	 * 交易状态
	 */
	private String trade_state;
	
	/**
	 * 交易状态描述
	 */
	private String trade_state_desc;

	/**
	 * 签名支付信息
	 */
	private String content;

	
	public PaymentEntity() {

	}

	public PaymentEntity(int errCode, String errInfo) {
		super(errCode, errInfo);
	}


	public PaymentEntity(int errCode, String errInfo, String prepayid,
			String noncestr, String timestamp, String sign, String trade_state,
			String trade_state_desc) {
		super(errCode, errInfo);
		this.prepayid = prepayid;
		this.noncestr = noncestr;
		this.timestamp = timestamp;
		this.sign = sign;
		this.trade_state = trade_state;
		this.trade_state_desc = trade_state_desc;
	}

	@Override
	public String getEntityId() {
		return prepayid;
	}

	public String getPrepayid() {
		return prepayid;
	}


	public void setPrepayid(String prepayid) {
		this.prepayid = prepayid;
	}


	public String getNoncestr() {
		return noncestr;
	}


	public void setNoncestr(String noncestr) {
		this.noncestr = noncestr;
	}


	public String getTimestamp() {
		return timestamp;
	}


	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}


	public String getSign() {
		return sign;
	}


	public void setSign(String sign) {
		this.sign = sign;
	}


	public String getTrade_state() {
		return trade_state;
	}


	public void setTrade_state(String trade_state) {
		this.trade_state = trade_state;
	}


	public String getTrade_state_desc() {
		return trade_state_desc;
	}


	public void setTrade_state_desc(String trade_state_desc) {
		this.trade_state_desc = trade_state_desc;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
