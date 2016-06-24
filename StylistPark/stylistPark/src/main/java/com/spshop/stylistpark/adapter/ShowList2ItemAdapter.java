package com.spshop.stylistpark.adapter;

import android.content.Context;
import android.graphics.Paint;
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
import com.spshop.stylistpark.entity.ProductListEntity;
import com.spshop.stylistpark.utils.LangCurrTools;
import com.spshop.stylistpark.utils.StringUtil;

import java.util.List;

public class ShowList2ItemAdapter extends BaseAdapter{
	
	private static final String IMAGE_URL_HTTP = AppConfig.ENVIRONMENT_PRESENT_IMG_APP;
	
	private Context context;
	private List<ListShowTwoEntity> datas;
	private AdapterCallback apCallback;
    private DisplayImageOptions options;
	private String currStr;
	
	public ShowList2ItemAdapter(Context context, List<ListShowTwoEntity> datas, AdapterCallback callback) {
		this.context = context;
		this.datas = datas;
		this.apCallback = callback;
		currStr = LangCurrTools.getCurrencyValue(context);
        options = AppApplication.getImageOptions(0, R.drawable.bg_img_white);
	}
	
	public void updateAdapter(List<ListShowTwoEntity> datas){
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
		LinearLayout item_main, left_main, right_main;
		ImageView left_img, right_img;
		TextView left_name, left_brand, left_sell_price, left_full_price, left_discount;
		TextView right_name, right_brand, right_sell_price, right_full_price, right_discount;
	}
	
	/**代表了ListView中的一个item对象*/
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null){
			convertView = View.inflate(context, R.layout.item_list_commodity_two, null);
			
			holder = new ViewHolder();
			holder.item_main = (LinearLayout) convertView.findViewById(R.id.list_commodity_two_ll_main);
			holder.left_main = (LinearLayout) convertView.findViewById(R.id.list_commodity_two_left_ll_main);
			holder.left_img = (ImageView) convertView.findViewById(R.id.list_commodity_two_left_iv_img);
			holder.left_name = (TextView) convertView.findViewById(R.id.list_commodity_two_left_tv_name);
			holder.left_brand = (TextView) convertView.findViewById(R.id.list_commodity_two_left_tv_brand);
			holder.left_sell_price = (TextView) convertView.findViewById(R.id.list_commodity_two_left_tv_sell_price);
			holder.left_full_price = (TextView) convertView.findViewById(R.id.list_commodity_two_left_tv_full_price);
			holder.left_discount = (TextView) convertView.findViewById(R.id.list_commodity_two_left_tv_discount);
			holder.right_main = (LinearLayout) convertView.findViewById(R.id.list_commodity_two_right_ll_main);
			holder.right_img = (ImageView) convertView.findViewById(R.id.list_commodity_two_right_iv_img);
			holder.right_name = (TextView) convertView.findViewById(R.id.list_commodity_two_right_tv_name);
			holder.right_brand = (TextView) convertView.findViewById(R.id.list_commodity_two_right_tv_brand);
			holder.right_sell_price = (TextView) convertView.findViewById(R.id.list_commodity_two_right_tv_sell_price);
			holder.right_full_price = (TextView) convertView.findViewById(R.id.list_commodity_two_right_tv_full_price);
			holder.right_discount = (TextView) convertView.findViewById(R.id.list_commodity_two_right_tv_discount);
			
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder)convertView.getTag();
		}
		final ListShowTwoEntity data = datas.get(position);
		if (position == 0) {
			holder.item_main.setPadding(12, 12, 12, 12);
		}else {
			holder.item_main.setPadding(12, 0, 12, 12);
		}
		final ProductListEntity leftEn = (ProductListEntity) data.getLeftEn();
		if (leftEn != null) {
			// 网络图片地址
			String imageUrl = IMAGE_URL_HTTP + leftEn.getImageUrl();
			if (!StringUtil.isNull(leftEn.getImageUrl())) {
				ImageLoader.getInstance().displayImage(imageUrl, holder.left_img, options);
			} else {
				holder.left_img.setImageResource(R.drawable.bg_img_white);
			}
			holder.left_name.setText(leftEn.getName()); //商品名称
			holder.left_sell_price.setText(currStr + leftEn.getSellPrice()); //商品卖价
			
			String full_price = leftEn.getFullPrice(); //商品原价
			if (StringUtil.isNull(full_price) || full_price.equals("0") || full_price.equals("0.00")) {
				holder.left_sell_price.setTextColor(context.getResources().getColor(R.color.text_color_content));
				holder.left_full_price.getPaint().setFlags(0);
				holder.left_full_price.setVisibility(View.GONE);
				holder.left_discount.setVisibility(View.GONE);
			} else {
				holder.left_sell_price.setTextColor(context.getResources().getColor(R.color.text_color_app_bar));
				holder.left_full_price.setText(full_price);
				holder.left_full_price.setVisibility(View.VISIBLE);
				holder.left_full_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
				if (!StringUtil.isNull(leftEn.getDiscount())) {
					holder.left_discount.setVisibility(View.VISIBLE);
					holder.left_discount.setText(leftEn.getDiscount());
				}
			}
			holder.left_main.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					apCallback.setOnClick(leftEn, position, 0);
				}
			});
		}
		final ProductListEntity rightEn = (ProductListEntity) data.getRightEn();
		if (rightEn != null) {
			holder.right_main.setVisibility(View.VISIBLE);
			// 网络图片地址
			String imageUrl = IMAGE_URL_HTTP + rightEn.getImageUrl();
			if (!StringUtil.isNull(rightEn.getImageUrl())) {
				ImageLoader.getInstance().displayImage(imageUrl, holder.right_img, options);
			} else {
				holder.right_img.setImageResource(R.drawable.bg_img_white);
			}
			holder.right_name.setText(rightEn.getName()); //商品名称
			holder.right_sell_price.setText(currStr + rightEn.getSellPrice()); //商品卖价
			
			String full_price = rightEn.getFullPrice(); //商品原价
			if (StringUtil.isNull(full_price) || full_price.equals("0") || full_price.equals("0.00")) {
				holder.right_sell_price.setTextColor(context.getResources().getColor(R.color.text_color_content));
				holder.right_full_price.getPaint().setFlags(0);
				holder.right_full_price.setVisibility(View.GONE);
				holder.right_discount.setVisibility(View.GONE);
			} else {
				holder.right_sell_price.setTextColor(context.getResources().getColor(R.color.text_color_app_bar));
				holder.right_full_price.setText(full_price);
				holder.right_full_price.setVisibility(View.VISIBLE);
				holder.right_full_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
				if (!StringUtil.isNull(rightEn.getDiscount())) {
					holder.right_discount.setVisibility(View.VISIBLE);
					holder.right_discount.setText(rightEn.getDiscount());
				}
			}
			holder.right_main.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					apCallback.setOnClick(rightEn, position, 0);
				}
			});
		}else {
			holder.right_main.setVisibility(View.GONE);
		}
		return convertView;
	}
    
}
