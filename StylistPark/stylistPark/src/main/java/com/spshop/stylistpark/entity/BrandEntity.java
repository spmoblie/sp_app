package com.spshop.stylistpark.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class BrandEntity implements IndexDisplay, Parcelable {

	String brandId;
	String name;
	String defineUrl;
	String desc;

	public BrandEntity() {
	}

	public void setBrandId(String brandId) {
		this.brandId = brandId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDefineURL(String defineUrl) {
		this.defineUrl = defineUrl;
	}

	public String getBrandId() {
		return brandId;
	}

	public String getName() {
		return name;
	}

	public String getDefineUrl() {
		return defineUrl;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	@Override
	public String getFirstCharIndex() {
		try {
			return getName().substring(0, 1);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	@Override
	public String getValueToBeSort() {
		try {
			return getName();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/*
	 * Parcelable part
	 */

	// example constructor that takes a Parcel and gives you an object populated
	// with it's values
	private BrandEntity(Parcel in) {
		brandId = in.readString();
		name = in.readString();
		defineUrl = in.readString();
	}

	// write your object's data to the passed-in Parcel
	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(brandId);
		out.writeString(name);
		out.writeString(defineUrl);
	}

	// this is used to regenerate your object. All Parcelables must have a
	// CREATOR that implements these two methods
	public static final Parcelable.Creator<BrandEntity> CREATOR = new Parcelable.Creator<BrandEntity>() {
		public BrandEntity createFromParcel(Parcel in) {
			return new BrandEntity(in);
		}

		public BrandEntity[] newArray(int size) {
			return new BrandEntity[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

}
