package com.spshop.stylistpark.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.entity.ProductListEntity;
import com.spshop.stylistpark.utils.StringUtil;

import java.util.List;

/**
 * 商品列表GridView适配器 
 */
public class ProductGridAdapter extends BaseAdapter{
	
	private static final String IMAGE_URL_HTTP = AppConfig.ENVIRONMENT_PRESENT_IMG_APP;
	
	private Context context;
	private List<ProductListEntity> datas;
	private AdapterCallback apCallback;
    private DisplayImageOptions options;
	
	public ProductGridAdapter(Context context, List<ProductListEntity> datas, AdapterCallback callback) {
		this.context = context;
		this.datas = datas;
		this.apCallback = callback;
        options = AppApplication.getImageOptions(0, R.drawable.bg_img_white);
	}
	
	public void updateAdapter(List<ProductListEntity> datas){
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
		FrameLayout item_main;
		ImageView item_img;
		TextView item_name;
		TextView item_brand;
		TextView item_sell_price;
		TextView item_full_price;
		TextView item_discount;
	}
	
	/**代表了ListView中的一个item对象*/
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null){
			convertView = View.inflate(context, R.layout.item_grid_commodity, null);
			holder = new ViewHolder();
			holder.item_main = (FrameLayout) convertView.findViewById(R.id.product_grid_item_fl_main);
			holder.item_img = (ImageView) convertView.findViewById(R.id.product_grid_item_iv_img);
			holder.item_name = (TextView) convertView.findViewById(R.id.product_grid_item_tv_name);
			holder.item_brand = (TextView) convertView.findViewById(R.id.product_grid_item_tv_brand);
			holder.item_sell_price = (TextView) convertView.findViewById(R.id.product_grid_item_tv_sell_price);
			holder.item_full_price = (TextView) convertView.findViewById(R.id.product_grid_item_tv_full_price);
			holder.item_discount = (TextView) convertView.findViewById(R.id.product_grid_item_tv_discount);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder)convertView.getTag();
		}
		final ProductListEntity data = datas.get(position);
		// 网络图片地址
		String imageUrl = IMAGE_URL_HTTP + data.getImageUrl();
		if (!StringUtil.isNull(data.getImageUrl())) {
			ImageLoader.getInstance().displayImage(imageUrl, holder.item_img, options);
		} else {
			holder.item_img.setImageResource(R.drawable.bg_img_white);
		}
        holder.item_name.setText(data.getName()); //商品名称
		holder.item_sell_price.setText(data.getSellPrice()); //商品卖价
		
		String full_price = data.getFullPrice(); //商品原价
		if (StringUtil.isNull(full_price) || full_price.equals("0") || full_price.equals("0.00")) {
			holder.item_sell_price.setTextColor(context.getResources().getColor(R.color.text_color_content));
			holder.item_full_price.getPaint().setFlags(0);
			holder.item_full_price.setVisibility(View.GONE);
			holder.item_discount.setVisibility(View.GONE);
		} else {
			holder.item_sell_price.setTextColor(context.getResources().getColor(R.color.text_color_red_1));
			holder.item_full_price.setText(full_price);
			holder.item_full_price.setVisibility(View.VISIBLE);
			holder.item_full_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
			if (!StringUtil.isNull(data.getDiscount())) {
				holder.item_discount.setVisibility(View.VISIBLE);
				holder.item_discount.setText(data.getDiscount());
			}else {
				holder.item_discount.setVisibility(View.GONE);
			}
		}
		holder.item_main.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				apCallback.setOnClick(data, position, 0);
			}
		});
		return convertView;
	}
    
}
