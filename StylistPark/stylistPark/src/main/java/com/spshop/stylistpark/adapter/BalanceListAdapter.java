package com.spshop.stylistpark.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.spshop.stylistpark.R;
import com.spshop.stylistpark.entity.BalanceDetailEntity;

import java.util.List;

/**
 * 余额明细列表适配器
 */
public class BalanceListAdapter extends BaseAdapter {
	
	private Context context;
	private List<BalanceDetailEntity> datas;
	//private AdapterCallback adapterCallback;

	public BalanceListAdapter(Context context, List<BalanceDetailEntity> datas, AdapterCallback adapterCallback) {
		this.context = context;
		this.datas = datas;
		//this.adapterCallback = adapterCallback;
	}

	public void updateAdapter(List<BalanceDetailEntity> datas) {
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

		TextView tv_title, tv_time, tv_money;

	}

	/** 代表了ListView中的一个item对象 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.item_list_balance, null);
			holder = new ViewHolder();
			holder.tv_title = (TextView) convertView.findViewById(R.id.item_list_balance_tv_title);
			holder.tv_time = (TextView) convertView.findViewById(R.id.item_list_balance_tv_time);
			holder.tv_money = (TextView) convertView.findViewById(R.id.item_list_balance_tv_money);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final BalanceDetailEntity data = datas.get(position);
		holder.tv_title.setText(data.getChangeDesc());
		holder.tv_time.setText(data.getChangeTime());
		
		if (data.getType().equals("-")) {
			holder.tv_money.setTextColor(context.getResources().getColor(R.color.text_color_red_0));
		}else {
			holder.tv_money.setTextColor(context.getResources().getColor(R.color.text_color_title));
		}
		holder.tv_money.setText(data.getType() + data.getChangeMoney());
		
		return convertView;
	}

}
