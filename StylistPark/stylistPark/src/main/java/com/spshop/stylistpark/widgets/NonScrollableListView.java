package com.spshop.stylistpark.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

public class NonScrollableListView extends ListView {

	public NonScrollableListView(Context context) {
		super(context);
	}

	public NonScrollableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public NonScrollableListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_MOVE)
			return true;
		return super.dispatchTouchEvent(ev);
	}

}
