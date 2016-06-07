package com.spshop.stylistpark.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.entity.CategoryListEntity;


/**
 * 商品分类ListView适配器 
 */
public class CategoryRightListAdapter extends BaseAdapter{
	
	private static final String IMAGE_URL_HTTP = AppConfig.ENVIRONMENT_PRESENT_IMG_APP;
	
	private Context context;
	private List<CategoryListEntity> datas;
	private AdapterCallback callback;
	private DisplayImageOptions options;
	
	public CategoryRightListAdapter(Context context, List<CategoryListEntity> datas, AdapterCallback callback) {
		this.context = context;
		this.datas = datas;
		this.callback = callback;
		options = AppApplication.getImageOptions(0, R.drawable.bg_img_icon_120);
	}
	
	public void updateAdapter(List<CategoryListEntity> datas){
		if (datas != null) {
			this.datas = datas;
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
		
		RelativeLayout rl_main;
		ImageView iv_brand;
		
	}
	
	/**代表了ListView中的一个item对象*/
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null){
			convertView = View.inflate(context, R.layout.item_list_category_right, null);
			holder = new ViewHolder();
			holder.rl_main = (RelativeLayout) convertView.findViewById(R.id.list_item_category_right_ll_main);
			holder.iv_brand = (ImageView) convertView.findViewById(R.id.list_item_category_right_iv);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder)convertView.getTag();
		}
		final CategoryListEntity data = datas.get(position);
		
		ImageLoader.getInstance().displayImage(IMAGE_URL_HTTP + data.getImageUrl(), holder.iv_brand, options);
		holder.rl_main.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				callback.setOnClick(data, position, 1);
			}
		});
		return convertView;
	}

}
