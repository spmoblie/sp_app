package com.spshop.stylistpark.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * scrollview中内嵌listview的简单实现(不确定Item个数)
 */
public class ScrollListView extends ListView {
	
	public ScrollListView(Context context) {
		super(context);
	}

	public ScrollListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ScrollListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	* Integer.MAX_VALUE >> 2,如果不设置，系统默认设置是显示两条
	*/
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
	
}
