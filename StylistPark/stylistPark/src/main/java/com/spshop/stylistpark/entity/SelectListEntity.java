package com.spshop.stylistpark.entity;

import java.util.List;

public class SelectListEntity extends BaseEntity{
	
	private static final long serialVersionUID = 1L;

	/**
	 * 父级Id
	 */
	private int typeId;
	
	/**
	 * 父级名称
	 */
	private String typeName;
	
	/**
	 * 子级Id
	 */
	private int childId;
	
	/**
	 * 子级回参名称
	 */
	private String childParamName;
	
	/**
	 * 子级展示名称
	 */
	private String childShowName;
	
	/**
	 * 子级Logo Url地址
	 */
	private String childLogoUrl;
	
	/**
	 * 子级Logo本地路径
	 */
	private int childLogoPath;
	
	/**
	 * 选择的子级实体对象
	 */
	private SelectListEntity selectEn;
	
	/**
	 * 子级集合
	 */
	private List<SelectListEntity> childLists;
	
	/**
	 * 集合打包传输
	 */
	private List<SelectListEntity> mainLists;

	
	public SelectListEntity() {
		super();
	}


	public SelectListEntity(int errCode, String errInfo) {
		super(errCode, errInfo);
	}


	public SelectListEntity(int errCode, String errInfo, List<SelectListEntity> mainLists) {
		super(errCode, errInfo);
		this.mainLists = mainLists;
	}

	
	public SelectListEntity(int childId, String childShowName, String childLogoUrl) {
		super();
		this.childId = childId;
		this.childShowName = childShowName;
		this.childLogoUrl = childLogoUrl;
	}


	public int getTypeId() {
		return typeId;
	}


	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}


	public String getTypeName() {
		return typeName;
	}


	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}


	public int getChildId() {
		return childId;
	}


	public void setChildId(int childId) {
		this.childId = childId;
	}


	public String getChildParamName() {
		return childParamName;
	}


	public void setChildParamName(String childParamName) {
		this.childParamName = childParamName;
	}


	public String getChildShowName() {
		return childShowName;
	}


	public void setChildShowName(String childShowName) {
		this.childShowName = childShowName;
	}


	public String getChildLogoUrl() {
		return childLogoUrl;
	}


	public void setChildLogoUrl(String childLogoUrl) {
		this.childLogoUrl = childLogoUrl;
	}


	public int getChildLogoPath() {
		return childLogoPath;
	}


	public void setChildLogoPath(int childLogoPath) {
		this.childLogoPath = childLogoPath;
	}


	public SelectListEntity getSelectEn() {
		return selectEn;
	}


	public void setSelectEn(SelectListEntity selectEn) {
		this.selectEn = selectEn;
	}


	public List<SelectListEntity> getChildLists() {
		return childLists;
	}


	public void setChildLists(List<SelectListEntity> childLists) {
		this.childLists = childLists;
	}


	public List<SelectListEntity> getMainLists() {
		return mainLists;
	}


	public void setMainLists(List<SelectListEntity> mainLists) {
		this.mainLists = mainLists;
	}
	
}
