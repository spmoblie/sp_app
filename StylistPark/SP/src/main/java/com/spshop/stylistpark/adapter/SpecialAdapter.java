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
import com.spshop.stylistpark.entity.ThemeEntity;

import java.util.List;

/**
 * 专题列表适配器
 */
public class SpecialAdapter extends BaseAdapter {

	private static final String IMAGE_URL_HTTP = AppConfig.ENVIRONMENT_PRESENT_IMG_APP;

	private Context context;
	private List<ThemeEntity> datas;
	private AdapterCallback adapterCallback;
	private DisplayImageOptions options, headOptions;
	private LinearLayout.LayoutParams lp;

	public SpecialAdapter(Context context, List<ThemeEntity> datas, AdapterCallback adapterCallback) {
		this.context = context;
		this.datas = datas;
		this.adapterCallback = adapterCallback;
		options = AppApplication.getDefaultImageOptions();
		headOptions = AppApplication.getHeadImageOptions();
		lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		int newWidth = (AppApplication.screenWidth - 20);
		lp.width = newWidth;
		lp.height = newWidth * 405 / 720; //标准视频宽高比
		lp.setMargins(0, 10, 0, 0);
	}

	public void updateAdapter(List<ThemeEntity> datas) {
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

		LinearLayout ll_main;
		ImageView iv_line, iv_logo, iv_head;
		TextView tv_title, tv_nick, tv_click;

	}

	/** 代表了ListView中的一个item对象 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.item_list_special, null);
			holder = new ViewHolder();
			holder.ll_main = (LinearLayout) convertView.findViewById(R.id.list_special_ll_main);
			holder.iv_line = (ImageView) convertView.findViewById(R.id.list_special_iv_line);
			holder.iv_logo = (ImageView) convertView.findViewById(R.id.list_special_iv_img);
			holder.iv_head = (ImageView) convertView.findViewById(R.id.list_special_iv_head);
			holder.tv_title = (TextView) convertView.findViewById(R.id.list_special_tv_title);
			holder.tv_nick = (TextView) convertView.findViewById(R.id.list_special_tv_nick);
			holder.tv_click = (TextView) convertView.findViewById(R.id.list_special_tv_click);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final ThemeEntity data = datas.get(position);

		if (position == 0) {
			holder.iv_line.setVisibility(View.GONE);
		} else {
			holder.iv_line.setVisibility(View.VISIBLE);
		}

		holder.iv_logo.setLayoutParams(lp);

		ImageLoader.getInstance().displayImage(IMAGE_URL_HTTP + data.getImgUrl(), holder.iv_logo, options);
		ImageLoader.getInstance().displayImage(data.getMebUrl(), holder.iv_head, headOptions);
		holder.tv_title.setText(data.getTitle());
		holder.tv_nick.setText(data.getMebName());
		holder.tv_click.setText(String.valueOf(data.getClickNum()));
		holder.ll_main.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				adapterCallback.setOnClick(data, position, 1);
			}
		});
		return convertView;
	}

}
