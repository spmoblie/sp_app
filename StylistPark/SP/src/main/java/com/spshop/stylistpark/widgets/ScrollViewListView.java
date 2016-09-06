package com.spshop.stylistpark.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;


/**
 * scrollview中内嵌listview自适应高度的简单实现(确定Item个数)
 * @author Administrator
 */
public class ScrollViewListView extends LinearLayout{
	
	@SuppressWarnings("unused")
	private MyOnItemClickListener onItemClickListener;
	private BaseAdapter adapter;
	boolean footerViewAttached = false;
	private View footerview;
	
	/**
	* 通知更新listview
	*/
	@SuppressWarnings("deprecation")
	public void notifyChange() {
		int count = getChildCount();
		if (footerViewAttached) {
			count--;
		}
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		for (int i = count; i < adapter.getCount(); i++) {
			final int index = i;
			final LinearLayout layout = new LinearLayout(getContext());
			layout.setLayoutParams(params);
			layout.setOrientation(VERTICAL);
			View v = adapter.getView(i, null, null);
//			v.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					if (onItemClickListener != null) {
//						onItemClickListener.onItemClick(ScrollViewListView.this, layout, index,
//						adapter.getItem(index));
//					}
//				}
//			});
			ImageView imageView = new ImageView(getContext());
//			imageView.setBackgroundResource(R.drawable.divider_list);
			imageView.setLayoutParams(params);
			layout.addView(v);
			layout.addView(imageView);
			addView(layout, index);
		}
	}
	
	public ScrollViewListView(Context context) {
		super(context);
		initAttr(null);
	}
	
	public ScrollViewListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initAttr(attrs);
	}
	
	public void initAttr(AttributeSet attrs) {
		setOrientation(VERTICAL);
	}
	
	/**
	* 初始化footerview
	*
	* @param footerView
	*/
	public void initFooterView(final View footerView) {
		this.footerview = footerView;
	}
	
	/**
	* 设置footerView监听事件
	*
	* @param onClickListener
	*/
	public void setFooterViewListener(OnClickListener onClickListener) {
		this.footerview.setOnClickListener(onClickListener);
	}
	
	public BaseAdapter getAdapter() {
		return adapter;
	}
	
	/**
	* 设置adapter并模拟listview添加????数据
	*
	* @param adpater
	*/
	public void setAdapter(BaseAdapter adpater) {
		this.adapter = adpater;
		removeAllViews();
		if (footerViewAttached)
			addView(footerview);
		notifyChange();
	}
	
	/**
	* 设置条目监听事件
	*
	* @param onClickListener
	*/
	public void setOnItemClickListener(MyOnItemClickListener onClickListener) {
		this.onItemClickListener = onClickListener;
	}
	
	/**
	* 没有下一页了
	*/
	public void noMorePages() {
		if (footerview != null && footerViewAttached) {
			removeView(footerview);
			footerViewAttached = false;
		}
	}
	
	/**
	* 可能还有下一??
	*/
	public void mayHaveMorePages() {
		if (!footerViewAttached && footerview != null) {
			addView(footerview);
			footerViewAttached = true;
		}
	}
	
	public static interface MyOnItemClickListener {
		public void onItemClick(ViewGroup parent, View view, int position, Object o);
	}
	
}