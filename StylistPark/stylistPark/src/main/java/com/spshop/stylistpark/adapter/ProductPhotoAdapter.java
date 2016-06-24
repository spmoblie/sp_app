package com.spshop.stylistpark.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.R;

import java.util.List;

public class ProductPhotoAdapter extends AppBaseAdapter<List<String>> {

	private static final String IMAGE_URL_HTTP = AppConfig.ENVIRONMENT_PRESENT_IMG_APP;

	private DisplayImageOptions options;

	public ProductPhotoAdapter(Context mContext) {
		super(mContext);
		options = AppApplication.getImageOptions(0, R.drawable.bg_img_white);
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ChildHolder holder = null;
		if (convertView == null) {
			holder = new ChildHolder();
			convertView = ((Activity) weakContext.get()).getLayoutInflater().inflate(R.layout.item_grid_product_photo, null);
			holder.productPhoto_ImageView = (ImageView) convertView.findViewById(R.id.productPhoto_ImageView);
			holder.productPhoto_dummyImageView = (ImageView) convertView.findViewById(R.id.productPhoto_dummyImageView);
			holder.productPhoto_ImageView.setOnClickListener(new OnClickListener() {

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
		List<String> item = (List<String>) getItem(position);

		holder.productPhoto_ImageView.setTag(item);
		if (item != null) {
			holder.productPhoto_dummyImageView.setVisibility(View.GONE);
			holder.productPhoto_ImageView.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(IMAGE_URL_HTTP + item.get(1), holder.productPhoto_ImageView, options);
		} else {
			holder.productPhoto_ImageView.setVisibility(View.GONE);
			holder.productPhoto_dummyImageView.setVisibility(View.VISIBLE);
		}
		return convertView;
	}

	public static class ChildHolder extends SuperHolder {

		ImageView productPhoto_ImageView;
		ImageView productPhoto_dummyImageView;
	}

}
