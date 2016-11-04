package com.spshop.stylistpark.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
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
import com.spshop.stylistpark.entity.OrderEntity;
import com.spshop.stylistpark.entity.ProductListEntity;
import com.spshop.stylistpark.entity.UserInfoEntity;
import com.spshop.stylistpark.utils.LangCurrTools;

import java.util.List;

/**
 * 会员订单列表适配器
 */
public class MemberOrderListAdapter extends BaseAdapter {
	
	private static final String IMAGE_URL_HTTP = AppConfig.ENVIRONMENT_PRESENT_IMG_APP;

	private Context context;
	private List<OrderEntity> datas;
	private String currStr;
	//private AdapterCallback adapterCallback;
	private LayoutInflater mInflater;
	private DisplayImageOptions options, avatarOptions;

	public MemberOrderListAdapter(Context context, List<OrderEntity> datas, AdapterCallback adapterCallback) {
		this.context = context;
		this.datas = datas;
		this.currStr = LangCurrTools.getCurrencyValue();
		//this.adapterCallback = adapterCallback;
		this.mInflater = LayoutInflater.from(context);
		options = AppApplication.getDefaultImageOptions();
		avatarOptions = AppApplication.getAvatarOptions();
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
		ImageView iv_avatar;
		TextView tv_order_no, tv_nick, tv_total_user, tv_order_status;

	}

	/** 代表了ListView中的一个item对象 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.item_list_order_member, null);
			holder = new ViewHolder();
			holder.ll_main = (LinearLayout) convertView.findViewById(R.id.item_list_order_member_ll_main);
			holder.ll_goods_lists = (LinearLayout) convertView.findViewById(R.id.member_order_lsit_ll_goods_lists);
			holder.iv_avatar = (ImageView) convertView.findViewById(R.id.item_list_order_member_iv_avatar);
			holder.tv_nick = (TextView) convertView.findViewById(R.id.item_list_order_member_tv_nick);
			holder.tv_order_no = (TextView) convertView.findViewById(R.id.item_list_order_member_tv_order_no);
			holder.tv_total_user = (TextView) convertView.findViewById(R.id.item_list_order_member_tv_total_user);
			holder.tv_order_status = (TextView) convertView.findViewById(R.id.item_list_order_member_tv_order_status);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final OrderEntity data = datas.get(position);
		
		UserInfoEntity infoEn = data.getUserInfo();
		if (infoEn != null) {
			ImageLoader.getInstance().displayImage(infoEn.getUserAvatar(), holder.iv_avatar, avatarOptions);
			holder.tv_nick.setText(infoEn.getUserNick());
		}
		holder.tv_order_no.setText(data.getBuyer());
		holder.tv_order_status.setText(data.getStatusName());
		holder.tv_total_user.setText(data.getPricePaid());
		
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
				TextView tv_curr = (TextView) view.findViewById(R.id.item_goods_vertical_tv_curr);
				tv_curr.setText(currStr);
				TextView tv_price = (TextView) view.findViewById(R.id.item_goods_vertical_tv_price);
				tv_price.setText(goodsLists.get(i).getSellPrice());
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
				holder.ll_goods_lists.addView(view);
			}
		}

		if (position == datas.size()-1) {
			holder.ll_main.setPadding(0, 0, 0, 20);
		}
		return convertView;
	}

}
