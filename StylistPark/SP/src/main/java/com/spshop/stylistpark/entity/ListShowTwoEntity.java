package com.spshop.stylistpark.entity;

import java.io.Serializable;

public class ListShowTwoEntity implements Serializable{
	
	private static final long serialVersionUID = 2980439304361030908L;
	
	private BaseEntity leftEn, rightEn;
	
	
	public ListShowTwoEntity() {
		super();
	}


	public BaseEntity getLeftEn() {
		return leftEn;
	}


	public void setLeftEn(BaseEntity leftEn) {
		this.leftEn = leftEn;
	}


	public BaseEntity getRightEn() {
		return rightEn;
	}


	public void setRightEn(BaseEntity rightEn) {
		this.rightEn = rightEn;
	}
	
}
