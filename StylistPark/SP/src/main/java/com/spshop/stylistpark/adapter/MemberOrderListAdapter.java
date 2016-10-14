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
		ImageView iv_avatar, iv_rank;
		TextView tv_nick, tv_add_time, tv_order_status, tv_total_goods, tv_total_user;

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
			holder.iv_rank = (ImageView) convertView.findViewById(R.id.item_list_order_member_iv_rank);
			holder.tv_nick = (TextView) convertView.findViewById(R.id.item_list_order_member_tv_nick);
			holder.tv_add_time = (TextView) convertView.findViewById(R.id.item_list_order_member_tv_add_time);
			holder.tv_order_status = (TextView) convertView.findViewById(R.id.item_list_order_member_tv_order_status);
			holder.tv_total_goods = (TextView) convertView.findViewById(R.id.item_list_order_member_tv_total_goods);
			holder.tv_total_user = (TextView) convertView.findViewById(R.id.item_list_order_member_tv_total_user);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final OrderEntity data = datas.get(position);
		
		UserInfoEntity infoEn = data.getUserInfo();
		if (infoEn != null) {
			ImageLoader.getInstance().displayImage(IMAGE_URL_HTTP + infoEn.getUserAvatar(), holder.iv_avatar, avatarOptions);
			switch (infoEn.getUserRankCode()) {
			case 0:
				holder.iv_rank.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_rank_0));
				break;
			case 1:
				holder.iv_rank.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_rank_1));
				break;
			case 2:
				holder.iv_rank.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_rank_2));
				break;
			case 3:
				holder.iv_rank.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_rank_3));
				break;
			case 4:
				holder.iv_rank.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_rank_4));
				break;
			default:
				holder.iv_rank.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_rank_0));
				break;
			}
			holder.tv_nick.setText(infoEn.getUserNick());
		}
		holder.tv_add_time.setText(data.getBuyer());
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
		holder.tv_total_goods.setText(data.getPriceTotal());
		holder.tv_total_user.setText(data.getPricePaid());
		
		if (position == datas.size()-1) {
			holder.ll_main.setPadding(0, 0, 0, 20);
		}
		return convertView;
	}

}
