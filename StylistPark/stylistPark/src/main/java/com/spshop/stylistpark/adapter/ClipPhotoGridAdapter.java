package com.spshop.stylistpark.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Thumbnails;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spshop.stylistpark.R;
import com.spshop.stylistpark.entity.ClipPhotoEntity;

/**
 * 选择相册适配器
 */
public class ClipPhotoGridAdapter extends BaseAdapter {

	private List<ClipPhotoEntity> aibumList;
	private Context context;

	public ClipPhotoGridAdapter(List<ClipPhotoEntity> list, Context context) {
		this.aibumList = list;
		this.context = context;
	}

	@Override
	public int getCount() {
		return aibumList.size();
	}

	@Override
	public Object getItem(int position) {
		return aibumList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	static class ViewHolder {
		ImageView iv;
		TextView tv;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.item_list_photo_select, null);
			holder = new ViewHolder();
			holder.iv = (ImageView) convertView.findViewById(R.id.photo_item_image);
			holder.tv = (TextView) convertView.findViewById(R.id.photo_item_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		/** 通过ID 获取缩略图 */
		Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(context .getContentResolver(), 
				aibumList.get(position).getBitmap(), Thumbnails.MICRO_KIND, null);
		holder.iv.setImageBitmap(bitmap);
		holder.tv.setText(aibumList.get(position).getName() + " ( " + aibumList.get(position).getCount() + " )");
		return convertView;
	}

}
