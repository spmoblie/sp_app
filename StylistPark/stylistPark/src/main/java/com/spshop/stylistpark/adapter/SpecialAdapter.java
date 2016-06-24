package com.spshop.stylistpark.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.entity.ListShowTwoEntity;
import com.spshop.stylistpark.entity.ThemeEntity;

import java.util.List;

/**
 * 活动列表适配器
 */
public class SpecialAdapter extends BaseAdapter {

	private static final String IMAGE_URL_HTTP = AppConfig.ENVIRONMENT_PRESENT_IMG_APP;

	private Context context;
	private List<ListShowTwoEntity> datas;
	private AdapterCallback adapterCallback;
	private DisplayImageOptions options;

	public SpecialAdapter(Context context, List<ListShowTwoEntity> datas, AdapterCallback adapterCallback) {
		this.context = context;
		this.datas = datas;
		this.adapterCallback = adapterCallback;
		options = AppApplication.getImageOptions(0, R.drawable.bg_img_white);
	}

	public void updateAdapter(List<ListShowTwoEntity> datas) {
		if (datas != null) {
			this.datas = datas;
			notifyDataSetChanged();
		}
	}

	/** 获得总共有多少条数据 */
	@Override
	public int getCount() {
		return datas.size();
	}

	/** 在ListView中显示的每个item内容 */
	@Override
	public Object getItem(int position) {
		return datas.get(position);
	}

	/** 返回集合中个某个元素的位置 */
	@Override
	public long getItemId(int position) {
		return position;
	}

	static class ViewHolder {

		LinearLayout left_main, right_main;
		ImageView left_logo, right_logo;
		TextView left_name, right_name;

	}

	/** 代表了ListView中的一个item对象 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.item_list_special_two, null);
			holder = new ViewHolder();
			holder.left_main = (LinearLayout) convertView.findViewById(R.id.list_special_two_left_ll_main);
			holder.left_logo = (ImageView) convertView.findViewById(R.id.list_special_two_left_iv_img);
			holder.left_name = (TextView) convertView.findViewById(R.id.home_line_sales_item_tv_name);

			holder.right_main = (LinearLayout) convertView.findViewById(R.id.list_special_two_left_ll_main);
			holder.right_logo = (ImageView) convertView.findViewById(R.id.list_special_two_left_iv_img);
			holder.right_name = (TextView) convertView.findViewById(R.id.home_line_sales_item_tv_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final ListShowTwoEntity data = datas.get(position);

		final ThemeEntity leftEn = (ThemeEntity) data.getLeftEn();
		if (leftEn != null) {
			ImageLoader.getInstance().displayImage(IMAGE_URL_HTTP + leftEn.getImgUrl(), holder.left_logo, options);
			holder.left_name.setText(leftEn.getTitle());

			holder.left_main.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					adapterCallback.setOnClick(leftEn, position, 1);
				}
			});
		}
		final ThemeEntity rightEn = (ThemeEntity) data.getRightEn();
		if (leftEn != null) {
			ImageLoader.getInstance().displayImage(IMAGE_URL_HTTP + rightEn.getImgUrl(), holder.right_logo, options);
			holder.right_name.setText(rightEn.getTitle());

			holder.right_main.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					adapterCallback.setOnClick(rightEn, position, 2);
				}
			});
		}
		return convertView;
	}

}
