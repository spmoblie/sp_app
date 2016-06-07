package com.spshop.stylistpark.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class FilterColor implements Parcelable {
	
	String displayName;
	String name;
	int drawableIdIconId;
	int drawableId;
	int colorID;
	
	public FilterColor(){
		displayName="";
		name="";
		drawableId=-1;
		colorID=-1;
		
	}
	
	public void setDisplayName(String displayName){
		this.displayName=displayName;
	}
	
	public void setName(String name){
		this.name=name;
	}
	
	
	public void setDrawableId(int drawableIdIconId, int drawableId){
		this.drawableIdIconId=drawableIdIconId;
		this.drawableId=drawableId;
		colorID=-1;
	}
	
	public void setColorId(int colorID){
		this.colorID=colorID;
		drawableIdIconId=-1;
		drawableId=-1;
		
	}
	
	public String getDisplayName(){
		return displayName;
	}
	
	public String getName(){
		return name;
	}
	
	public int getColorId(){
		return colorID;
	}
	
	public int getDrawableIdIconId(){
		return drawableIdIconId;
	}
	
	public int getDrawableId(){
		return drawableId;
	}
	
	
	/*
	 * Parcelable part
	 */
	
	 // example constructor that takes a Parcel and gives you an object populated with it's values
    private FilterColor(Parcel in) {
    	name = in.readString();
    	drawableIdIconId=in.readInt();
    	drawableId=in.readInt();
    	colorID=in.readInt();
    	displayName=in.readString();
    }
	
	 // write your object's data to the passed-in Parcel
	@Override
	public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeInt(drawableIdIconId);
        out.writeInt(drawableId);
        out.writeInt(colorID);
        out.writeString(displayName);
    }
    
    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<FilterColor> CREATOR = new Parcelable.Creator<FilterColor>() {
        public FilterColor createFromParcel(Parcel in) {
            return new FilterColor(in);
        }

        public FilterColor[] newArray(int size) {
            return new FilterColor[size];
        }
    };


	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

}
