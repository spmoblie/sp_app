package com.spshop.stylistpark.widgets;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class IViewPager extends ViewPager {

	private boolean isCanScroll = true;

	public IViewPager(Context context) {
		super(context);
	}

	public IViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setScanScroll(boolean isCanScroll){
		this.isCanScroll = isCanScroll;
	}

	@Override
	public void scrollTo(int x, int y){
		if (isCanScroll){
			super.scrollTo(x, y);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return isCanScroll && super.onTouchEvent(event);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		return isCanScroll && super.onInterceptTouchEvent(event);
	}

}
