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
import com.spshop.stylistpark.entity.SelectListEntity;
import com.spshop.stylistpark.utils.StringUtil;

import java.util.List;

/**
 * 选择列表适配器
 */
public class SelectListAdapter extends BaseAdapter{
	
	private static final String IMAGE_URL_HTTP = AppConfig.ENVIRONMENT_PRESENT_IMG_APP;
	public static final int DATA_TYPE_1 = 1; //ScreenListActivity
	public static final int DATA_TYPE_2 = 2; //ScreenListActivity --> SelectListActivity 
	public static final int DATA_TYPE_3 = 3; //ProductListActivity
	public static final int DATA_TYPE_4 = 4; //ProductListActivity --> SelectListActivity
	public static final int DATA_TYPE_5 = 5; //PersonalActivity --> SelectListActivity
	public static final int DATA_TYPE_6 = 6; //PostOrderActivity --> SelectListActivity
	public static final int DATA_TYPE_7 = 7; //ShowListHeadActivity --> SelectListActivity

	private Context context;
	private SelectListEntity selectData;
	private List<SelectListEntity> datas;
	private AdapterCallback apCallback;
	private DisplayImageOptions options;
	private int dataType;
	
	public SelectListAdapter(Context context, List<SelectListEntity> datas, 
			AdapterCallback apCallback, int dataType) {
		this.context = context;
		this.datas = datas;
		this.apCallback = apCallback;
		this.dataType = dataType;
	}
	
	public SelectListAdapter(Context context, SelectListEntity selectEn, List<SelectListEntity> datas, 
			AdapterCallback callback, int dataType) {
		this.context = context;
		this.selectData = selectEn;
		this.datas = datas;
		this.apCallback = callback;
		this.dataType = dataType;
		options = AppApplication.getImageOptions(0, R.drawable.bg_img_icon_120, true);
	}
	
	public void updateAdapter(List<SelectListEntity> datas, int dataType){
		if (datas != null) {
			this.datas = datas;
			this.dataType = dataType;
			notifyDataSetChanged();
		}
	}

	/**获得总共有多少条数据*/
	@Override
	public int getCount() {
		return datas.size();
	}

	/**在ListView中显示的每个item内容*/
	@Override
	public Object getItem(int position) {
		return datas.get(position);
	}

	/**返回集合中个某个元素的位置*/
	@Override
	public long getItemId(int position) {
		return position;
	}

	static class ViewHolder{
		
		LinearLayout ll_item;
		TextView tv_item_name, tv_select_name;
		ImageView iv_logo, iv_go, iv_line_1, iv_line_2;
		
	}
	
	/**代表了ListView中的一个item对象*/
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null){
			convertView = View.inflate(context, R.layout.item_list_select, null);
			
			holder = new ViewHolder();
			holder.ll_item = (LinearLayout) convertView.findViewById(R.id.list_item_select_ll);
			holder.tv_item_name = (TextView) convertView.findViewById(R.id.list_item_select_tv_item_name);
			holder.tv_select_name = (TextView) convertView.findViewById(R.id.list_item_select_tv_select_name);
			holder.iv_logo = (ImageView) convertView.findViewById(R.id.list_item_select_iv_logo);
			holder.iv_go = (ImageView) convertView.findViewById(R.id.list_item_select_iv_go);
			holder.iv_line_1 = (ImageView) convertView.findViewById(R.id.list_item_select_iv_line_1);
			holder.iv_line_2 = (ImageView) convertView.findViewById(R.id.list_item_select_iv_line_2);
			
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder)convertView.getTag();
		}
		final SelectListEntity data = datas.get(position);
		switch (dataType) {
		case DATA_TYPE_1: //ScreenListActivity
			holder.tv_item_name.setText(data.getTypeName());
			holder.iv_logo.setVisibility(View.GONE);
			holder.iv_go.setVisibility(View.VISIBLE);
			holder.tv_select_name.setVisibility(View.VISIBLE);
			SelectListEntity selectEn = data.getSelectEn();
			if (selectEn != null) {
				holder.tv_select_name.setText(selectEn.getChildShowName());
				holder.tv_select_name.setTextColor(context.getResources().getColor(R.color.text_color_red_1));
			}else {
				holder.tv_select_name.setText(R.string.all);
				holder.tv_select_name.setTextColor(context.getResources().getColor(R.color.text_color_content));
			}
			break;
		case DATA_TYPE_2: //ScreenListActivity --> SelectListActivity
		case DATA_TYPE_4: //ProductListActivity --> SelectListActivity
		case DATA_TYPE_7: //ShowListHeadActivity --> SelectListActivity
			itemChangeTextColor(holder, data);
			break;
		case DATA_TYPE_3: //ProductListActivity
			holder.tv_item_name.setText(data.getChildShowName());
			holder.tv_select_name.setVisibility(View.GONE);
			holder.iv_go.setVisibility(View.GONE);
			holder.iv_logo.setVisibility(View.GONE);
			holder.ll_item.setBackgroundColor(context.getResources().getColor(R.color.ui_bg_color_gray));
			break;
		case DATA_TYPE_5: //PersonalActivity --> SelectListActivity
		case DATA_TYPE_6: //PostOrderActivity --> SelectListActivity
			itemChangeImgTick(holder, data);
			break;
		}
		if (position == getCount() - 1) {
			holder.iv_line_1.setVisibility(View.GONE);
			holder.iv_line_2.setVisibility(View.VISIBLE);
		}else {
			holder.iv_line_1.setVisibility(View.VISIBLE);
			holder.iv_line_2.setVisibility(View.GONE);
		}
		holder.ll_item.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (apCallback != null) {
					apCallback.setOnClick(data, position, dataType);
				}
			}
		});
		return convertView;
	}

	/**
	 * Item选中后显示勾选
	 */
	private void itemChangeImgTick(ViewHolder holder, final SelectListEntity data) {
		holder.iv_go.setImageDrawable(context.getResources().getDrawable(R.drawable.topbar_icon_tick));
		if (selectData != null && selectData.getChildId() == data.getChildId()) {
			holder.iv_go.setVisibility(View.VISIBLE);
		}else {
			holder.iv_go.setVisibility(View.GONE);
		}
		holder.tv_item_name.setText(data.getChildShowName());
		holder.tv_select_name.setVisibility(View.GONE);
		
		if (!StringUtil.isNull(data.getChildLogoUrl())) {
			holder.iv_logo.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(IMAGE_URL_HTTP + data.getChildLogoUrl(), holder.iv_logo, options);
		}else {
			holder.iv_logo.setVisibility(View.GONE);
			holder.iv_logo.setImageDrawable(context.getResources().getDrawable(R.drawable.bg_img_icon_120));
		}
	}

	/**
	 * Item选中后字体变红色
	 */
	private void itemChangeTextColor(ViewHolder holder, final SelectListEntity data) {
		if (selectData != null && selectData.getChildId() == data.getChildId()) {
			holder.tv_item_name.setTextColor(context.getResources().getColor(R.color.text_color_red_1));
		}else {
			holder.tv_item_name.setTextColor(context.getResources().getColor(R.color.text_color_content));
		}
		holder.tv_item_name.setText(data.getChildShowName());
		holder.tv_select_name.setVisibility(View.GONE);
		holder.iv_go.setVisibility(View.GONE);
		
		if (!StringUtil.isNull(data.getChildLogoUrl())) {
			holder.iv_logo.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(IMAGE_URL_HTTP + data.getChildLogoUrl(), holder.iv_logo, options);
		}else {
			holder.iv_logo.setVisibility(View.GONE);
			holder.iv_go.setImageDrawable(context.getResources().getDrawable(R.drawable.bg_img_icon_120));
		}
	}

}
