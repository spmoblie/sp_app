package com.spshop.stylistpark.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.entity.BrandEntity;
import com.spshop.stylistpark.entity.IndexDisplay;

public class BrandIndexDisplayAdapter extends IndexDisplayAdapter {

	private static final String IMAGE_URL_HTTP = AppConfig.ENVIRONMENT_PRESENT_IMG_APP;
	private DisplayImageOptions options;
	OnIndexDisplayItemClick onIndexDisplayItemClick;

	public BrandIndexDisplayAdapter(Context mContext) {
		super(mContext);
		options = AppApplication.getImageOptions(0, 0, true);
	}

	public void setOnIndexDisplayItemClick(OnIndexDisplayItemClick onIndexDisplayItemClick) {
		this.onIndexDisplayItemClick = onIndexDisplayItemClick;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ChildHolder holder = null;
		if (convertView == null) {
			holder = new ChildHolder();
			convertView = ((Activity) weakContext.get()).getLayoutInflater().inflate(R.layout.item_brand_index_display, null);

			holder.brand_indexLayout = (ViewGroup) convertView.findViewById(R.id.brand_indexLayout);
			holder.brand_logoImageView = (ImageView) convertView.findViewById(R.id.brand_iv_logo);
			holder.brand_indexNameTextView = (TextView) convertView.findViewById(R.id.brand_indexNameTextView);
			holder.brand_nameTextView = (TextView) convertView.findViewById(R.id.brand_nameTextView);
			holder.brand_dividerImageView = (ImageView) convertView.findViewById(R.id.brand_dividerImageView);

			holder.brand_itemLayout = (ViewGroup) convertView.findViewById(R.id.brand_itemLayout);
			holder.brand_itemLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (onIndexDisplayItemClick != null)
						onIndexDisplayItemClick.onIndexDisplayItemClick((IndexDisplay) v.getTag());
				}
			});
			convertView.setTag(holder);
		} else {
			holder = (ChildHolder) convertView.getTag();
		}
		IndexDisplay item = getItem(position);

		if (item != null) {
			holder.obj = item;
			holder.brand_itemLayout.setTag(item);
			if (isFirst(position)) {
				String indexStr = getIndexChar(position);
				holder.brand_indexLayout.setVisibility(View.VISIBLE);
				holder.brand_indexNameTextView.setText(indexStr);
			} else {
				holder.brand_indexLayout.setVisibility(View.GONE);
			}
			/*if (isLast(position)) {
				holder.brand_dividerImageView.setVisibility(View.GONE);
			} else {
				holder.brand_dividerImageView.setVisibility(View.VISIBLE);
			}*/
			ImageLoader.getInstance().displayImage(IMAGE_URL_HTTP +
					((BrandEntity) item).getDefineUrl(), holder.brand_logoImageView, options);
			holder.brand_nameTextView.setText(((BrandEntity) item).getName());
		}
		return convertView;
	}

	public static class ChildHolder extends SuperHolder {
		ViewGroup brand_indexLayout;
		TextView brand_indexNameTextView;

		ViewGroup brand_itemLayout;
		ImageView brand_logoImageView;
		TextView brand_nameTextView;
		ImageView brand_dividerImageView;
	}

}
