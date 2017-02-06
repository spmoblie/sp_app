package com.spshop.stylistpark.entity;

import java.util.ArrayList;
import java.util.List;

public class OrderEntity extends BaseEntity {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 订单Id
	 */
	private String orderId;
	
	/**
	 * 订单号
	 */
	private String orderNo;
	
	/**
	 * 订单状态编号
	 * (1:待付款/2:待发货/3:待收货/4:退换货/5:交易完成/6:交易关闭/其他:全部)
	 */
	private int status;
	
	/**
	 * 订单状态名称
	 */
	private String statusName;
	
	/**
	 * 物流公司名称
	 */
	private String logisticsName;
	
	/**
	 * 物流单号
	 */
	private String logisticsNo;
	
	/**
	 * 当前货币
	 */
	private String currency;
	
	/**
	 * 订单总价名称
	 */
	private String priceTotalName;
	
	/**
	 * 订单总价
	 */
	private String priceTotal;
	
	/**
	 * 运费名称
	 */
	private String priceFeeName;
	
	/**
	 * 运费
	 */
	private String priceFee;
	
	/**
	 * 支付手续费名称
	 */
	private String priceChargesName;
	
	/**
	 * 支付手续费
	 */
	private String priceCharges;
	
	/**
	 * 优惠券抵用名称
	 */
	private String priceCouponName;
	
	/**
	 * 优惠券抵用金额
	 */
	private String priceCoupon;
	
	/**
	 * 抵用优惠券Id
	 */
	private String couponId;

	/**
	 * 账户可使用余额
	 */
	private String userBalance;

	/**
	 * 活动优惠名称
	 */
	private String priceDiscountName;
	
	/**
	 * 活动优惠金额
	 */
	private String priceDiscount;

	/**
	 * 达人返现名称
	 */
	private String priceCashbackName;

	/**
	 * 达人返现金额
	 */
	private String priceCashback;

	/**
	 * 余额支付名称
	 */
	private String priceBalanceName;

	/**
	 * 余额支付
	 */
	private String priceBalance;

	/**
	 * 已付金额名称
	 */
	private String pricePaidName;
	
	/**
	 * 已付金额
	 */
	private String pricePaid;

	/**
	 * 应付金额名称
	 */
	private String pricePayName;

	/**
	 * 应付金额
	 */
	private String pricePay;

	/**
	 * 订单金额
	 */
	private String orderAmount;
	
	/**
	 * 支付Id
	 */
	private int payId;
	
	/**
	 * 支付方式
	 */
	private String payType;
	
	/**
	 * 配送方式编码
	 */
	private int shippingCode;

	/**
	 * 配送方式名称
	 */
	private String shippingName;

	/**
	 * 发票信息名称
	 */
	private String invoiceName;
	
	/**
	 * 发票类型
	 */
	private String invoiceType;
	
	/**
	 * 发票抬头
	 */
	private String invoicePayee;
	
	/**
	 * 订单备注名称
	 */
	private String buyerName;
	
	/**
	 * 订单备注
	 */
	private String buyer;
	
	/**
	 * 订单商品总数量
	 */
	private int goodsTotal;

	/**
	 * 订单商品总数量(String)
	 */
	private String goodsTotalStr;

	/**
	 * 订单生成时间
	 */
	private long createTime;
	
	/**
	 * 订单有效时间
	 */
	private long validTime;

	/**
	 * 订单日期
	 */
	private String addTime;

	/**
	 * 收货地址
	 */
	private AddressEntity addressEn;
	
	/**
	 * 订单用户信息
	 */
	private UserInfoEntity userInfo;
	
	/**
	 * 商品集合
	 */
	private ArrayList<ProductListEntity> goodsLists;

	/**
	 * 配送列表
	 */
	private ArrayList<AddressEntity> addLists;

	/**
	 * 传输数据集
	 */
	private List<OrderEntity> mainLists;
	

	public OrderEntity() {
		super();
	}
	
	
	public OrderEntity(int errCode, String errInfo) {
		super(errCode, errInfo);
	}

	@Override
	public String getEntityId() {
		return orderId;
	}

	public String getOrderId() {
		return orderId;
	}


	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}


	public String getOrderNo() {
		return orderNo;
	}


	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}


	public int getStatus() {
		return status;
	}


	public void setStatus(int status) {
		this.status = status;
	}


	public String getStatusName() {
		return statusName;
	}


	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}


	public String getLogisticsName() {
		return logisticsName;
	}


	public void setLogisticsName(String logisticsName) {
		this.logisticsName = logisticsName;
	}


	public String getLogisticsNo() {
		return logisticsNo;
	}


	public void setLogisticsNo(String logisticsNo) {
		this.logisticsNo = logisticsNo;
	}


	public String getCurrency() {
		return currency;
	}


	public void setCurrency(String currency) {
		this.currency = currency;
	}


	public String getPriceTotalName() {
		return priceTotalName;
	}


	public void setPriceTotalName(String priceTotalName) {
		this.priceTotalName = priceTotalName;
	}


	public String getPriceTotal() {
		return priceTotal;
	}


	public void setPriceTotal(String priceTotal) {
		this.priceTotal = priceTotal;
	}


	public String getPriceFeeName() {
		return priceFeeName;
	}


	public void setPriceFeeName(String priceFeeName) {
		this.priceFeeName = priceFeeName;
	}


	public String getPriceFee() {
		return priceFee;
	}


	public void setPriceFee(String priceFee) {
		this.priceFee = priceFee;
	}


	public String getPriceChargesName() {
		return priceChargesName;
	}


	public void setPriceChargesName(String priceChargesName) {
		this.priceChargesName = priceChargesName;
	}


	public String getPriceCharges() {
		return priceCharges;
	}


	public void setPriceCharges(String priceCharges) {
		this.priceCharges = priceCharges;
	}


	public String getPriceCouponName() {
		return priceCouponName;
	}


	public void setPriceCouponName(String priceCouponName) {
		this.priceCouponName = priceCouponName;
	}


	public String getPriceCoupon() {
		return priceCoupon;
	}


	public void setPriceCoupon(String priceCoupon) {
		this.priceCoupon = priceCoupon;
	}


	public String getCouponId() {
		return couponId;
	}


	public void setCouponId(String couponId) {
		this.couponId = couponId;
	}


	public String getUserBalance() {
		return userBalance;
	}


	public void setUserBalance(String userBalance) {
		this.userBalance = userBalance;
	}


	public String getPriceDiscountName() {
		return priceDiscountName;
	}


	public void setPriceDiscountName(String priceDiscountName) {
		this.priceDiscountName = priceDiscountName;
	}


	public String getPriceDiscount() {
		return priceDiscount;
	}


	public void setPriceDiscount(String priceDiscount) {
		this.priceDiscount = priceDiscount;
	}


	public String getPriceCashbackName() {
		return priceCashbackName;
	}


	public void setPriceCashbackName(String priceCashbackName) {
		this.priceCashbackName = priceCashbackName;
	}


	public String getPriceCashback() {
		return priceCashback;
	}


	public void setPriceCashback(String priceCashback) {
		this.priceCashback = priceCashback;
	}


	public String getPriceBalanceName() {
		return priceBalanceName;
	}


	public void setPriceBalanceName(String priceBalanceName) {
		this.priceBalanceName = priceBalanceName;
	}


	public String getPriceBalance() {
		return priceBalance;
	}


	public void setPriceBalance(String priceBalance) {
		this.priceBalance = priceBalance;
	}

	public String getPricePaidName() {
		return pricePaidName;
	}


	public void setPricePaidName(String pricePaidName) {
		this.pricePaidName = pricePaidName;
	}


	public String getPricePaid() {
		return pricePaid;
	}


	public void setPricePaid(String pricePaid) {
		this.pricePaid = pricePaid;
	}


	public String getPricePayName() {
		return pricePayName;
	}


	public void setPricePayName(String pricePayName) {
		this.pricePayName = pricePayName;
	}


	public String getOrderAmount() {
		return orderAmount;
	}


	public void setOrderAmount(String orderAmount) {
		this.orderAmount = orderAmount;
	}


	public String getPricePay() {
		return pricePay;
	}


	public void setPricePay(String pricePay) {
		this.pricePay = pricePay;
	}


	public int getPayId() {
		return payId;
	}


	public void setPayId(int payId) {
		this.payId = payId;
	}


	public String getPayType() {
		return payType;
	}


	public void setPayType(String payType) {
		this.payType = payType;
	}

	public int getShippingCode() {
		return shippingCode;
	}

	public void setShippingCode(int shippingCode) {
		this.shippingCode = shippingCode;
	}

	public String getShippingName() {
		return shippingName;
	}

	public void setShippingName(String shippingName) {
		this.shippingName = shippingName;
	}

	public String getInvoiceName() {
		return invoiceName;
	}


	public void setInvoiceName(String invoiceName) {
		this.invoiceName = invoiceName;
	}


	public String getInvoiceType() {
		return invoiceType;
	}


	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
	}


	public String getInvoicePayee() {
		return invoicePayee;
	}


	public void setInvoicePayee(String invoicePayee) {
		this.invoicePayee = invoicePayee;
	}


	public String getBuyerName() {
		return buyerName;
	}


	public void setBuyerName(String buyerName) {
		this.buyerName = buyerName;
	}


	public String getBuyer() {
		return buyer;
	}


	public void setBuyer(String buyer) {
		this.buyer = buyer;
	}


	public int getGoodsTotal() {
		return goodsTotal;
	}


	public void setGoodsTotal(int goodsTotal) {
		this.goodsTotal = goodsTotal;
	}

	public String getGoodsTotalStr() {
		return goodsTotalStr;
	}

	public void setGoodsTotalStr(String goodsTotalStr) {
		this.goodsTotalStr = goodsTotalStr;
	}


	public long getCreateTime() {
		return createTime;
	}


	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}


	public long getValidTime() {
		return validTime;
	}


	public void setValidTime(long validTime) {
		this.validTime = validTime;
	}


	public String getAddTime() {
		return addTime;
	}


	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}


	public AddressEntity getAddressEn() {
		return addressEn;
	}


	public void setAddressEn(AddressEntity addressEn) {
		this.addressEn = addressEn;
	}


	public UserInfoEntity getUserInfo() {
		return userInfo;
	}


	public void setUserInfo(UserInfoEntity userInfo) {
		this.userInfo = userInfo;
	}


	public ArrayList<ProductListEntity> getGoodsLists() {
		return goodsLists;
	}


	public void setGoodsLists(ArrayList<ProductListEntity> goodsLists) {
		this.goodsLists = goodsLists;
	}

	public ArrayList<AddressEntity> getAddLists() {
		return addLists;
	}

	public void setAddLists(ArrayList<AddressEntity> addLists) {
		this.addLists = addLists;
	}

	public List<OrderEntity> getMainLists() {
		return mainLists;
	}


	public void setMainLists(List<OrderEntity> mainLists) {
		this.mainLists = mainLists;
	}

}
