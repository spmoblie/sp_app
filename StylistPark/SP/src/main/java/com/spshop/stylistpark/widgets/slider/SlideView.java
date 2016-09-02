package com.spshop.stylistpark.widgets.slider;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.spshop.stylistpark.R;

public class SlideView extends LinearLayout {
	
	private static final int TAN = 2;
	private int mHolderWidth = 80;
	private float mLastX = 0;
	private float mLastY = 0;
	private LinearLayout mViewContent;
	private Scroller mScroller;

	private SlideView(Context context) {
		super(context);
		initView(null, null, null);
	}

	public SlideView(Context context, Resources resources, View content) {
		super(context);
		initView(context, resources, content);
	}

	private SlideView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(null, null, null);
	}

	private void initView(Context context, Resources resources, View content) {
		setOrientation(LinearLayout.HORIZONTAL);
		mScroller = new Scroller(context);
		LayoutInflater.from(context).inflate(resources.getLayout(R.layout.item_list_slider_view_merge), this);
		mViewContent = (LinearLayout) findViewById(R.id.list_slider_view_ll_content);
		mHolderWidth = Math.round(TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, mHolderWidth, getResources().getDisplayMetrics()));
		if (content != null) {
			mViewContent.addView(content);
		}
	}

	public void shrink() {
		int offset = getScrollX();
		if (offset == 0) {
			return;
		}
		scrollTo(0, 0);
	}

	public void setContentView(View view) {
		if (mViewContent != null) {
			mViewContent.addView(view);
		}
	}

	public void reset() {
		int offset = getScrollX();
		if (offset == 0) {
			return;
		}
		smoothScrollTo(0, 0);
	}

	public void adjust(boolean left) {
		int offset = getScrollX();
		if (offset == 0) {
			return;
		}
		if (offset < 20) {
			this.smoothScrollTo(0, 0);
		} else if (offset < mHolderWidth - 20) {
			if (left) {
				this.smoothScrollTo(mHolderWidth, 0);
			} else {
				this.smoothScrollTo(0, 0);
			}
		} else {
			this.smoothScrollTo(mHolderWidth, 0);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE:
			float x = event.getX();
			float y = event.getY();
			float deltaX = x - mLastX;
			float delatY = y - mLastY;
			mLastX = x;
			mLastY = y;
			if (Math.abs(deltaX) < Math.abs(delatY) * TAN) {
				break;
			}
			if (deltaX != 0) {
				float newScrollX = getScrollX() - deltaX;
				if (newScrollX < 0) {
					newScrollX = 0;
				} else if (newScrollX > mHolderWidth) {
					newScrollX = mHolderWidth;
				}
				this.scrollTo((int) newScrollX, 0);
			}
			break;
		}
		return super.onTouchEvent(event);
	}

	private void smoothScrollTo(int destX, int destY) {
		int scrollX = getScrollX();
		int delta = destX - scrollX;
		mScroller.startScroll(scrollX, 0, delta, 0, Math.abs(delta) * 3);
		invalidate();
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		}
	}
}

