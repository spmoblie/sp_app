package com.spshop.stylistpark.entity;

import java.util.List;

public class ThemeEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	private int id;
	private int type;
	private int clickNum;
	private long endTime;
	private String title;
	private String nick;
	private String avatar;
	private String imgUrl;
	private String vdoUrl;
	private ThemeEntity adEn; //广告
	private ThemeEntity windowEn; //橱窗
	private ProductListEntity goodsEn; //热销商品
	private ThemeEntity peidaEn; //今日专题
	private ThemeEntity saleEn; //限时活动
	private List<ThemeEntity> mainLists;

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

	public int getClickNum() {
		return clickNum;
	}

	public void setClickNum(int clickNum) {
		this.clickNum = clickNum;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
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

	public ThemeEntity getWindowEn() {
		return windowEn;
	}

	public void setWindowEn(ThemeEntity windowEn) {
		this.windowEn = windowEn;
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
