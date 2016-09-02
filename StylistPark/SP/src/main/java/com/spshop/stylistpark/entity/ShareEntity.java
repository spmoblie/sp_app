package com.spshop.stylistpark.entity;

import android.net.Uri;

import java.util.ArrayList;

public class ShareEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;
	
	private String id; //分享对象的ID（如果有多个ID则用“，”隔离）
	private String title; //分享的标题
	private String text; //分享文本
	private String imagePath; //图片的本地路径
	private String imageUrl; //图片的网络路径
	private String longImgPath; //长图片的本地路径
	private String url; //分享的链接
	private int type; //分享对象的类型
	private ArrayList<Uri> imageUris; //保存到本地的图片Uri集
	
	
	public ShareEntity() {
		
	}

	public ShareEntity(int errCode, String errInfo) {
		super(errCode, errInfo);
	}

	@Override
	public String getEntityId() {
		return id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getLongImgPath() {
		return longImgPath;
	}

	public void setLongImgPath(String longImgPath) {
		this.longImgPath = longImgPath;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public ArrayList<Uri> getImageUris() {
		return imageUris;
	}

	public void setImageUris(ArrayList<Uri> imageUris) {
		this.imageUris = imageUris;
	}

}
