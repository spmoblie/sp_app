package com.spshop.stylistpark.entity;

import android.database.Cursor;

public class Decoration {
	
//	public final static int[] LargeDecoration={R.drawable.decoration_image_001,
//												R.drawable.decoration_image_002,
//												R.drawable.decoration_image_003,
//												R.drawable.decoration_image_004,
//												R.drawable.decoration_image_005,
//												R.drawable.decoration_image_006,
//												R.drawable.decoration_image_007,
//												R.drawable.decoration_image_008,
//												R.drawable.decoration_image_009};
	
//	int imgId;
//	int imgThumbId;
	String id;
	String name;
	String keyWord;
	String color;
	String path;
	String url2x;
	String url3x;

	public Decoration(){
		
	}
	
	public Decoration(Cursor c){

		id=c.getString(0);
		name=c.getString(1);
		color=c.getString(2);
		keyWord=c.getString(3);
//		imgId=Tools.getResId(c.getString(4),R.drawable.class);
		url2x=c.getString(5);
		url3x=c.getString(6);
//		boolean isLarge=false;
//		for(int i=0;i<LargeDecoration.length;i++){
//			if(imgId==LargeDecoration[i]){
//				isLarge=true;
//				break;
//			}
//		}
//		if(isLarge){
//			imgThumbId=Tools.getResId(c.getString(4)+"_300x300",R.drawable.class);
//		}else{
//			imgThumbId=Tools.getResId(c.getString(4),R.drawable.class);
//		}
//		imgThumbId=Tools.getResId(c.getString(4),R.drawable.class);
		
	}

//	public void setImgId(int imgId){
//		this.imgId=imgId;
//	}
//	public void setImgThumbId(int imgThumbId){
//		this.imgThumbId=imgThumbId;
//	}
//	public int getThumbId(){
//		return imgThumbId;
//	}
//	public int getImgId(){
//		return imgId;
//	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getKeyWord() {
		return keyWord;
	}
	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}

    public String getUrl2x()
    {
        return url2x;
    }

    public void setUrl2x(String url2x)
    {
        this.url2x = url2x;
    }

    public String getUrl3x()
    {
        return url3x;
    }

    public void setUrl3x(String url3x)
    {
        this.url3x = url3x;
    }
	
}
