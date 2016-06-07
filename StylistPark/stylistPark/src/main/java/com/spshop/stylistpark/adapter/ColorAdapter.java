package com.spshop.stylistpark.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.spshop.stylistpark.R;
import com.spshop.stylistpark.entity.FilterColor;
import com.spshop.stylistpark.widgets.SquareImageView;

public class ColorAdapter extends AppBaseAdapter<FilterColor> {

	FilterColor selectedColor;

	public ColorAdapter(Context mContext) {
		super(mContext);
	}

	public void setSelectedColor(FilterColor selectedColor) {
		this.selectedColor = selectedColor;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ChildHolder holder = null;
		if (convertView == null) {
			holder = new ChildHolder();
			convertView = ((Activity) weakContext.get()).getLayoutInflater().inflate(R.layout.item_grid_color, null);
			holder.color_SquareImageView = (SquareImageView) convertView.findViewById(R.id.color_SquareImageView);
			holder.color_selectedImageView = (SquareImageView) convertView.findViewById(R.id.color_selectedImageView);
			holder.color_SquareImageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (onItemCellClickListener != null)
						onItemCellClickListener.onItemCellClickListener(v.getTag());
				}
			});
			convertView.setTag(holder);
		} else {
			holder = (ChildHolder) convertView.getTag();
		}
		FilterColor item = (FilterColor) getItem(position);

		holder.color_SquareImageView.setTag(item);
		if (item.getDrawableId() != -1) {
			holder.color_SquareImageView.setBackgroundResource(item.getDrawableId());
		} else if (item.getColorId() != -1) {
			holder.color_SquareImageView.setBackgroundColor(weakContext.get().getResources().getColor(item.getColorId()));
		}
		if (selectedColor != null) {
			if (item.getName().equals(selectedColor.getName())) {
				holder.color_selectedImageView.setImageResource(R.drawable.generator_filter_color_select);
			}
		}

		return convertView;
	}

	public static class ChildHolder extends SuperHolder {

		SquareImageView color_SquareImageView;
		SquareImageView color_selectedImageView;
	}

}
