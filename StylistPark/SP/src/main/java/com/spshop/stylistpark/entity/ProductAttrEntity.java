package com.spshop.stylistpark.entity;

import java.util.ArrayList;

public class ProductAttrEntity extends BaseEntity {
	
	private static final long serialVersionUID = 1L;

	/**
	 * 属性id
	 */
	private int attrId;

	/**
	 * 商品id
	 */
	private int goodsId;

	/**
	 * 首张缩略图
	 */
	private String fristImgUrl;

	/**
	 * 商品结算价
	 */
	private double computePrice;

	/**
	 * 商品库存数
	 */
	private int skuNum;

	/**
	 * 商品属性名称
	 */
	private String attrName;
	
	/**
	 * 商品属性价值
	 */
	private double attrPrice;
	
	/**
	 * 商品属性图片
	 */
	private String attrImg;
	
	/**
	 * 属性集合
	 */
	private ArrayList<ProductAttrEntity> attrLists;
	
	/**
	 * 商品库存集合Key
	 */
	private String sku_key;
	
	/**
	 * 商品库存集合Value
	 */
	private int sku_value;
	
	/**
	 * 商品库存集合
	 */
	private ArrayList<ProductAttrEntity> skuLists;

	
	public ProductAttrEntity() {
		super();
	}


	public ProductAttrEntity(int errCode, String errInfo) {
		super(errCode, errInfo);
	}

	@Override
	public String getEntityId() {
		return String.valueOf(attrId);
	}

	public int getAttrId() {
		return attrId;
	}


	public void setAttrId(int attrId) {
		this.attrId = attrId;
	}

	public int getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(int goodsId) {
		this.goodsId = goodsId;
	}

	public String getFristImgUrl() {
		return fristImgUrl;
	}

	public void setFristImgUrl(String fristImgUrl) {
		this.fristImgUrl = fristImgUrl;
	}

	public double getComputePrice() {
		return computePrice;
	}

	public void setComputePrice(double computePrice) {
		this.computePrice = computePrice;
	}

	public int getSkuNum() {
		return skuNum;
	}


	public void setSkuNum(int skuNum) {
		this.skuNum = skuNum;
	}


	public String getAttrName() {
		return attrName;
	}


	public void setAttrName(String attrName) {
		this.attrName = attrName;
	}


	public double getAttrPrice() {
		return attrPrice;
	}


	public void setAttrPrice(double attrPrice) {
		this.attrPrice = attrPrice;
	}


	public String getAttrImg() {
		return attrImg;
	}


	public void setAttrImg(String attrImg) {
		this.attrImg = attrImg;
	}


	public ArrayList<ProductAttrEntity> getAttrLists() {
		return attrLists;
	}


	public void setAttrLists(ArrayList<ProductAttrEntity> attrLists) {
		this.attrLists = attrLists;
	}


	public String getSku_key() {
		return sku_key;
	}


	public void setSku_key(String sku_key) {
		this.sku_key = sku_key;
	}


	public int getSku_value() {
		return sku_value;
	}


	public void setSku_value(int sku_value) {
		this.sku_value = sku_value;
	}


	public ArrayList<ProductAttrEntity> getSkuLists() {
		return skuLists;
	}


	public void setSkuLists(ArrayList<ProductAttrEntity> skuLists) {
		this.skuLists = skuLists;
	}
	
}
