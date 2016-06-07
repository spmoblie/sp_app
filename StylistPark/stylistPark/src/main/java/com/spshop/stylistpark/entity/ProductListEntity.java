package com.spshop.stylistpark.entity;

import java.util.List;

public class ProductListEntity extends BaseEntity{
	
	private static final long serialVersionUID = 1L;

	/**
	 * 商品id
	 */
	private int id;
	
	/**
	 * 商品图片
	 */
	private String imageUrl;
	
	/**
	 * 商品名称
	 */
	private String name;
	
	/**
	 * 品牌商
	 */
	private String brand;
	
	/**
	 * 商品属性
	 */
	private String attr;
	
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
	private int computePrice;

	/**
	 * 商品折扣
	 */
	private String discount;
	
	/**
	 * 会员佣金
	 */
	private String commission;
	
	/**
	 * 商品总数量
	 */
	private int total;
	
	/**
	 * 集合打包传输
	 */
	private List<ProductListEntity> mainLists;

	
	public ProductListEntity() {
		super();
	}


	public ProductListEntity(int errCode, String errInfo) {
		super(errCode, errInfo);
	}


	public ProductListEntity(int errCode, String errInfo, List<ProductListEntity> mainLists) {
		super(errCode, errInfo);
		this.mainLists = mainLists;
	}


	public ProductListEntity(int id, String imageUrl, String name,
			String brand, String fullPrice, String sellPrice, int total) {
		super();
		this.id = id;
		this.imageUrl = imageUrl;
		this.name = name;
		this.brand = brand;
		this.fullPrice = fullPrice;
		this.sellPrice = sellPrice;
		this.total = total;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getImageUrl() {
		return imageUrl;
	}


	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getBrand() {
		return brand;
	}


	public void setBrand(String brand) {
		this.brand = brand;
	}


	public String getAttr() {
		return attr;
	}


	public void setAttr(String attr) {
		this.attr = attr;
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


	public int getComputePrice() {
		return computePrice;
	}


	public void setComputePrice(int computePrice) {
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


	public int getTotal() {
		return total;
	}


	public void setTotal(int total) {
		this.total = total;
	}


	public List<ProductListEntity> getMainLists() {
		return mainLists;
	}


	public void setMainLists(List<ProductListEntity> mainLists) {
		this.mainLists = mainLists;
	}
	
}
