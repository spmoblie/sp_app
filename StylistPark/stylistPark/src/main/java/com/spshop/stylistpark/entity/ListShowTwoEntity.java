package com.spshop.stylistpark.entity;

import java.io.Serializable;

public class ListShowTwoEntity implements Serializable{
	
	private static final long serialVersionUID = 2980439304361030908L;
	
	private ProductListEntity leftEn;
	private ProductListEntity rightEn;
	
	
	public ListShowTwoEntity() {
		super();
	}


	public ProductListEntity getLeftEn() {
		return leftEn;
	}


	public void setLeftEn(ProductListEntity leftEn) {
		this.leftEn = leftEn;
	}


	public ProductListEntity getRightEn() {
		return rightEn;
	}


	public void setRightEn(ProductListEntity rightEn) {
		this.rightEn = rightEn;
	}
	
}
