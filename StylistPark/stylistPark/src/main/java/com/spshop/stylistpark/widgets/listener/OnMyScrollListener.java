package com.spshop.stylistpark.widgets.listener;

import android.widget.AbsListView;

/**
 * @author mrsimple
 */
public interface OnMyScrollListener {
	
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount);
    
}
