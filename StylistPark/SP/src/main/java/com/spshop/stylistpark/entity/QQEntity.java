package com.spshop.stylistpark.entity;

public class QQEntity {
	
	private int errcode; //0
	private String errmsg; //"sucess"
	private String access_token; //接口调用凭证
	private String expires_in; //access_token接口调用凭证超时时间，单位（秒）
	private String openid; //授权用户唯一标识
	private String pay_token; //支付时专用的pay_token
	private String pf; //平台标识信息
	private String pfkey; //登录时候由平台直接传给应用，应用原样传给平台即可强校验

	public QQEntity(int errcode, String errmsg) {
		super();
		this.errcode = errcode;
		this.errmsg = errmsg;
	}
	

	public QQEntity(int errcode, String errmsg, String access_token,
			String expires_in, String openid, String pay_token, String pf,
			String pfkey) {
		super();
		this.errcode = errcode;
		this.errmsg = errmsg;
		this.access_token = access_token;
		this.expires_in = expires_in;
		this.openid = openid;
		this.pay_token = pay_token;
		this.pf = pf;
		this.pfkey = pfkey;
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

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public String getExpires_in() {
		return expires_in;
	}

	public void setExpires_in(String expires_in) {
		this.expires_in = expires_in;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getPay_token() {
		return pay_token;
	}

	public void setPay_token(String pay_token) {
		this.pay_token = pay_token;
	}

	public String getPf() {
		return pf;
	}

	public void setPf(String pf) {
		this.pf = pf;
	}

	public String getPfkey() {
		return pfkey;
	}

	public void setPfkey(String pfkey) {
		this.pfkey = pfkey;
	}

}
