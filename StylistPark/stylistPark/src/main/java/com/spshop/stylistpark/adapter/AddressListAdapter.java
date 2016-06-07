package com.spshop.stylistpark.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spshop.stylistpark.R;
import com.spshop.stylistpark.entity.AddressEntity;


/**
 * 收货地址列表适配器 
 */
public class AddressListAdapter extends BaseAdapter{
	
	public static final int TYPE_SELECT = 1; //选择
	public static final int TYPE_EDIT = 2; //编辑
	public static final int TYPE_DELETE = 3; //删除
	
	private Context context;
	private List<AddressEntity> datas;
	private AdapterCallback apCallback;
	private Drawable select_no, select_yes;
    
	public AddressListAdapter(Context context, List<AddressEntity> datas, AdapterCallback callback) {
		this.context = context;
		this.datas = datas;
		this.apCallback = callback;
		select_no = context.getResources().getDrawable(R.drawable.btn_select_hook_no);
		select_yes = context.getResources().getDrawable(R.drawable.btn_select_hook_yes);
		select_no.setBounds(0, 0, select_no.getMinimumWidth(), select_no.getMinimumHeight());
		select_yes.setBounds(0, 0, select_yes.getMinimumWidth(), select_yes.getMinimumHeight());
	}
	
	public void updateAdapter(List<AddressEntity> datas){
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
		LinearLayout item_main;
		TextView tv_name, tv_phone, tv_email, tv_address;
		TextView tv_default, tv_edit, tv_delete;
	}
	
	/**代表了ListView中的一个item对象*/
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null){
			convertView = View.inflate(context, R.layout.item_list_address, null);
			
			holder = new ViewHolder();
			holder.item_main = (LinearLayout) convertView.findViewById(R.id.list_item_address_ll_main);
			holder.tv_name = (TextView) convertView.findViewById(R.id.list_item_address_tv_name);
			holder.tv_phone = (TextView) convertView.findViewById(R.id.list_item_address_tv_phone);
			holder.tv_email = (TextView) convertView.findViewById(R.id.list_item_address_tv_email);
			holder.tv_address = (TextView) convertView.findViewById(R.id.list_item_address_tv_address);
			holder.tv_default = (TextView) convertView.findViewById(R.id.list_item_address_tv_default);
			holder.tv_edit = (TextView) convertView.findViewById(R.id.list_item_address_tv_edit);
			holder.tv_delete = (TextView) convertView.findViewById(R.id.list_item_address_tv_delete);
			
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder)convertView.getTag();
		}
		final AddressEntity data = datas.get(position);
		
		holder.tv_name.setText(data.getName());
		holder.tv_phone.setText(data.getPhone());
		holder.tv_email.setText(data.getEmail());
		holder.tv_address.setText(data.getAddress());
		
		if (data.getDefaultId() == data.getAddressId()) {
			holder.tv_default.setCompoundDrawables(select_yes, null, null, null);
		}else {
			holder.tv_default.setCompoundDrawables(select_no, null, null, null);
		}
		
		holder.tv_default.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				apCallback.setOnClick(data, position, TYPE_SELECT);
			}
		});
		holder.tv_edit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				apCallback.setOnClick(data, position, TYPE_EDIT);
			}
		});
		holder.tv_delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				apCallback.setOnClick(data, position, TYPE_DELETE);
			}
		});
		
		return convertView;
	}
    
}
