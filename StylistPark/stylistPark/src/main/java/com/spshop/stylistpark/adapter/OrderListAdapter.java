package com.spshop.stylistpark.adapter;

import java.util.List;

import android.content.Context;
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
import com.spshop.stylistpark.entity.OrderEntity;
import com.spshop.stylistpark.entity.ProductListEntity;
import com.spshop.stylistpark.utils.LangCurrTools;

/**
 * 我的订单列表适配器
 */
public class OrderListAdapter extends BaseAdapter {
	
	private static final String IMAGE_URL_HTTP = AppConfig.ENVIRONMENT_PRESENT_IMG_APP;
	public static final int TYPE_CHECK = 1;
	public static final int TYPE_PAY = 2;
	public static final int TYPE_CACEL = 3;

	private Context context;
	private List<OrderEntity> datas;
	private AdapterCallback adapterCallback;
	private LayoutInflater mInflater;
	private String currencyStr;
	private DisplayImageOptions options;

	public OrderListAdapter(Context context, List<OrderEntity> datas, AdapterCallback adapterCallback) {
		this.context = context;
		this.datas = datas;
		this.adapterCallback = adapterCallback;
		this.mInflater = LayoutInflater.from(context);
		currencyStr = LangCurrTools.getCurrencyValue(context);
		options = AppApplication.getImageOptions(0, R.drawable.bg_img_icon_120);
	}

	public void updateAdapter(List<OrderEntity> datas) {
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

		LinearLayout ll_main, ll_goods_lists;
		RelativeLayout rl_edit, rl_valid_time;
		ImageView iv_edit_line;
		TextView tv_order_sn, tv_order_status, tv_total_num;
		TextView tv_total_price, tv_pay, tv_delete;

	}

	/** 代表了ListView中的一个item对象 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.item_list_order, null);
			holder = new ViewHolder();
			holder.ll_main = (LinearLayout) convertView.findViewById(R.id.item_list_order_ll_main);
			holder.ll_goods_lists = (LinearLayout) convertView.findViewById(R.id.order_lsit_ll_goods_lists);
			holder.rl_edit = (RelativeLayout) convertView.findViewById(R.id.item_list_order_rl_edit);
			holder.rl_valid_time = (RelativeLayout) convertView.findViewById(R.id.item_list_order_rl_valid_time);
			holder.iv_edit_line = (ImageView) convertView.findViewById(R.id.item_list_order_iv_edit_line);
			holder.tv_order_sn = (TextView) convertView.findViewById(R.id.item_list_order_tv_order_sn);
			holder.tv_order_status = (TextView) convertView.findViewById(R.id.item_list_order_tv_order_status);
			holder.tv_total_num = (TextView) convertView.findViewById(R.id.item_list_order_tv_total_num);
			holder.tv_total_price = (TextView) convertView.findViewById(R.id.item_list_order_tv_total_price);
			holder.tv_pay = (TextView) convertView.findViewById(R.id.item_list_order_tv_pay);
			holder.tv_delete = (TextView) convertView.findViewById(R.id.item_list_order_tv_delete);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final OrderEntity data = datas.get(position);
		holder.tv_order_sn.setText(context.getString(R.string.order_sn, data.getOrderNo()));
		holder.tv_order_status.setText(data.getStatusName());
		
		List<ProductListEntity> goodsLists = data.getGoodsLists();
		if (goodsLists != null) {
			holder.ll_goods_lists.removeAllViews(); //移除之前添加的所有View
			for (int i = 0; i < goodsLists.size(); i++) {
				View view = mInflater.inflate(R.layout.item_goods_img_vertical, holder.ll_goods_lists, false);  
				ImageView img = (ImageView) view.findViewById(R.id.item_goods_vertical_iv_img);
				String imgUrl = IMAGE_URL_HTTP + goodsLists.get(i).getImageUrl();
				ImageLoader.getInstance().displayImage(imgUrl, img, options);
				
				TextView tv_brand = (TextView) view.findViewById(R.id.item_goods_vertical_tv_brand);
				tv_brand.setText(goodsLists.get(i).getBrand());
				TextView tv_price = (TextView) view.findViewById(R.id.item_goods_vertical_tv_price);
				tv_price.setText(currencyStr + goodsLists.get(i).getSellPrice());
				TextView tv_name = (TextView) view.findViewById(R.id.item_goods_vertical_tv_name);
				tv_name.setText(goodsLists.get(i).getName());
				TextView tv_number = (TextView) view.findViewById(R.id.item_goods_vertical_tv_number);
				tv_number.setText("x"+goodsLists.get(i).getTotal());
				TextView tv_attr = (TextView) view.findViewById(R.id.item_goods_vertical_tv_attr);
				String attrStr = goodsLists.get(i).getAttr();
				attrStr = attrStr.replace("\n", " ");
				tv_attr.setText(attrStr);
				
				if (i == goodsLists.size()-1) {
					ImageView iv_line = (ImageView) view.findViewById(R.id.item_goods_vertical_iv_line);
					iv_line.setVisibility(View.GONE);
				}
				view.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						adapterCallback.setOnClick(data, position, TYPE_CHECK);
					}
				});
				holder.ll_goods_lists.addView(view);
			}
		}
		holder.tv_total_num.setText(context.getString(R.string.cart_goods_num, data.getGoodsTotal()));
		holder.tv_total_price.setText(context.getString(R.string.order_pay, currencyStr + data.getPriceTotal()));
		
		if (position == datas.size()-1) {
			holder.ll_main.setPadding(0, 0, 0, 20);
		}
		
		if (data.getStatus() == 1) { //待支付
			holder.rl_edit.setVisibility(View.VISIBLE);
			holder.iv_edit_line.setVisibility(View.VISIBLE);
			holder.tv_pay.setVisibility(View.VISIBLE);
			holder.tv_pay.setText(context.getString(R.string.order_pay_now));
			holder.tv_delete.setText(context.getString(R.string.order_cacel));
		}else {
			holder.rl_edit.setVisibility(View.GONE);
			holder.iv_edit_line.setVisibility(View.GONE);
		}
		
//		holder.rl_valid_time.removeAllViews();
//		if (data.getStatus() == 1) { //待付款
//			String timeStr = TimeUtil.getTextTimeMinuteSecond(context, (data.getValidTime()-System.currentTimeMillis())/1000);
//			if (!StringUtil.isNull(timeStr)) { //启动倒计时
//				final TextView timeView = new TextView(context);
//				timeView.setText(context.getString(R.string.order_time_close, timeStr));
//				timeView.setTextColor(context.getResources().getColor(R.color.text_color_assist));
//				timeView.setTextSize(14);
//				timeView.setId(position);
//				holder.rl_valid_time.addView(timeView);
//				new CountDownTimer(data.getValidTime()-System.currentTimeMillis(), 1000) {
//					
//					@Override
//					public void onTick(long millisUntilFinished) {
//						String timeStr = TimeUtil.getTextTimeMinuteSecond(context, millisUntilFinished/1000);
//						timeView.setText(context.getString(R.string.order_time_close, timeStr));
//					}
//					
//					@Override
//					public void onFinish() {
//						notifyDataSetChanged();
//					}
//				}.start();
//			}else { //交易关闭
//				holder.tv_order_status.setText(context.getString(R.string.order_close));
//				holder.tv_pay.setVisibility(View.GONE);
//				holder.tv_delete.setText(context.getString(R.string.order_delete));
//			}
//		}
		
		holder.ll_main.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				adapterCallback.setOnClick(data, position, TYPE_CHECK);
			}
		});
		holder.tv_pay.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switch (data.getStatus()) {
				case 1: //待付款
					adapterCallback.setOnClick(data, position, TYPE_PAY);
					break;
				}
			}
		});
		holder.tv_delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switch (data.getStatus()) {
				case 1: //待付款
					adapterCallback.setOnClick(data, position, TYPE_CACEL);
					break;
				}
			}
		});
		
		return convertView;
	}

}
