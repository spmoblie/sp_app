package com.spshop.stylistpark.widgets.slider;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

public class SliderListView extends ListView {
	
	private float mX = 0;
	private float mY = 0;
	private int mPosition = -1;
	private boolean isSlider = false;
	private SlideView mFocusedItemView;

	public SliderListView(Context context) {
		super(context);
	}

	public SliderListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SliderListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			isSlider = false;
			mX = x;
			mY = y;
			int position = pointToPosition((int) x, (int) y);
			if (mPosition != position) {
				mPosition = position;
				if (mFocusedItemView != null) {
					mFocusedItemView.reset();
				}
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (mPosition != -1) {
				if (Math.abs(mY - y) < 30 && Math.abs(mX - x) > 20) {
					int first = this.getFirstVisiblePosition();
					int index = mPosition - first;
					mFocusedItemView = (SlideView) getChildAt(index);
					// mFocusedItemView = (SlideView)
					// getItemAtPosition(mPosition);
					mFocusedItemView.onTouchEvent(event);
					isSlider = true;
					return true;
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			if (isSlider) {
				isSlider = false;
				if (mFocusedItemView != null) {
					mFocusedItemView.adjust(mX - x > 0);
					return true;
				}
			}
			break;
		}
		return super.onTouchEvent(event);
	}
	
	public void reset(){
		if (mFocusedItemView != null) {
			mFocusedItemView.reset();
		}
	}
}
