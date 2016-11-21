package com.spshop.stylistpark.entity;

import java.util.List;

public class ProductDetailEntity extends BaseEntity{
	
	private static final long serialVersionUID = 1L;

	/**
	 * 商品id
	 */
	private int id;
	
	/**
	 * 商品名称
	 */
	private String name;
	
	/**
	 * 商品库存数
	 */
	private int stockNum;
	
	/**
	 * 购物车商品列表id
	 */
	private int recId;
	
	/**
	 * 放入购物车的商品数量
	 */
	private int cartNum;
	
	/**
	 * 放入购物车的商品属性
	 */
	private String attrStr;
	
	/**
	 * 品牌Id
	 */
	private String brandId;
	
	/**
	 * 品牌Logo
	 */
	private String brandLogo;
	
	/**
	 * 品牌名称
	 */
	private String brandName;
	
	/**
	 * 品牌国家
	 */
	private String brandCountry;
	
	/**
	 * 商品活动类型
	 */
	private String promotionType;
	
	/**
	 * 商品活动内容
	 */
	private String promotionName;
	
	/**
	 * 商品发货地
	 */
	private String mailCountry;
	
	/**
	 * 商品折价倒计时
	 */
	private long promoteTime;
	
	/**
	 * 当前使用货币
	 */
	private String currency;
	
	/**
	 * 商品原价
	 */
	private String fullPrice;
	
	/**
	 * 商品卖价
	 */
	private String sellPrice;
	
	/**
	 * 商品结算价
	 */
	private double computePrice;

	/**
	 * 商品折扣
	 */
	private String discount;

	/**
	 * 达人返现
	 */
	private String commission;

	/**
	 * 是否收藏
	 */
	private String isCollection;
	
	/**
	 * 是否有视频(0:无/1:有)
	 */
	private int isVideo;
	
	/**
	 * 视频路径
	 */
	private String videoUrl;
	
	/**
	 * 图片Id
	 */
	private String imgId;
	
	/**
	 * 图片Url（小）
	 */
	private String imgMinUrl;
	
	/**
	 * 图片Url（大）
	 */
	private String imgMaxUrl;

	/**
	 * 分享实体
	 */
	private ShareEntity shareEn;
	
	/**
	 * 图片集合
	 */
	private List<ProductDetailEntity> imgLists;
	
	/**
	 * 商品活动集合
	 */
	private List<ProductDetailEntity> promotionLists;

	
	public ProductDetailEntity() {
		super();
	}


	public ProductDetailEntity(int errCode, String errInfo) {
		super(errCode, errInfo);
	}

	@Override
	public String getEntityId() {
		return String.valueOf(id);
	}

	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public int getStockNum() {
		return stockNum;
	}


	public void setStockNum(int stockNum) {
		this.stockNum = stockNum;
	}


	public int getRecId() {
		return recId;
	}


	public void setRecId(int recId) {
		this.recId = recId;
	}


	public int getCartNum() {
		return cartNum;
	}


	public void setCartNum(int cartNum) {
		this.cartNum = cartNum;
	}


	public String getAttrStr() {
		return attrStr;
	}


	public void setAttrStr(String attrStr) {
		this.attrStr = attrStr;
	}


	public String getBrandId() {
		return brandId;
	}


	public void setBrandId(String brandId) {
		this.brandId = brandId;
	}


	public String getBrandLogo() {
		return brandLogo;
	}


	public void setBrandLogo(String brandLogo) {
		this.brandLogo = brandLogo;
	}


	public String getBrandName() {
		return brandName;
	}


	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}


	public String getBrandCountry() {
		return brandCountry;
	}


	public void setBrandCountry(String brandCountry) {
		this.brandCountry = brandCountry;
	}


	public String getPromotionName() {
		return promotionName;
	}


	public void setPromotionName(String promotionName) {
		this.promotionName = promotionName;
	}


	public String getMailCountry() {
		return mailCountry;
	}


	public void setMailCountry(String mailCountry) {
		this.mailCountry = mailCountry;
	}


	public long getPromoteTime() {
		return promoteTime;
	}


	public void setPromoteTime(long promoteTime) {
		this.promoteTime = promoteTime;
	}


	public String getCurrency() {
		return currency;
	}


	public void setCurrency(String currency) {
		this.currency = currency;
	}


	public String getFullPrice() {
		return fullPrice;
	}


	public void setFullPrice(String fullPrice) {
		this.fullPrice = fullPrice;
	}


	public String getSellPrice() {
		return sellPrice;
	}


	public void setSellPrice(String sellPrice) {
		this.sellPrice = sellPrice;
	}


	public double getComputePrice() {
		return computePrice;
	}


	public void setComputePrice(double computePrice) {
		this.computePrice = computePrice;
	}


	public String getDiscount() {
		return discount;
	}


	public void setDiscount(String discount) {
		this.discount = discount;
	}


	public String getCommission() {
		return commission;
	}


	public void setCommission(String commission) {
		this.commission = commission;
	}


	public String getIsCollection() {
		return isCollection;
	}


	public void setIsCollection(String isCollection) {
		this.isCollection = isCollection;
	}


	public int getIsVideo() {
		return isVideo;
	}


	public void setIsVideo(int isVideo) {
		this.isVideo = isVideo;
	}


	public String getVideoUrl() {
		return videoUrl;
	}


	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}


	public String getImgId() {
		return imgId;
	}


	public void setImgId(String imgId) {
		this.imgId = imgId;
	}


	public String getImgMinUrl() {
		return imgMinUrl;
	}


	public void setImgMinUrl(String imgMinUrl) {
		this.imgMinUrl = imgMinUrl;
	}


	public String getImgMaxUrl() {
		return imgMaxUrl;
	}


	public void setImgMaxUrl(String imgMaxUrl) {
		this.imgMaxUrl = imgMaxUrl;
	}


	public ShareEntity getShareEn() {
		return shareEn;
	}


	public void setShareEn(ShareEntity shareEn) {
		this.shareEn = shareEn;
	}


	public List<ProductDetailEntity> getImgLists() {
		return imgLists;
	}


	public void setImgLists(List<ProductDetailEntity> imgLists) {
		this.imgLists = imgLists;
	}


	public String getPromotionType() {
		return promotionType;
	}


	public void setPromotionType(String promotionType) {
		this.promotionType = promotionType;
	}


	public List<ProductDetailEntity> getPromotionLists() {
		return promotionLists;
	}


	public void setPromotionLists(List<ProductDetailEntity> promotionLists) {
		this.promotionLists = promotionLists;
	}

}
