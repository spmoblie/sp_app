package com.spshop.stylistpark.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.spshop.stylistpark.R;
import com.spshop.stylistpark.activity.collage.ProductMenuFragment.ProductMenu;

public class ProductMenuAdapter extends AppBaseAdapter<ProductMenu> {

	int itemWidth;

	public ProductMenuAdapter(Context mContext) {
		super(mContext);
		itemWidth = 0;
	}

	public void setItemWidth(int width) {
		this.itemWidth = width;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ChildHolder holder = null;
		if (convertView == null) {
			holder = new ChildHolder();
			convertView = ((Activity) weakContext.get()).getLayoutInflater().inflate(R.layout.item_product_menu, null);
			holder.cellProductMenu_RelativeLayout = (ViewGroup) convertView.findViewById(R.id.cellProductMenu_RelativeLayout);
			holder.cellProductMenu_ImageView = (ImageView) convertView.findViewById(R.id.cellProductMenu_ImageView);
			holder.cellProductMenu_TextView = (TextView) convertView.findViewById(R.id.cellProductMenu_TextView);
			if (itemWidth != 0) {
				FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) holder.cellProductMenu_RelativeLayout.getLayoutParams();
				lp.width = itemWidth;
			}
			convertView.setTag(holder);
		} else {
			holder = (ChildHolder) convertView.getTag();
		}
		ProductMenu item = (ProductMenu) getItem(position);

		holder.cellProductMenu_ImageView.setImageResource(item.drawableIdNormal);
		holder.cellProductMenu_TextView.setText(item.name);

		return convertView;
	}

	public static class ChildHolder extends SuperHolder {
		ViewGroup cellProductMenu_RelativeLayout;
		ImageView cellProductMenu_ImageView;
		TextView cellProductMenu_TextView;
	}

}
