package com.spshop.stylistpark.utils;

import java.util.Stack;

import android.graphics.Bitmap;
import android.graphics.Point;

/**
 * Copy from Project, "GPUSample"
 */

public class FloodFill {

	public FloodFill() {
		// TODO Auto-generated constructor stub
	}
	
	public Bitmap mBitmap;
	
	/**
	 * Fills the selected pixel and all surrounding pixels of the old color with the new color.
	 * @param x
	 * @param y
	 * @param newColor
	 * @param oldColor
	 */
	
	public void floodFillScanlineStack(int x, int y, int newColor, int oldColor)
	{
		Stack<Point> stack=new Stack<Point>();
		if(oldColor == newColor) return;
		stack.clear();
	
		int w = mBitmap.getWidth();
		int h = mBitmap.getHeight();
		int y1; 
		boolean spanLeft, spanRight;
	
		stack.push(new Point(x, y));
		
		int pixeln = 0, pixelp = 0;
		while(!stack.empty())
		{
			Point pt = stack.pop();
			x = pt.x;
			y = pt.y;
			
			y1 = y;
		
			while(y1 >= 0 && checkPixel(mBitmap.getPixel(x, y1), oldColor, 20)) y1--;
			y1++;
			spanLeft = spanRight = false;
			while(y1 < h && checkPixel(mBitmap.getPixel(x, y1), oldColor, 20) )
			{
			mBitmap.setPixel(x, y1, newColor);
			if(!spanLeft){
				if(x > 0){
					if(pixeln == 0){
						pixeln = mBitmap.getPixel(x-1, y1);
					}
					if(checkPixel(pixeln, oldColor, 20)){
						stack.push(new Point(x - 1, y1));
				spanLeft = true;
					}
				}
			}else{
				if(x > 0){
					if(pixeln == 0){
						pixeln = mBitmap.getPixel(x-1, y1);
					}
					if(!checkPixel(pixeln, oldColor, 20)){
				spanLeft = false;
					}
				}
			}
			pixeln = 0;
			if(!spanRight){
				if(x < w - 1){
					if(pixelp == 0){
						pixelp = mBitmap.getPixel(x+1, y1);
					}
					if(checkPixel(pixelp, oldColor, 20)){
						stack.push(new Point(x + 1, y1));
						spanRight = true;
					}
				}
			}else{
				if(x < w - 1){
					if(pixelp == 0){
						pixelp = mBitmap.getPixel(x+1, y1);
					}
					if(!checkPixel(pixelp, oldColor, 20)){
						spanRight = false;
					}
				}
			}
			pixelp = 0;
			y1++;
			}
		}
	}
	
	/**
	 * check color of the pixel within range of color, not real
	 * @param pixel
	 * @param color
	 * @param range
	 */
	
	public boolean checkPixel(int pixel, int color, int range){
//	    int pa = (pixel >> 24) & 0xFF;
//		int pr = (pixel >> 16) & 0xFF;
//		int pg = (pixel >> 8) & 0xFF;
		int pb = (pixel >> 0) & 0xFF;
//		int a = (color >> 24) & 0xFF;
//		int r = (color >> 16) & 0xFF;
//		int g = (color >> 8) & 0xFF;
		int b = (color >> 0) & 0xFF;
//		if(pr < r + range){
//			return true;
//		}
//		if(pg < g + range){
//			return true;	
//				}
		if(pb < b + range){
			return true;
		}
		return false; // equal
	}
	
}
