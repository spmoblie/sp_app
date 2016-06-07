package com.spshop.stylistpark.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spshop.stylistpark.R;
import com.spshop.stylistpark.entity.LogisticsEntity;


/**
 * 物流信息适配器
 */
public class LogisticsListAdapter extends BaseAdapter{
	
	private Context context;
	private List<LogisticsEntity> datas;
	
	public LogisticsListAdapter(Context context, List<LogisticsEntity> datas) {
		this.context = context;
		this.datas = datas;
	}
	
	public void updateAdapter(List<LogisticsEntity> datas){
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
		
		LinearLayout ll_main;
		ImageView iv_node;
		TextView tv_content, tv_time;
		
	}
	
	/**代表了ListView中的一个item对象*/
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder=null;
		if(convertView==null){
			convertView=View.inflate(context,R.layout.item_list_logistics,null);
			holder=new ViewHolder();
			holder.ll_main = (LinearLayout) convertView.findViewById(R.id.logistics_ll_main);
			holder.iv_node = (ImageView) convertView.findViewById(R.id.logistics_iv_node);
			holder.tv_content = (TextView) convertView.findViewById(R.id.logistics_tv_content);
			holder.tv_time = (TextView) convertView.findViewById(R.id.logistics_tv_time);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		LogisticsEntity data = datas.get(position);
		holder.tv_content.setText(data.getMsgContent());
		holder.tv_time.setText(data.getMsgTime());
		if (position == 0) {
			holder.ll_main.setBackgroundColor(context.getResources().getColor(R.color.ui_bg_color_white));
			holder.iv_node.setImageResource(R.drawable.logistics_line_first);
			holder.tv_content.setTextColor(context.getResources().getColor(R.color.text_color_title));
			holder.tv_time.setTextColor(context.getResources().getColor(R.color.text_color_title));
		}else {
			holder.ll_main.setBackgroundColor(context.getResources().getColor(R.color.ui_bg_color_gray));
			holder.iv_node.setImageResource(R.drawable.logistics_line_last);
			holder.tv_content.setTextColor(context.getResources().getColor(R.color.text_color_assist));
			holder.tv_time.setTextColor(context.getResources().getColor(R.color.text_color_assist));
		}
		return convertView;
	}

}
