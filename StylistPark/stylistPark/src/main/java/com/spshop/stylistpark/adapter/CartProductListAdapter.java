package com.spshop.stylistpark.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.AppConfig;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.entity.ProductDetailEntity;
import com.spshop.stylistpark.utils.StringUtil;
import com.spshop.stylistpark.widgets.slider.SlideView;

import java.util.HashMap;
import java.util.List;


/**
 * 购物车商品列表适配器 
 */
public class CartProductListAdapter extends BaseAdapter{
	
	private static final String IMAGE_URL_HTTP = AppConfig.ENVIRONMENT_PRESENT_IMG_APP;
	public static final int TYPE_SELECT = 1;
	public static final int TYPE_CHECK = 2;
	public static final int TYPE_MINUS = 3;
	public static final int TYPE_ADD = 4;
	public static final int TYPE_DELETE = 5;
	
	private Context context;
	private List<ProductDetailEntity> datas;
	//private HashMap<Integer, ProductDetailEntity> cartHashMap;
	private AdapterCallback apCallback;
    private SlideView slideview;
    private DisplayImageOptions options;
	
	public CartProductListAdapter(Context context, List<ProductDetailEntity> datas, 
			HashMap<Integer, ProductDetailEntity> cartHashMap, AdapterCallback callback) {
		this.context = context;
		this.datas = datas;
		//this.cartHashMap = cartHashMap;
		this.apCallback = callback;
        
        options = AppApplication.getDefaultImageOptions();
	}
	
	public void updateAdapter(List<ProductDetailEntity> datas, HashMap<Integer, ProductDetailEntity> cartHashMap){
		if (datas != null) {
			this.datas = datas;
			//this.cartHashMap = cartHashMap;
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
		RelativeLayout rl_select, rl_minus, rl_add;
		ImageView iv_select, iv_img, iv_minus, iv_add, iv_delete;
		TextView tv_brand, tv_name, tv_attr, tv_price, tv_number;
	}
	
	/**代表了ListView中的一个item对象*/
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null){
			holder = new ViewHolder();
			View view = LayoutInflater.from(context).inflate(R.layout.item_list_cart_product, null);
			slideview = new SlideView(context, context.getResources(), view);
			convertView = slideview;
			
			holder.rl_select = (RelativeLayout) convertView.findViewById(R.id.item_list_cart_product_rl_select);
			holder.rl_minus = (RelativeLayout) convertView.findViewById(R.id.item_list_cart_product_rl_num_minus);
			holder.rl_add = (RelativeLayout) convertView.findViewById(R.id.item_list_cart_product_rl_num_add);
			holder.iv_select = (ImageView) convertView.findViewById(R.id.item_list_cart_product_iv_select);
			holder.iv_img = (ImageView) convertView.findViewById(R.id.item_list_cart_product_iv_img);
			holder.iv_minus = (ImageView) convertView.findViewById(R.id.item_list_cart_product_iv_num_minus);
			holder.iv_add = (ImageView) convertView.findViewById(R.id.item_list_cart_product_iv_num_add);
			holder.iv_delete = (ImageView) convertView.findViewById(R.id.item_list_cart_product_iv_delete);
			holder.tv_brand = (TextView) convertView.findViewById(R.id.item_list_cart_product_tv_brand);
			holder.tv_name = (TextView) convertView.findViewById(R.id.item_list_cart_product_tv_name);
			holder.tv_attr = (TextView) convertView.findViewById(R.id.item_list_cart_product_tv_attr);
			holder.tv_price = (TextView) convertView.findViewById(R.id.item_list_cart_product_tv_price);
			holder.tv_number = (TextView) convertView.findViewById(R.id.item_list_cart_product_tv_number);
			
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		final ProductDetailEntity data = datas.get(position);
		
		String imgUrl = data.getImgMinUrl();
		if (!StringUtil.isNull(imgUrl)) {
			ImageLoader.getInstance().displayImage(IMAGE_URL_HTTP + imgUrl, holder.iv_img, options);
		}else {
			holder.iv_img.setImageResource(R.drawable.bg_img_white);
		}
		holder.tv_brand.setText(data.getBrandName());
		holder.tv_name.setText(data.getName());
		holder.tv_price.setText(data.getSellPrice());
		holder.tv_number.setText(String.valueOf(data.getCartNum()));
		
		String attrStr = data.getAttrStr();
		attrStr = attrStr.replace("\n", " ");
		holder.tv_attr.setText(attrStr);
		
		if (data.getCartNum() > 1) { //可减
			holder.iv_minus.setSelected(true);
		}else {
			holder.iv_minus.setSelected(false);
		}
		if (data.getCartNum() < data.getStockNum()) { //可加
			holder.iv_add.setSelected(true);
		}else {
			holder.iv_add.setSelected(false);
		}
		
		holder.rl_select.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//apCallback.setOnClick(data, position, CODE_SELECT); //选择或取消
			}
		});
		holder.iv_img.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				apCallback.setOnClick(data, position, TYPE_CHECK); //查看
			}
		});
		holder.rl_minus.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				apCallback.setOnClick(data, position, TYPE_MINUS); //减
			}
		});
		holder.rl_add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				apCallback.setOnClick(data, position, TYPE_ADD); //加
			}
		});
		holder.iv_delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				apCallback.setOnClick(data, position, TYPE_DELETE); //删除
			}
		});
		
		return convertView;
	}
    
}
