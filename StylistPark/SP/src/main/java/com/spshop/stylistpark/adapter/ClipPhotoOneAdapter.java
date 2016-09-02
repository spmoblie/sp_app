package com.spshop.stylistpark.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Thumbnails;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;

import com.spshop.stylistpark.entity.ClipPhotoEntity;
import com.spshop.stylistpark.widgets.PhotoGridItem;

/**
 * 选择相片适配器
 */
public class ClipPhotoOneAdapter extends BaseAdapter {

	private Context context;
	private ClipPhotoEntity aibum;

	public ClipPhotoOneAdapter(Context context, ClipPhotoEntity aibum) {
		this.context = context;
		this.aibum = aibum;
	}

	@Override
	public int getCount() {
		return aibum.getBitList().size();
	}

	@Override
	public Object getItem(int position) {
		return aibum.getBitList().get(getCount() - 1 - position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		PhotoGridItem item;
		if (convertView == null) {
			item = new PhotoGridItem(context);
			item.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		} else {
			item = (PhotoGridItem) convertView;
		}

		// 通过ID 加载缩略图
		Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(context.getContentResolver(),
				aibum.getBitList().get(getCount() - 1 - position).getPhotoId(), Thumbnails.MICRO_KIND, null);
		item.SetBitmap(bitmap);
		boolean flag = aibum.getBitList().get(getCount() - 1 - position).isSelect();
		item.setChecked(flag);
		return item;
	}

}
