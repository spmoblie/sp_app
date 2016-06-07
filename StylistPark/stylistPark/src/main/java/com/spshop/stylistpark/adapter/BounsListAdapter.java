package com.spshop.stylistpark.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spshop.stylistpark.R;
import com.spshop.stylistpark.entity.BounsEntity;
import com.spshop.stylistpark.utils.StringUtil;

/**
 * 红包列表适配器
 */
public class BounsListAdapter extends BaseAdapter {
	
	private static final int TYPE_SELECT = 1;
	
	private Context context;
	private List<BounsEntity> datas;
	private String selectId;
	private AdapterCallback adapterCallback;

	public BounsListAdapter(Context context, List<BounsEntity> datas, 
			String selectId, AdapterCallback adapterCallback) {
		this.context = context;
		this.datas = datas;
		this.selectId = selectId;
		this.adapterCallback = adapterCallback;
	}

	public void updateAdapter(List<BounsEntity> datas, String selectId) {
		if (datas != null) {
			this.datas = datas;
			this.selectId = selectId;
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

		LinearLayout ll_main;
		ImageView iv_select;
		TextView tv_curr, tv_money, tv_name, tv_limit, tv_status, tv_date;

	}

	/** 代表了ListView中的一个item对象 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.item_list_bouns, null);
			holder = new ViewHolder();
			holder.ll_main = (LinearLayout) convertView.findViewById(R.id.item_list_bouns_ll_main);
			holder.tv_curr = (TextView) convertView.findViewById(R.id.item_list_bouns_tv_currency);
			holder.tv_money = (TextView) convertView.findViewById(R.id.item_list_bouns_tv_money);
			holder.tv_name = (TextView) convertView.findViewById(R.id.item_list_bouns_tv_name);
			holder.tv_limit = (TextView) convertView.findViewById(R.id.item_list_bouns_tv_limit);
			holder.tv_status = (TextView) convertView.findViewById(R.id.item_list_bouns_tv_status);
			holder.tv_date = (TextView) convertView.findViewById(R.id.item_list_bouns_tv_date);
			holder.iv_select = (ImageView) convertView.findViewById(R.id.item_list_bouns_iv_select);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final BounsEntity data = datas.get(position);
		
		if (data.getStatusType() == 1) {
			holder.tv_curr.setTextColor(context.getResources().getColor(R.color.text_color_red_1));
			holder.tv_money.setTextColor(context.getResources().getColor(R.color.text_color_red_1));
		}else {
			holder.tv_curr.setTextColor(context.getResources().getColor(R.color.text_color_assist));
			holder.tv_money.setTextColor(context.getResources().getColor(R.color.text_color_assist));
		}
		holder.tv_curr.setText(data.getCurrency());
		holder.tv_money.setText(data.getBounsMoney());
		holder.tv_name.setText(data.getTypeName());
		holder.tv_limit.setText(data.getBounsLimit());
		holder.tv_date.setText(data.getStartDate() + " - " + data.getEndDate());
		
		holder.tv_status.setText(data.getStatusName());
		if (!StringUtil.isNull(selectId) && selectId.equals(data.getBounsId())) {
			holder.tv_status.setVisibility(View.GONE);
			holder.iv_select.setVisibility(View.VISIBLE);
		}else {
			holder.tv_status.setVisibility(View.VISIBLE);
			holder.iv_select.setVisibility(View.GONE);
		}
		
		holder.ll_main.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (data.getStatusType() == 1) {
					adapterCallback.setOnClick(data, position, TYPE_SELECT);
				}
			}
		});
		return convertView;
	}

}
