package com.spshop.stylistpark.entity;

import java.util.ArrayList;
import java.util.List;


public class ClipPhotoEntity extends BaseEntity {
	
	private static final long serialVersionUID = 4438798104656417389L;
	
	private String name; //相册名称
	private String count; //数量
	private String photoUrl; //相片路径
	private int photoId; //相片Id
	private int bitmap; //相册的第一张相片
	private boolean select; //记录选择的相片
	private List<ClipPhotoEntity> bitList = new ArrayList<ClipPhotoEntity>(); //打包集合


	public ClipPhotoEntity() {
		super();
	}


	public ClipPhotoEntity(int photoId, String photoUrl) {
		super();
		this.photoId = photoId;
		this.photoUrl = photoUrl;
	}

	@Override
	public String getEntityId() {
		return String.valueOf(photoId);
	}

	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getCount() {
		return count;
	}


	public void setCount(String count) {
		this.count = count;
	}


	public int getPhotoId() {
		return photoId;
	}


	public void setPhotoId(int photoId) {
		this.photoId = photoId;
	}


	public String getPhotoUrl() {
		return photoUrl;
	}


	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}


	public int getBitmap() {
		return bitmap;
	}


	public void setBitmap(int bitmap) {
		this.bitmap = bitmap;
	}


	public boolean isSelect() {
		return select;
	}


	public void setSelect(boolean select) {
		this.select = select;
	}


	public List<ClipPhotoEntity> getBitList() {
		return bitList;
	}


	public void setBitList(List<ClipPhotoEntity> bitList) {
		this.bitList = bitList;
	}

}
