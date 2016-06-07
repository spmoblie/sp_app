package com.spshop.stylistpark.collageviews;

import android.graphics.drawable.Drawable;
import android.view.View;

public class Edit {
	
	public enum Type{add, delete, move, scale, rotate, moveUp, moveDown, changeImage}
	
	public View view;
	public int index;
	public float value1, value2; // scale, rotate
	public float valueY1, valueY2;
	public Drawable drawable1, drawable2;
	public Type type;
	public String url1, url2;
	public int type1, type2; // NOTSET, DECOR, PRODUCT
	// for add
	public Edit(View v){
		this.type = Type.add;
		this.view = v;
	}
	// for delete
	public Edit(Type type, View v, int i){
		this.type = type;
		this.view = v;
		this.index = i;
	}
	// for move up/ down
	public Edit(Type type, View v){
		this.type = type;
		this.view = v;
	}
	// for scale, rotate
	public Edit(Type type, View v, float v1, float v2){
		this.type = type;
		this.view = v;
		this.value1 = v1;
		this.value2 = v2;
	}
	// for translate
	public Edit(Type type, View v, float v1, float v2, float vY1, float vY2){
		this.type = Type.move;
		this.view = v;
		this.value1 = v1;
		this.value2 = v2;
		this.valueY1 = vY1;
		this.valueY2 = vY2;
	}
	// for change image
	public Edit(View v, Drawable drawable1, Drawable drawable2, String url1, String url2){
	    this.type = Type.changeImage;
	    this.view = v;
	    this.drawable1 = drawable1;
	    this.drawable2 = drawable2;
	    this.url1 = url1;
	    this.url2 = url2;
	}
	
	public Edit(View v, Drawable drawable1, Drawable drawable2, String url1, String url2, int type1, int type2){
        this.type = Type.changeImage;
        this.view = v;
        this.drawable1 = drawable1;
        this.drawable2 = drawable2;
        this.url1 = url1;
        this.url2 = url2;
        this.type1 = type1;
        this.type2 = type2;
    }
	
}
