package com.spshop.stylistpark.entity;

import java.util.List;


public class GoodsCartEntity extends BaseEntity{
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 购物车商品总数量
	 */
	private int goodsTotal;
	
	/**
	 * 购物车商品总价格
	 */
	private String amount;
	
	/**
	 * 当前货币
	 */
	private String currency;
	
	/**
	 * 某商品的总库存
	 */
	private int skuNum;
	
	/**
	 * 商品子集合
	 */
	private List<ProductDetailEntity> childLists;

	
	public GoodsCartEntity() {
		super();
	}


	public GoodsCartEntity(int errCode, String errInfo) {
		super(errCode, errInfo);
	}


	public int getGoodsTotal() {
		return goodsTotal;
	}


	public void setGoodsTotal(int goodsTotal) {
		this.goodsTotal = goodsTotal;
	}


	public String getAmount() {
		return amount;
	}


	public void setAmount(String amount) {
		this.amount = amount;
	}


	public String getCurrency() {
		return currency;
	}


	public void setCurrency(String currency) {
		this.currency = currency;
	}


	public int getSkuNum() {
		return skuNum;
	}


	public void setSkuNum(int skuNum) {
		this.skuNum = skuNum;
	}


	public List<ProductDetailEntity> getChildLists() {
		return childLists;
	}


	public void setChildLists(List<ProductDetailEntity> childLists) {
		this.childLists = childLists;
	}
	
}
