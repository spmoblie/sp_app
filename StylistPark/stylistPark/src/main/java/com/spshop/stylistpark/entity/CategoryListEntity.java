package com.spshop.stylistpark.entity;

import java.util.List;

public class CategoryListEntity extends BaseEntity{
	
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
	private List<CategoryListEntity> childLists;
	
	/**
	 * 品牌列表集合
	 */
	private List<BrandEntity> brandLists;
	
	/**
	 * 集合打包传输
	 */
	private List<CategoryListEntity> mainLists;

	
	public CategoryListEntity() {
		super();
	}


	public CategoryListEntity(int errCode, String errInfo) {
		super(errCode, errInfo);
	}


	public CategoryListEntity(int errCode, String errInfo, List<CategoryListEntity> mainLists) {
		super(errCode, errInfo);
		this.mainLists = mainLists;
	}


	public CategoryListEntity(int typeId, String imageUrl, String name) {
		super();
		this.typeId = typeId;
		this.imageUrl = imageUrl;
		this.name = name;
	}


	public CategoryListEntity(int typeId, String imageUrl, String name,
			List<CategoryListEntity> childLists) {
		super();
		this.typeId = typeId;
		this.imageUrl = imageUrl;
		this.name = name;
		this.childLists = childLists;
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


	public List<CategoryListEntity> getChildLists() {
		return childLists;
	}


	public void setChildLists(List<CategoryListEntity> childLists) {
		this.childLists = childLists;
	}


	public List<BrandEntity> getBrandLists() {
		return brandLists;
	}


	public void setBrandLists(List<BrandEntity> brandLists) {
		this.brandLists = brandLists;
	}


	public List<CategoryListEntity> getMainLists() {
		return mainLists;
	}


	public void setMainLists(List<CategoryListEntity> mainLists) {
		this.mainLists = mainLists;
	}
	
}
