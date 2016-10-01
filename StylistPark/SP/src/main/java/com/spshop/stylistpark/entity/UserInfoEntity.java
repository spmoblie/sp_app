package com.spshop.stylistpark.entity;


public class UserInfoEntity extends BaseEntity {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 用户Id
	 */
	private String userId;

	/**
	 * 推广Id
	 */
	private String shareId;
	
	/**
	 * 用户姓名
	 */
	private String userName;

	/**
	 * 用户身份ID
	 */
	private String userNameID;

	/**
	 * 用户昵称
	 */
	private String userNick;
	
	/**
	 * 用户头像
	 */
	private String headImg;
	
	/**
	 * 用户简介
	 */
	private String userIntro;
	
	/**
	 * 性别代码(0:保密/1:男/2:女)
	 */
	private int sexCode;
	
	/**
	 * 用户生日
	 */
	private String birthday;
	
	/**
	 * 用户邮箱
	 */
	private String userEmail;
	
	/**
	 * 用户手机号码
	 */
	private String userPhone;
	
	/**
	 * 用户等级编号（4 == 达人）
	 */
	private int userRankCode;
	
	/**
	 * 用户等级名称
	 */
	private String userRankName;
	
	/**
	 * 待付款订单数
	 */
	private int order_1;
	
	/**
	 * 待收货订单数
	 */
	private int order_2;
	
	/**
	 * 待评价订单数
	 */
	private int order_3;
	
	/**
	 * 返修/退换订单数
	 */
	private int order_4;
	
	/**
	 * 当前购物车商品数量
	 */
	private int cartTotal;
	
	/**
	 * 当前货币
	 */
	private String currency;
	
	/**
	 * 账户余额
	 */
	private String money;
	
	/**
	 * 优惠券
	 */
	private String coupon;
	
	/**
	 * 会员数
	 */
	private String memberNum;
	
	/**
	 * 会员订单
	 */
	private String memberOrder;

	
	public UserInfoEntity() {
		super();
	}


	public UserInfoEntity(int errCode, String errInfo) {
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

	public String getShareId() {
		return shareId;
	}

	public void setShareId(String shareId) {
		this.shareId = shareId;
	}

	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserNameID() {
		return userNameID;
	}

	public void setUserNameID(String userNameID) {
		this.userNameID = userNameID;
	}

	public String getUserNick() {
		return userNick;
	}


	public void setUserNick(String userNick) {
		this.userNick = userNick;
	}


	public String getHeadImg() {
		return headImg;
	}


	public void setHeadImg(String headImg) {
		this.headImg = headImg;
	}


	public String getUserIntro() {
		return userIntro;
	}


	public void setUserIntro(String userIntro) {
		this.userIntro = userIntro;
	}


	public int getSexCode() {
		return sexCode;
	}


	public void setSexCode(int sexCode) {
		this.sexCode = sexCode;
	}


	public String getBirthday() {
		return birthday;
	}


	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}


	public String getUserEmail() {
		return userEmail;
	}


	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	
	public String getUserPhone() {
		return userPhone;
	}


	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}


	public int getUserRankCode() {
		return userRankCode;
	}


	public void setUserRankCode(int userRankCode) {
		this.userRankCode = userRankCode;
	}


	public String getUserRankName() {
		return userRankName;
	}


	public void setUserRankName(String userRankName) {
		this.userRankName = userRankName;
	}


	public int getOrder_1() {
		return order_1;
	}


	public void setOrder_1(int order_1) {
		this.order_1 = order_1;
	}


	public int getOrder_2() {
		return order_2;
	}


	public void setOrder_2(int order_2) {
		this.order_2 = order_2;
	}


	public int getOrder_3() {
		return order_3;
	}


	public void setOrder_3(int order_3) {
		this.order_3 = order_3;
	}


	public int getOrder_4() {
		return order_4;
	}


	public void setOrder_4(int order_4) {
		this.order_4 = order_4;
	}


	public int getCartTotal() {
		return cartTotal;
	}


	public void setCartTotal(int cartTotal) {
		this.cartTotal = cartTotal;
	}


	public String getCurrency() {
		return currency;
	}


	public void setCurrency(String currency) {
		this.currency = currency;
	}


	public String getMoney() {
		return money;
	}


	public void setMoney(String money) {
		this.money = money;
	}


	public String getCoupon() {
		return coupon;
	}


	public void setCoupon(String coupon) {
		this.coupon = coupon;
	}


	public String getMemberNum() {
		return memberNum;
	}


	public void setMemberNum(String memberNum) {
		this.memberNum = memberNum;
	}


	public String getMemberOrder() {
		return memberOrder;
	}


	public void setMemberOrder(String memberOrder) {
		this.memberOrder = memberOrder;
	}
	
}
