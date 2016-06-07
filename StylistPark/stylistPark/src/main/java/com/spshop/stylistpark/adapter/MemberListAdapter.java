package com.spshop.stylistpark.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.spshop.stylistpark.AppApplication;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.entity.MemberEntity;

/**
 * 会员列表适配器
 */
public class MemberListAdapter extends BaseAdapter {
	
	//private static final String IMAGE_URL_HTTP = AppConfig.ENVIRONMENT_PRESENT_IMG_APP;
	public static final int TYPE_CHECK = 1;
	public static final int TYPE_PAY = 2;
	public static final int TYPE_CACEL = 3;
	public static final int TYPE_RECEIVE = 4;
	public static final int TYPE_LOGISTTIC = 5;

	private Context context;
	private List<MemberEntity> datas;
	private AdapterCallback adapterCallback;
	private DisplayImageOptions options;
	private Drawable rank_0, rank_1, rank_2, rank_3, rank_4;

	public MemberListAdapter(Context context, List<MemberEntity> datas, AdapterCallback adapterCallback) {
		this.context = context;
		this.datas = datas;
		this.adapterCallback = adapterCallback;
		options = AppApplication.getImageOptions(90, R.drawable.head_portrait_80);
		
		rank_0 = context.getResources().getDrawable(R.drawable.icon_rank_0);
		rank_1 = context.getResources().getDrawable(R.drawable.icon_rank_1);
		rank_2 = context.getResources().getDrawable(R.drawable.icon_rank_2);
		rank_3 = context.getResources().getDrawable(R.drawable.icon_rank_3);
		rank_4 = context.getResources().getDrawable(R.drawable.icon_rank_4);
		rank_0.setBounds(0, 0, rank_0.getMinimumWidth(), rank_0.getMinimumHeight());
		rank_1.setBounds(0, 0, rank_1.getMinimumWidth(), rank_1.getMinimumHeight());
		rank_2.setBounds(0, 0, rank_2.getMinimumWidth(), rank_2.getMinimumHeight());
		rank_3.setBounds(0, 0, rank_3.getMinimumWidth(), rank_3.getMinimumHeight());
		rank_4.setBounds(0, 0, rank_4.getMinimumWidth(), rank_4.getMinimumHeight());
	}

	public void updateAdapter(List<MemberEntity> datas) {
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

		ImageView iv_top_line, iv_head;
		TextView tv_name, tv_last_login, tv_order_count, tv_order_money;

	}

	/** 代表了ListView中的一个item对象 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.item_list_member, null);
			holder = new ViewHolder();
			holder.iv_top_line = (ImageView) convertView.findViewById(R.id.item_list_member_iv_top_line);
			holder.iv_head = (ImageView) convertView.findViewById(R.id.item_list_member_iv_head);
			holder.tv_name = (TextView) convertView.findViewById(R.id.item_list_member_tv_name);
			holder.tv_last_login = (TextView) convertView.findViewById(R.id.item_list_member_tv_last_login);
			holder.tv_order_count = (TextView) convertView.findViewById(R.id.item_list_member_tv_order_count);
			holder.tv_order_money = (TextView) convertView.findViewById(R.id.item_list_member_tv_order_money);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final MemberEntity data = datas.get(position);
		
		if (position == 0) {
			holder.iv_top_line.setVisibility(View.VISIBLE);
		}else {
			holder.iv_top_line.setVisibility(View.GONE);
		}
		
		holder.tv_name.setText(data.getUserName());
		switch (data.getMemberRank()) {
		case 0:
			holder.tv_name.setCompoundDrawables(null, null, rank_0, null);
			break;
		case 1:
			holder.tv_name.setCompoundDrawables(null, null, rank_1, null);
			break;
		case 2:
			holder.tv_name.setCompoundDrawables(null, null, rank_2, null);
			break;
		case 3:
			holder.tv_name.setCompoundDrawables(null, null, rank_3, null);
			break;
		case 4:
			holder.tv_name.setCompoundDrawables(null, null, rank_4, null);
			break;
		default:
			holder.tv_name.setCompoundDrawables(null, null, rank_0, null);
			break;
		}
		holder.tv_last_login.setText(data.getLastLogin());
		holder.tv_order_count.setText(data.getOrderCount());
		holder.tv_order_money.setText(data.getOrderMoney());
		
		ImageLoader.getInstance().displayImage(data.getHeadImg(), holder.iv_head, options);
		holder.iv_head.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				adapterCallback.setOnClick(data, position, 0);
			}
		});
		return convertView;
	}

}
