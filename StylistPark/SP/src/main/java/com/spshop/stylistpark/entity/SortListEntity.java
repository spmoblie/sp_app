package com.spshop.stylistpark.entity;

import java.util.List;

public class SortListEntity extends BaseEntity {
	
	private static final long serialVersionUID = 1L;

	/**
	 * 分类id
	 */
	private int typeId;
	
	/**
	 * 分类图片
	 */
	private String imageUrl;
	
	/**
	 * 分类名称
	 */
	private String name;
	
	/**
	 * 子级分类集合
	 */
	private List<SortListEntity> childLists;
	
	/**
	 * 品牌列表集合
	 */
	private List<BrandEntity> brandLists;
	
	/**
	 * 集合打包传输
	 */
	private List<SortListEntity> mainLists;

	
	public SortListEntity() {
		super();
	}


	public SortListEntity(int errCode, String errInfo) {
		super(errCode, errInfo);
	}


	public SortListEntity(int errCode, String errInfo, List<SortListEntity> mainLists) {
		super(errCode, errInfo);
		this.mainLists = mainLists;
	}


	public SortListEntity(int typeId, String imageUrl, String name) {
		super();
		this.typeId = typeId;
		this.imageUrl = imageUrl;
		this.name = name;
	}


	public SortListEntity(int typeId, String imageUrl, String name,
						  List<SortListEntity> childLists) {
		super();
		this.typeId = typeId;
		this.imageUrl = imageUrl;
		this.name = name;
		this.childLists = childLists;
	}

	@Override
	public String getEntityId() {
		return String.valueOf(typeId);
	}

	public int getTypeId() {
		return typeId;
	}


	public void setTypeId(int typeId) {
		this.typeId = typeId;
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


	public List<SortListEntity> getChildLists() {
		return childLists;
	}


	public void setChildLists(List<SortListEntity> childLists) {
		this.childLists = childLists;
	}


	public List<BrandEntity> getBrandLists() {
		return brandLists;
	}


	public void setBrandLists(List<BrandEntity> brandLists) {
		this.brandLists = brandLists;
	}


	public List<SortListEntity> getMainLists() {
		return mainLists;
	}


	public void setMainLists(List<SortListEntity> mainLists) {
		this.mainLists = mainLists;
	}
	
}
