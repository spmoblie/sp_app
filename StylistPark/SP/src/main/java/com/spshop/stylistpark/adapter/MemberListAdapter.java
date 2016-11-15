package com.spshop.stylistpark.adapter;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.spshop.stylistpark.R;
import com.spshop.stylistpark.entity.MemberEntity;
import com.spshop.stylistpark.utils.OptionsManager;

import java.util.List;

/**
 * 会员列表适配器
 */
public class MemberListAdapter extends BaseAdapter {
	
	public static final int TYPE_CHECK = 1;
	public static final int TYPE_PAY = 2;
	public static final int TYPE_CACEL = 3;
	public static final int TYPE_RECEIVE = 4;
	public static final int TYPE_LOGISTTIC = 5;

	private Context context;
	private List<MemberEntity> datas;
	private AdapterCallback adapterCallback;
	private DisplayImageOptions avatarOptions;

	public MemberListAdapter(Context context, List<MemberEntity> datas, AdapterCallback adapterCallback) {
		this.context = context;
		this.datas = datas;
		this.adapterCallback = adapterCallback;
		avatarOptions = OptionsManager.getInstance().getAvatarOptions();
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

		ImageView iv_top_line, iv_avatar;
		TextView tv_nick, tv_gender, tv_type, tv_order_money;

	}

	/** 代表了ListView中的一个item对象 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.item_list_member, null);
			holder = new ViewHolder();
			holder.iv_top_line = (ImageView) convertView.findViewById(R.id.item_list_member_iv_top_line);
			holder.iv_avatar = (ImageView) convertView.findViewById(R.id.item_list_member_iv_avatar);
			holder.tv_nick = (TextView) convertView.findViewById(R.id.item_list_member_tv_nick);
			holder.tv_gender = (TextView) convertView.findViewById(R.id.item_list_member_tv_gender);
			holder.tv_type = (TextView) convertView.findViewById(R.id.item_list_member_tv_type);
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
		
		holder.tv_nick.setText(data.getMemberNick());
		holder.tv_gender.setText(data.getGender());
		holder.tv_type.setText(data.getMemberType());
		holder.tv_order_money.setText(data.getOrderMoney());

		holder.iv_avatar.setImageResource(R.drawable.default_avatar);
		ImageLoader.getInstance().displayImage(data.getAvatar(), holder.iv_avatar, avatarOptions);
		holder.iv_avatar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				adapterCallback.setOnClick(data, position, 0);
			}
		});
		return convertView;
	}

}
