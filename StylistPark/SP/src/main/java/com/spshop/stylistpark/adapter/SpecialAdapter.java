package com.spshop.stylistpark.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
 * 专题列表适配器
 */
public class SpecialAdapter extends BaseAdapter {

	private static final String IMAGE_URL_HTTP = AppConfig.ENVIRONMENT_PRESENT_IMG_APP;
	public static final int TYPE_SELECT_LEFT = 1;
	public static final int TYPE_SELECT_RIGHT = 2;

	private Context context;
	private List<ListShowTwoEntity> datas;
	private AdapterCallback adapterCallback;
	private DisplayImageOptions options, headOptions;
	private RelativeLayout.LayoutParams lp;

	public SpecialAdapter(Context context, List<ListShowTwoEntity> datas, AdapterCallback adapterCallback) {
		this.context = context;
		this.datas = datas;
		this.adapterCallback = adapterCallback;
		options = AppApplication.getDefaultImageOptions();
		headOptions = AppApplication.getHeadImageOptions();
		lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		lp.width = (AppApplication.screenWidth - 38) / 2;
		lp.height = (AppApplication.screenWidth - 38) / 2;
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
		ImageView left_logo, left_head, right_logo, right_head;
		TextView left_title, left_nick, left_click, right_title, right_nick, right_click;

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
			holder.left_head = (ImageView) convertView.findViewById(R.id.list_special_two_left_iv_head);
			holder.left_title = (TextView) convertView.findViewById(R.id.list_special_two_left_tv_title);
			holder.left_nick = (TextView) convertView.findViewById(R.id.list_special_two_left_tv_nick);
			holder.left_click = (TextView) convertView.findViewById(R.id.list_special_two_left_tv_click);

			holder.right_main = (LinearLayout) convertView.findViewById(R.id.list_special_two_right_ll_main);
			holder.right_logo = (ImageView) convertView.findViewById(R.id.list_special_two_right_iv_img);
			holder.right_head = (ImageView) convertView.findViewById(R.id.list_special_two_right_iv_head);
			holder.right_title = (TextView) convertView.findViewById(R.id.list_special_two_right_tv_title);
			holder.right_nick = (TextView) convertView.findViewById(R.id.list_special_two_right_tv_nick);
			holder.right_click = (TextView) convertView.findViewById(R.id.list_special_two_right_tv_click);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final ListShowTwoEntity data = datas.get(position);

		final ThemeEntity leftEn = (ThemeEntity) data.getLeftEn();
		holder.left_logo.setLayoutParams(lp);
		if (leftEn != null) {
			ImageLoader.getInstance().displayImage(IMAGE_URL_HTTP + leftEn.getImgUrl(), holder.left_logo, options);
			ImageLoader.getInstance().displayImage(IMAGE_URL_HTTP + leftEn.getMebUrl(), holder.left_head, headOptions);
			holder.left_title.setText(leftEn.getTitle());
			holder.left_nick.setText(leftEn.getMebName());
			holder.left_click.setText(String.valueOf(leftEn.getClickNum()));
			holder.left_main.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					adapterCallback.setOnClick(leftEn, position, TYPE_SELECT_LEFT);
				}
			});
		}
		final ThemeEntity rightEn = (ThemeEntity) data.getRightEn();
		holder.right_logo.setLayoutParams(lp);
		if (rightEn != null) {
			holder.right_main.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(IMAGE_URL_HTTP + rightEn.getImgUrl(), holder.right_logo, options);
			ImageLoader.getInstance().displayImage(IMAGE_URL_HTTP + rightEn.getMebUrl(), holder.right_head, headOptions);
			holder.right_title.setText(rightEn.getTitle());
			holder.right_nick.setText(rightEn.getMebName());
			holder.right_click.setText(String.valueOf(rightEn.getClickNum()));
			holder.right_main.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					adapterCallback.setOnClick(rightEn, position, TYPE_SELECT_RIGHT);
				}
			});
		} else {
			holder.right_main.setVisibility(View.INVISIBLE);
		}
		return convertView;
	}

}
