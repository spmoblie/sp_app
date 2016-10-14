package com.spshop.stylistpark.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
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
import com.spshop.stylistpark.entity.ProductDetailEntity;
import com.spshop.stylistpark.utils.LangCurrTools;
import com.spshop.stylistpark.utils.StringUtil;
import com.spshop.stylistpark.widgets.MyHorizontalScrollView;
import com.spshop.stylistpark.widgets.MyHorizontalScrollView.ScrollType;
import com.spshop.stylistpark.widgets.MyHorizontalScrollView.ScrollViewListener;

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
	public static final int TYPE_SCROLL = 6;

	private Context context;
	private List<ProductDetailEntity> datas;
	private int scrollPos = -1;
	//private SparseArray<ProductDetailEntity> sa_cart;
	private String currStr;
	private AdapterCallback apCallback;
    private DisplayImageOptions options;
	private LinearLayout.LayoutParams lp;
	
	public CartProductListAdapter(Context context, List<ProductDetailEntity> datas,
					SparseArray<ProductDetailEntity> sa_cart, AdapterCallback callback) {
		this.context = context;
		this.datas = datas;
		//this.sa_cart = sa_cart;
		currStr = LangCurrTools.getCurrencyValue();
		this.apCallback = callback;
        
        options = AppApplication.getDefaultImageOptions();

		lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		lp.width = AppApplication.screenWidth;
	}
	
	public void updateAdapter(List<ProductDetailEntity> datas, SparseArray<ProductDetailEntity> sa_cart){
		if (datas != null) {
			this.datas = datas;
			//this.sa_cart = sa_cart;
			this.scrollPos = -1;
			notifyDataSetChanged();
		}
	}

	public void reset(int scrollPos){
		this.scrollPos = scrollPos;
		notifyDataSetChanged();
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
		MyHorizontalScrollView scroll_hsv;
		LinearLayout ll_left_main;
		RelativeLayout rl_select, rl_minus, rl_add;
		ImageView iv_select, iv_img, iv_minus, iv_add;
		TextView tv_brand, tv_name, tv_attr, tv_curr, tv_price, tv_number, tv_delete;
	}
	
	/**代表了ListView中的一个item对象*/
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.item_list_cart_product, null);

			holder.scroll_hsv = (MyHorizontalScrollView) convertView.findViewById(R.id.item_list_cart_product_hsv_main);
			holder.ll_left_main = (LinearLayout) convertView.findViewById(R.id.item_list_cart_product_ll_left_main);
			holder.rl_select = (RelativeLayout) convertView.findViewById(R.id.item_list_cart_product_rl_select);
			holder.rl_minus = (RelativeLayout) convertView.findViewById(R.id.item_list_cart_product_rl_num_minus);
			holder.rl_add = (RelativeLayout) convertView.findViewById(R.id.item_list_cart_product_rl_num_add);
			holder.iv_select = (ImageView) convertView.findViewById(R.id.item_list_cart_product_iv_select);
			holder.iv_img = (ImageView) convertView.findViewById(R.id.item_list_cart_product_iv_img);
			holder.iv_minus = (ImageView) convertView.findViewById(R.id.item_list_cart_product_iv_num_minus);
			holder.iv_add = (ImageView) convertView.findViewById(R.id.item_list_cart_product_iv_num_add);
			holder.tv_brand = (TextView) convertView.findViewById(R.id.item_list_cart_product_tv_brand);
			holder.tv_name = (TextView) convertView.findViewById(R.id.item_list_cart_product_tv_name);
			holder.tv_attr = (TextView) convertView.findViewById(R.id.item_list_cart_product_tv_attr);
			holder.tv_curr = (TextView) convertView.findViewById(R.id.item_list_cart_product_tv_curr);
			holder.tv_price = (TextView) convertView.findViewById(R.id.item_list_cart_product_tv_price);
			holder.tv_number = (TextView) convertView.findViewById(R.id.item_list_cart_product_tv_number);
			holder.tv_delete = (TextView) convertView.findViewById(R.id.item_list_cart_product_tv_delect);

			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		final ProductDetailEntity data = datas.get(position);

		holder.ll_left_main.setLayoutParams(lp); //适配屏幕宽度
		if (scrollPos != position) { //对非当前滚动项进行复位
			holder.scroll_hsv.smoothScrollTo(0, holder.scroll_hsv.getScrollY());
		}
		holder.scroll_hsv.setOnScrollStateChangedListener(new ScrollViewListener() {
			@Override
			public void onScrollChanged(ScrollType scrollType) {
				switch (scrollType) {
					case TOUCH_SCROLL: //手指拖动滚动
						break;
					case FLING: //滚动
						break;
					case IDLE: //滚动停止
						if (scrollPos != position) { //非同一滚动项
							apCallback.setOnClick(data, position, TYPE_SCROLL); //滚动
						}
						break;
				}
			}
		});

		String imgUrl = data.getImgMinUrl();
		if (!StringUtil.isNull(imgUrl)) {
			ImageLoader.getInstance().displayImage(IMAGE_URL_HTTP + imgUrl, holder.iv_img, options);
		}else {
			holder.iv_img.setImageResource(R.drawable.bg_img_white);
		}
		holder.tv_brand.setText(data.getBrandName());
		holder.tv_name.setText(data.getName());
		holder.tv_curr.setText(currStr);
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
		holder.tv_delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				apCallback.setOnClick(data, position, TYPE_DELETE); //删除
			}
		});
		
		return convertView;
	}
    
}
