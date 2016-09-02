package com.spshop.stylistpark.entity;

import java.util.List;

public class ThemeEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	int id;
	int type;
	int clickNum;
	int countTotal;
	long endTime;
	String title;
	String mebName;
	String mebUrl;
	String imgUrl;
	String vdoUrl;
	ThemeEntity adEn; //广告
	ProductListEntity goodsEn; //热销商品
	ThemeEntity peidaEn; //今日专题
	ThemeEntity saleEn; //限时活动
	List<ThemeEntity> mainLists;

	public ThemeEntity() {
		super();
	}

	public ThemeEntity(int errCode, String errInfo) {
		super(errCode, errInfo);
	}

	@Override
	public String getEntityId() {
		return String.valueOf(id);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getVdoUrl() {
		return vdoUrl;
	}

	public void setVdoUrl(String vdoUrl) {
		this.vdoUrl = vdoUrl;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getCountTotal() {
		return countTotal;
	}

	public void setCountTotal(int countTotal) {
		this.countTotal = countTotal;
	}

	public int getClickNum() {
		return clickNum;
	}

	public void setClickNum(int clickNum) {
		this.clickNum = clickNum;
	}

	public String getMebName() {
		return mebName;
	}

	public void setMebName(String mebName) {
		this.mebName = mebName;
	}

	public String getMebUrl() {
		return mebUrl;
	}

	public void setMebUrl(String mebUrl) {
		this.mebUrl = mebUrl;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public ThemeEntity getAdEn() {
		return adEn;
	}

	public void setAdEn(ThemeEntity adEn) {
		this.adEn = adEn;
	}

	public ProductListEntity getGoodsEn() {
		return goodsEn;
	}

	public void setGoodsEn(ProductListEntity goodsEn) {
		this.goodsEn = goodsEn;
	}

	public ThemeEntity getPeidaEn() {
		return peidaEn;
	}

	public void setPeidaEn(ThemeEntity peidaEn) {
		this.peidaEn = peidaEn;
	}

	public ThemeEntity getSaleEn() {
		return saleEn;
	}

	public void setSaleEn(ThemeEntity saleEn) {
		this.saleEn = saleEn;
	}

	public List<ThemeEntity> getMainLists() {
		return mainLists;
	}

	public void setMainLists(List<ThemeEntity> mainLists) {
		this.mainLists = mainLists;
	}
}
