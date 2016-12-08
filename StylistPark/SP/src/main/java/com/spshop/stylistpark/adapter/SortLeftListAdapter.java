package com.spshop.stylistpark.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.spshop.stylistpark.R;
import com.spshop.stylistpark.entity.SortListEntity;
import com.spshop.stylistpark.utils.LangCurrTools;
import com.spshop.stylistpark.utils.LangCurrTools.Language;

import java.util.List;

import static com.spshop.stylistpark.AppApplication.mScale;


/**
 * 商品分类ListView适配器 
 */
public class SortLeftListAdapter extends BaseAdapter{
	
	private Context context;
	private List<SortListEntity> datas;
	private AdapterCallback callback;
	private int index = 0;
	private Language lang;
	
	public SortLeftListAdapter(Context context, List<SortListEntity> datas, AdapterCallback callback) {
		this.context = context;
		this.datas = datas;
		this.callback = callback;
		lang = LangCurrTools.getLanguage();
	}
	
	public void updateAdapter(List<SortListEntity> datas, int index){
		if (datas != null) {
			this.datas = datas;
			this.index = index;
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
		
		RelativeLayout rl_item;
		TextView tv_name;
		
	}
	
	/**代表了ListView中的一个item对象*/
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null){
			convertView = View.inflate(context, R.layout.item_list_sort_left, null);
			
			holder = new ViewHolder();
			holder.rl_item = (RelativeLayout) convertView.findViewById(R.id.list_item_sort_left_rl_main);
			holder.tv_name = (TextView) convertView.findViewById(R.id.list_item_sort_left_tv);
			
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder)convertView.getTag();
		}
		
		final SortListEntity data = datas.get(position);
		
		if (lang == Language.En) {
			holder.tv_name.setTextSize(mScale * 10);
		}else {
			holder.tv_name.setTextSize(mScale * 12);
		}
		holder.tv_name.setText(data.getName());
		
		if (index == position) {
			holder.rl_item.setBackgroundColor(context.getResources().getColor(R.color.ui_bg_color_white));
			holder.tv_name.setTextColor(context.getResources().getColor(R.color.tv_color_status));
		}else {
			holder.rl_item.setBackgroundColor(context.getResources().getColor(R.color.ui_bg_color_nut));
			holder.tv_name.setTextColor(context.getResources().getColor(R.color.tv_color_change));
		}
		holder.rl_item.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				callback.setOnClick(data, position, 1);
			}
		});
		return convertView;
	}

}
